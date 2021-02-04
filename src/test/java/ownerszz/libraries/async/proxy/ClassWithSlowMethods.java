package ownerszz.libraries.async.proxy;

import ownerszz.libraries.async.proxy.core.primitives.replacement.Wrapper;

public class ClassWithSlowMethods {
    private String stringAttribute;
    private int intAttribute;


    public int getIntAttribute() {
        return intAttribute;
    }

    public String getStringAttribute() {
        return stringAttribute;
    }


    public String combineStringAndInt(int waitTimeInMilliseconds) throws InterruptedException {
        Thread.sleep(waitTimeInMilliseconds);
        return stringAttribute + intAttribute;
    }

    public SomeObject calculateObject(int waitTimeInMilliseconds) throws InterruptedException{
        Thread.sleep(waitTimeInMilliseconds);
        return new SomeObject(stringAttribute, intAttribute);
    }

    public Void throwException(int waitTimeInMilliseconds) throws Exception {
        Thread.sleep(waitTimeInMilliseconds);
        throw new Exception("test");
    }

    public Wrapper<String> combineStringAndIntAsync(int waitTimeInMilliseconds){
        return null;
    }
    public SomeObject calculateObjectAsync(int waitTimeInMilliseconds){
        return null;
    }
    public Void throwExceptionAsync(int waitTimeInMilliseconds) throws Exception {
        return null;
    }

    public void setStringAttribute(String stringAttribute) {
        this.stringAttribute = stringAttribute;
    }

    public void setIntAttribute(int intAttribute) {
        this.intAttribute = intAttribute;
    }


}
