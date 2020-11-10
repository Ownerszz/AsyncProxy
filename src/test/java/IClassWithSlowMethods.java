public interface IClassWithSlowMethods {
    @MakeAsync
    Object combineStringAndInt(int waitTimeInMilliseconds) throws InterruptedException;

    @MakeAsync
    Object calculateObject(int waitTimeInMilliseconds) throws InterruptedException;

    @MakeAsync
    Void throwException(int waitTimeInMilliseconds) throws Exception;
}
