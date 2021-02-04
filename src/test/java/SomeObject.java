import ownerszz.libraries.async.proxy.core.AsyncProxyFactory;

import java.util.Objects;
import java.util.Random;

public class SomeObject {
    private String string;
    private int anInt;
    private SomeObject object;


    public SomeObject(String stringAttribute, int intAttribute) {
        string = stringAttribute;
        anInt = intAttribute;
    }

    public int getRandomInteger(){
        Random random = new Random();
        return random.nextInt();
    }

    public void setString(String s){
        string = s;
    }
    public void setObject(SomeObject o){
        object = o;
    }

    public boolean objectEqualsThis(){
        return object.equals(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        SomeObject object = (SomeObject) o;
        return anInt == object.getAnInt() &&
                Objects.equals(string, object.getString());
    }

    @Override
    public int hashCode() {
        return Objects.hash(string, anInt);
    }

    public String getString() {
        return string;
    }
    public int getAnInt() {
        return anInt;
    }
    public SomeObject getObject() throws Throwable {
        return object;
    }
}
