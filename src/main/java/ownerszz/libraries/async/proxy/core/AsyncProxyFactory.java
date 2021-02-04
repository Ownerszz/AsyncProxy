package ownerszz.libraries.async.proxy.core;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;
import org.objenesis.ObjenesisHelper;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class AsyncProxyFactory {
    /**
     * Creates a proxy that will run methods ending with "Async" async.
     * The actual method will not be invoked.
     *
     * @param instance The instance for which we will create a proxy for.
     * @param <T> Type
     * @throws Exception throws an exception when the supplied type is primitive or the return types of the async methods are primitive.
     * @since 1.1
     */
    public static <T> T createProxy(T instance) throws Exception{
        Class<T> clazz = (Class<T>) instance.getClass();
        validateCLass(clazz);
        AsyncProxy asyncProxy = new AsyncProxy(instance);
        Class<? extends T> proxyClass = new ByteBuddy()
                .with(new NamingStrategy.SuffixingRandom("asyncProxy"))
                .subclass(clazz)
                .method(ElementMatchers.any()).intercept(MethodDelegation.to(asyncProxy,AsyncProxy.class))
                .make()
                .load(clazz.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
        return ObjenesisHelper.newInstance(proxyClass);
    }

    private static void validateCLass(Class clazz) throws Exception{
        StringBuilder stringBuilder = new StringBuilder();
        for (Method method: clazz.getDeclaredMethods()) {
            if( method.getName().endsWith("Async") && method.getReturnType().isPrimitive() && method.getReturnType() != Void.class){
                stringBuilder.append(method.getName()).append(" has a primitive return type. Use Wrapper<").append(method.getReturnType()).append("> instead.").append("\n");
            }
        }
        if(!stringBuilder.toString().isBlank()){
            throw new Exception("Class: " + clazz.getName() + "\n" + stringBuilder.toString());
        }
    }





}
