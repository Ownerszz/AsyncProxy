package core;

import java.lang.reflect.InvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;

public class AsyncProxyInvocationHandler implements InvocationHandler  {
    private final Object implementation;
    private final boolean returnFuture;
    public AsyncProxyInvocationHandler(Object instance) {
        implementation = instance;
        this.returnFuture = false;
    }
    public AsyncProxyInvocationHandler(Object instance, boolean returnFuture) {
        implementation = instance;
        this.returnFuture = returnFuture;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (!method.isAnnotationPresent(MakeAsync.class)){
            throw new MethodNotAsyncable("Method is not marked as \"MakeAsync\"");
        }
        CompletableFuture<Object> completableFuture = new CompletableFuture<>();

        Executors.newCachedThreadPool().submit(() -> {
            try {
                completableFuture.complete(method.invoke(implementation, args));
            } catch (Exception e) {
                completableFuture.completeExceptionally(e.getCause());
            }

        });
        if (returnFuture){
            return completableFuture;
        }else {
            return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), method.getReturnType().getInterfaces(), new InvocationResult(completableFuture));

        }
    }
}
