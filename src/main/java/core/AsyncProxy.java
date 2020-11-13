package core;

import java.lang.reflect.Proxy;


public class AsyncProxy {
    /**
     * Creates a proxy that contains all methods that need to be done async.
     *
     * The given instance must have 1 interface.
     * The proxy will keep an reference to the instance so states are being kept.
     *
     * Mark all methods as {@link MakeAsync MakeAsync} and make sure all methods return an Object (or Void for void methods).
     * This is to make sure that you read this javadoc
     *
     * @since 1.0
     */
    public static <T> T createProxy(T instance){
        return createProxy(instance, false);
    }

    /**
     * Creates a proxy that contains all methods that need to be done async.
     *
     * The given instance must have 1 interface.
     * The proxy will keep an reference to the instance so states are being kept.
     *
     * Mark all methods as {@link MakeAsync MakeAsync} and make sure all methods return an Object (or Void for void methods).
     * This is to make sure that you read this javadoc
     *
     * @param returnFutureOnInvocation This boolean decides whether we return a Future instead of an proxy that holds the future which allows you to use
     *                                 the {@link #await(Object) await} method.
     * @since 1.0
     */
    public static <T> T createProxy(T instance, boolean returnFutureOnInvocation){
        AsyncProxyInvocationHandler handler = new AsyncProxyInvocationHandler(instance, returnFutureOnInvocation);

        return (T) Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(), instance.getClass().getInterfaces(), handler);
    }

    /**
     * @param object The object must be an {@link Object object} created from invoking the {@link AsyncProxy proxy} methods.
     * Forces the current thread to block until the future is complete.
     *
     * @return An object of type T.
     *
     * @throws Throwable exception
     * @since 1.0
     */
    public static <T> T await(Object object) throws Throwable {
        if (!Proxy.isProxyClass(object.getClass()) && Proxy.getInvocationHandler(object).getClass() != InvocationResult.class){
            throw new IllegalArgumentException("object must be an java.lang.reflect.proxy AND must have an core.InvocationResult as InvocationHandler");
        }
        InvocationResult handler = (InvocationResult) Proxy.getInvocationHandler(object);
        return (T) handler.getResult();
    }

    /**
     * @param object The object must be an {@link Void object} created from invoking the {@link AsyncProxy proxy} methods.
     * Forces the current thread to block until the future is complete.
     *
     * @throws Throwable exception
     * @since 1.0
     */
    public static void await(Void object) throws Throwable {
        if (!Proxy.isProxyClass(object.getClass()) && Proxy.getInvocationHandler(object).getClass() != InvocationResult.class){
            throw new IllegalArgumentException("object must be an java.lang.reflect.proxy AND must have an core.InvocationResult as InvocationHandler");
        }
        InvocationResult handler = (InvocationResult) Proxy.getInvocationHandler(object);
        handler.getResult();
    }

}
