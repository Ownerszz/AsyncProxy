import java.util.Objects;
import java.util.Random;

public class SomeObject {
    private String string;
    private int anInt;
    private Object object;


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
    public void setObject(Object o){
        object = o;
    }

    public boolean objectEqualsThis(){
        return object.equals(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SomeObject object = (SomeObject) o;
        return anInt == object.anInt &&
                Objects.equals(string, object.string);
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
        return AsyncProxy.await(object);
    }
}
