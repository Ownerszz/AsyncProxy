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

public class AsyncProxy {
    private final Object instance;

    public AsyncProxy(Object instance) {
        this.instance = instance;
    }


    @RuntimeType
    public Object invoke(@This Object proxy, @Origin Method method, @AllArguments Object[] args) throws Throwable {
        Object toReturn;
        Method toInvoke = instance.getClass().getMethod(method.getName(),method.getParameterTypes());
        if(toInvoke.isAnnotationPresent(RunAsync.class)){
            Class<?> clazz = method.getReturnType();
            Class<?> actualClazz = toInvoke.getReturnType();
            CompletableFuture<Object> result = new CompletableFuture<>();
            Executors.newCachedThreadPool().submit(()->{
                try {
                    result.complete(toInvoke.invoke(instance, args));
                }catch (Exception e){
                    result.completeExceptionally(e);
                }finally {
                    updateProxyToMatchImpl(proxy);
                }
            });
            if(actualClazz.isPrimitive() || actualClazz == String.class){
                return PrimitivesUtil.createWrapperForPrimitiveType(result);
            }
            InvocationResult invocationResult = new InvocationResult(result);
            Class<?> proxyClass = new ByteBuddy()
                    .with(new NamingStrategy.SuffixingRandom("invocationResult"))
                    .subclass(clazz)
                    .method(ElementMatchers.any()).intercept(MethodDelegation.to(invocationResult,InvocationResult.class))
                    .make()
                    .load(clazz.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
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
