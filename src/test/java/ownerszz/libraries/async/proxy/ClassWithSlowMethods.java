package ownerszz.libraries.async.proxy;

import ownerszz.libraries.async.proxy.core.RunAsync;
import ownerszz.libraries.async.proxy.core.primitives.replacement.Wrapper;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

public class ClassWithSlowMethods {
    private String stringAttribute;
    private int intAttribute;


    public int getIntAttribute() {
        return intAttribute;
    }

    public String getStringAttribute() {
        return stringAttribute;
    }


    @RunAsync
    public Wrapper<String> combineStringAndInt(int waitTimeInMilliseconds) throws InterruptedException {
        Thread.sleep(waitTimeInMilliseconds);
        return Wrapper.of(stringAttribute + intAttribute);
    }
    @RunAsync
    public SomeObject calculateObject(int waitTimeInMilliseconds) throws InterruptedException{
        Thread.sleep(waitTimeInMilliseconds);
        return new SomeObject(stringAttribute, intAttribute);
    }
    @RunAsync
    public Wrapper<Void> throwException(int waitTimeInMilliseconds) throws Exception {
        Thread.sleep(waitTimeInMilliseconds);
        throw new Exception("test");
    }

    @RunAsync
    public Future<SomeObject> calcObject(int waitTimeInMilliseconds) throws Exception{
        Thread.sleep(waitTimeInMilliseconds);
        return CompletableFuture.completedFuture(new SomeObject(stringAttribute,intAttribute));
    }


    public void setStringAttribute(String stringAttribute) {
        this.stringAttribute = stringAttribute;
    }

    public void setIntAttribute(int intAttribute) {
        this.intAttribute = intAttribute;
    }


}
