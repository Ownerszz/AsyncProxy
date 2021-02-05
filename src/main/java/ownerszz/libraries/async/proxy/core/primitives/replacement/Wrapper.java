package ownerszz.libraries.async.proxy.core.primitives.replacement;

import java.util.concurrent.CompletableFuture;

/**
 * Wrapper used for Primitive types.
 * @param <T>
 */
public  class Wrapper<T> {
   private CompletableFuture<T> value;
   public Wrapper(CompletableFuture<T> value) {
      this.value = value;
   }

   /**
    * Get the value that the wrapper is wrapping.
    * @return the value of type T
    */
   public T getValue(){
      try {
         return value.get();

      }catch (Exception e){
         throw new RuntimeException(e.getMessage());
      }
   }

   /**
    * Returns a wrapper based on the given value;
    * @param value The value to wrap
    * @param <T> Type param
    * @return {@link Wrapper a Wrapper of type T}
    */
   public static <T> Wrapper<T> of(T value){
      CompletableFuture<T> completedFuture = new CompletableFuture<>();
      completedFuture.complete(value);
      return new Wrapper<>(completedFuture);
   }
}
