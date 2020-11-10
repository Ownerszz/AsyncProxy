public class MethodNotAsyncable extends Exception {
    public MethodNotAsyncable() {
    }

    public MethodNotAsyncable(String message) {
        super(message);
    }

    public MethodNotAsyncable(String message, Throwable cause) {
        super(message, cause);
    }

    public MethodNotAsyncable(Throwable cause) {
        super(cause);
    }

    public MethodNotAsyncable(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
