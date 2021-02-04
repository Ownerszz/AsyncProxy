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
        if(method.getName().endsWith("Async")){
            Class<?> clazz = method.getReturnType();
            Method actualMethod = instance.getClass().getMethod(method.getName().substring(0,method.getName().length() - 5),method.getParameterTypes());
            Class<?> actualClazz = actualMethod.getReturnType();
            CompletableFuture<Object> result = new CompletableFuture<>();
            Executors.newCachedThreadPool().submit(()->{
                try {
                    result.complete(actualMethod.invoke(instance, args));
                    updateProxyToMatchImpl(proxy);
                }catch (Exception e){
                    result.completeExceptionally(e);
                }
            });
            if(actualClazz == Void.class){
                return null;
            }

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
            toReturn = instance.getClass().getMethod(method.getName(),method.getParameterTypes()).invoke(instance,args);
        }
        updateProxyToMatchImpl(proxy);
        return toReturn;
    }

    private void updateProxyToMatchImpl(Object proxy) throws Exception {
        Class proxyClass = proxy.getClass().getSuperclass();
        for (Field field:proxyClass.getDeclaredFields()) {
            field.setAccessible(true);
            Object implValue = field.get(instance);
            field.set(proxy,implValue);
        }

    }

}
