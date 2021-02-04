
import ownerszz.libraries.async.proxy.core.AsyncProxyFactory;
import org.junit.Before;
import org.junit.Test;
import ownerszz.libraries.async.proxy.core.primitives.replacement.Wrapper;

import java.util.ArrayList;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class AsyncProxyFactoryTest {
    private ClassWithSlowMethods normalInstance;
    private final int WAIT_TIME_MS =500 ;

    @Before
    public void setup(){
        normalInstance = new ClassWithSlowMethods();
        normalInstance.setStringAttribute("test");
        normalInstance.setIntAttribute(123);
    }

    @Test
    public void testCreateProxy() throws Exception{
        ClassWithSlowMethods proxy = AsyncProxyFactory.createProxy(normalInstance);
        assertNotNull(proxy);
    }



    @Test
    public void testGetSlowMethod() throws Exception {
        ClassWithSlowMethods proxy = AsyncProxyFactory.createProxy(normalInstance);
        long start = System.currentTimeMillis();
        Wrapper<String> result = proxy.combineStringAndIntAsync(WAIT_TIME_MS);
        int actualTime = (int) (System.currentTimeMillis() - start);
        assertTrue("method combineStringAndInt must run async", actualTime < WAIT_TIME_MS);
        System.out.println("Time spent before \"getting\" the result: " + actualTime +"ms");
        assertEquals(result.getValue(), normalInstance.combineStringAndInt(WAIT_TIME_MS/1000));
    }

    @Test
    public void testCheckIfEquals() throws Exception{
        ClassWithSlowMethods proxy = AsyncProxyFactory.createProxy(normalInstance);
        long start = System.currentTimeMillis();
        SomeObject result = proxy.calculateObjectAsync(WAIT_TIME_MS);
        int actualTime = (int) (System.currentTimeMillis() - start);
        assertTrue("method calculateObject must run async", actualTime < WAIT_TIME_MS);
        System.out.println("Time spent before \"getting\" the result: " + actualTime +"ms");
        assertTrue(normalInstance.calculateObject(WAIT_TIME_MS/1000).equals(result));
    }

    @Test
    public void testAwaitObject() throws Throwable{
        ClassWithSlowMethods proxy = AsyncProxyFactory.createProxy(normalInstance);
        long start = System.currentTimeMillis();
        SomeObject result = proxy.calculateObjectAsync(WAIT_TIME_MS);
        int actualTime = (int) (System.currentTimeMillis() - start);
        SomeObject normalObject = normalInstance.calculateObject(WAIT_TIME_MS/1000);
        assertTrue("method calculateObject must run async", actualTime < WAIT_TIME_MS);
        System.out.println("Time spent before \"getting\" the result: " + actualTime +"ms");
        assertTrue(normalObject.equals(result));
        result.getRandomInteger();
        String message = "Method invocations worked";
        result.setString(message);
        assertEquals(message, result.getString());
    }

   @Test(expected = Exception.class)
    public void testExceptionThrowingWorks() throws Throwable{
        try {
            ClassWithSlowMethods proxy = AsyncProxyFactory.createProxy(normalInstance);

            long start = System.currentTimeMillis();
            Void result = proxy.throwExceptionAsync(WAIT_TIME_MS);
            int actualTime = (int) (System.currentTimeMillis() - start);
            assertTrue("method throwException must run async", actualTime < WAIT_TIME_MS);
            System.out.println("Time spent before \"getting\" the result: " + actualTime +"ms");
            result.equals(null);
        }catch (Exception e){
            System.out.println("Exception message: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testRunTwoSlowMethods() throws Throwable{
        ClassWithSlowMethods proxy = AsyncProxyFactory.createProxy(normalInstance);
        long start = System.currentTimeMillis();
        SomeObject result1 = proxy.calculateObjectAsync(WAIT_TIME_MS);
        Wrapper<String> result2 = proxy.combineStringAndIntAsync(WAIT_TIME_MS);
        int actualTime = (int) (System.currentTimeMillis() - start);
        assertTrue("methods must run async", actualTime < WAIT_TIME_MS);
        System.out.println("Time spent before \"getting\" the results: " + actualTime +"ms");
        assertEquals(result2.getValue(), result1.getString() + result1.getAnInt());
        assertTrue((int) (System.currentTimeMillis() - start) < WAIT_TIME_MS * 2);
        System.out.println("Actual runtime: " + (System.currentTimeMillis() - start)+"ms");
    }

    @Test
    public void testRunSameSlowMethods() throws Throwable{
        ClassWithSlowMethods proxy = AsyncProxyFactory.createProxy(normalInstance);
        ArrayList<SomeObject> results = new ArrayList<>();
        int resultCount = 100;
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            results.add(proxy.calculateObjectAsync(WAIT_TIME_MS));
        }
        int actualTime = (int) (System.currentTimeMillis() - start);
        assertTrue("method calculateObject must run async; actual time = " + actualTime + "ms", actualTime < WAIT_TIME_MS * 10);
        System.out.println("Time spent before \"getting\" the results: " + actualTime +"ms");
        ArrayList<SomeObject> actualObjects = new ArrayList<>(results);
        assertEquals(actualObjects.size(), resultCount);
        assertTrue(actualObjects.stream().allMatch((someObject -> someObject.getString().equals(normalInstance.getStringAttribute()))));
        assertTrue((int) (System.currentTimeMillis() - start) < WAIT_TIME_MS * resultCount);
        System.out.println("Actual runtime: " + (System.currentTimeMillis() - start) +"ms");
    }

    @Test
    public void testEqualsProxy() throws Throwable{
        ClassWithSlowMethods proxy = AsyncProxyFactory.createProxy(normalInstance);
        SomeObject test = new SomeObject(normalInstance.getStringAttribute(), normalInstance.getIntAttribute());
        long start = System.currentTimeMillis();
        SomeObject result = proxy.calculateObjectAsync(WAIT_TIME_MS);
        test.setObject(result);
        int actualTime = (int) (System.currentTimeMillis() - start);
        assertTrue("method calculateObject must run async", actualTime < WAIT_TIME_MS);
        System.out.println("Time spent before \"getting\" the results: " + actualTime +"ms");
        assertTrue(test.objectEqualsThis());
        SomeObject object = test.getObject();
        assertEquals(test, object);
    }


}
