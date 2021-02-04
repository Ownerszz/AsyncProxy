package ownerszz.libraries.async.proxy.core.primitives;

import ownerszz.libraries.async.proxy.core.primitives.replacement.Wrapper;

import java.util.concurrent.CompletableFuture;

public class PrimitivesUtil {
    public static <T> Wrapper<T> createWrapperForPrimitiveType(CompletableFuture<T> value){
        return new Wrapper<>(value);
    }
}
