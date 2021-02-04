package ownerszz.libraries.async.proxy.core.primitives;

import ownerszz.libraries.async.proxy.core.primitives.replacement.Wrapper;

import java.util.concurrent.CompletableFuture;

/**
 * Utility class that will create the {@link Wrapper wrapper} for us.
 */
public class PrimitivesUtil {
    /**
     * Creates a wrapper for the given future.
     *
     * @param value The future
     * @param <T> Type parameter. Mostly used for Primitives
     * @return {@link Wrapper wrapper}
     */
    public static <T> Wrapper<T> createWrapperForPrimitiveType(CompletableFuture<T> value){
        return new Wrapper<>(value);
    }
}
