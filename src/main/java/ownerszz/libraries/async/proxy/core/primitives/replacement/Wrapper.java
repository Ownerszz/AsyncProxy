package ownerszz.libraries.async.proxy.core.primitives.replacement;

import java.util.concurrent.CompletableFuture;

public  class Wrapper<T> {
   private CompletableFuture<T> value;
   public Wrapper(CompletableFuture<T> value) {
      this.value = value;
   }

   public T getValue(){
      try {
         return value.get();

      }catch (Exception e){
         throw new RuntimeException(e.getMessage());
      }
   }
}
