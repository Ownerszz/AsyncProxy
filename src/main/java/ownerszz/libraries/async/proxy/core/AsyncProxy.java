package ownerszz.libraries.async.proxy.core;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.implementation.bind.annotation.*;
import net.bytebuddy.matcher.ElementMatchers;
import org.objenesis.ObjenesisHelper;
import ownerszz.libraries.async.proxy.core.primitives.PrimitivesUtil;
import ownerszz.libraries.dependency.injection.core.DependencyInstanstatior;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncProxy {
    private final Object instance;

    public AsyncProxy(Object instance) {
        this.instance = instance;
    }


    @RuntimeType
    public Object invoke(@This Object proxy, @Origin Method method, @AllArguments Object[] args) throws Throwable {
        Object toReturn;
        Class<?> instanceClass = instance.getClass();
        boolean ok = false;
        while (!ok){
            if(instanceClass.getSimpleName().contains("$")){
                if(instanceClass.getSuperclass() != null){
                    instanceClass = instanceClass.getSuperclass();
                }else {
                    instanceClass = instanceClass.getInterfaces()[0];
                }
            }else {
                ok= true;
            }
        }

        Method toInvoke = instanceClass.getMethod(method.getName(),method.getParameterTypes());
        if(toInvoke.isAnnotationPresent(RunAsync.class)){
            Class<?> actualClazz = toInvoke.getReturnType();
            CompletableFuture<Object> result = new CompletableFuture<>();
            Executors.newCachedThreadPool().submit(()->{
                try {
                    if(Future.class.isAssignableFrom(actualClazz)){
                        Future task = (Future) toInvoke.invoke(instance, args);
                        result.complete(task.get());
                    }else {
                        result.complete(toInvoke.invoke(instance, args));
                    }
                }catch (Exception e){
                    result.completeExceptionally(e);
                }finally {
                    updateProxyToMatchImpl(proxy);
                }
            });
            if(actualClazz.isPrimitive() || actualClazz == String.class){
                return PrimitivesUtil.createWrapperForPrimitiveType(result);
            }else if(Future.class.isAssignableFrom(actualClazz)){
                return result;
            }
            InvocationResult invocationResult = new InvocationResult(result);
            Class<?> proxyClass = new ByteBuddy()
                    .with(new NamingStrategy.SuffixingRandom("invocationResult"))
                    .subclass(actualClazz)
                    .method(ElementMatchers.any()).intercept(MethodDelegation.to(invocationResult,InvocationResult.class))
                    .make()
                    .load(actualClazz.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                    .getLoaded();
            toReturn = ObjenesisHelper.newInstance(proxyClass);
        }else {
            toReturn = toInvoke.invoke(instance,args);
        }
        updateProxyToMatchImpl(proxy);
        return toReturn;
    }

    private void updateProxyToMatchImpl(Object proxy) throws RuntimeException {
        Class proxyClass = proxy.getClass().getSuperclass();
        for (Field field:proxyClass.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                Object implValue = field.get(instance);
                field.set(proxy,implValue);
            }catch (Exception e){
                throw new RuntimeException(e);
            }
        }

    }

}
