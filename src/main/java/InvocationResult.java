import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InvocationResult implements InvocationHandler {
    private Object result;
    private CompletableFuture<Object> completableFuture;

    protected InvocationResult(CompletableFuture<Object> completableFuture) throws Throwable{
        this.completableFuture = completableFuture;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        setResult();
        /*Hijack the equals method and check if the result equals the argument OR vice versa
         *  (args[0] can be another proxy so we want to make sure that we can allow it to hijack it as well)
        */
        if ("equals".equalsIgnoreCase(method.getName())){
            return result.equals(args[0]) || args[0].equals(result);
        }else if ("tostring".equalsIgnoreCase(method.getName())){
            return result.toString();
        }else if("hashcode".equalsIgnoreCase(method.getName())){
            return result.hashCode();
        } else {
            return method.invoke(result, args);
        }
    }

    /**
     * Forces the current thread to block until the future is complete and sets the result.
     * @throws Throwable
     * @since 1.0
     */
    private void setResult() throws Throwable {
        if(result == null){
            result = completableFuture.get();
        }else {
            completableFuture = null;
        }
    }
    /**
     * Forces the current thread to block until the future is complete and sets the result.
     * @return result
     * @throws Throwable
     * @since 1.0
     */
    protected Object getResult() throws Throwable {
        setResult();
        return result;
    }
}
