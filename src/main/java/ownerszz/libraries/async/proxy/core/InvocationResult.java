package ownerszz.libraries.async.proxy.core;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.This;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.concurrent.CompletableFuture;


public class InvocationResult {
    private CompletableFuture<Object> result;

    protected InvocationResult(CompletableFuture<Object> completableFuture) throws Throwable{
        this.result = completableFuture;
    }

    @RuntimeType
    public Object invoke(@This Object proxy, @Origin Method method, @AllArguments Object[] args) throws Throwable {
        Object toReturn = method.invoke(result.get(),args);
        updateProxyToMatchImpl(proxy);
        return toReturn;
    }
    private void updateProxyToMatchImpl(Object proxy) throws Exception {
        Class proxyClass = proxy.getClass().getSuperclass();
        for (Field field:proxyClass.getDeclaredFields()) {
            field.setAccessible(true);
            Object implValue = field.get(result.get());
            field.set(proxy,implValue);
        }

    }

}
