public class ClassWithSlowMethods implements IClassWithSlowMethods {
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

    public void setStringAttribute(String stringAttribute) {
        this.stringAttribute = stringAttribute;
    }

    public void setIntAttribute(int intAttribute) {
        this.intAttribute = intAttribute;
    }


}
