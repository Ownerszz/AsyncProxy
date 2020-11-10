
import java.lang.reflect.InvocationHandler;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class AsyncProxyInvocationHandler implements InvocationHandler  {
    private final Object implementation;
    public AsyncProxyInvocationHandler(Object instance) {
        implementation = instance;
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
        return Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), method.getReturnType().getInterfaces(), new InvocationResult(completableFuture));

    }
}
