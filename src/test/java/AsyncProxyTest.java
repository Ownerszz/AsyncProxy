
import core.AsyncProxy;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class AsyncProxyTest {
    private ClassWithSlowMethods normalInstance;
    private final int WAIT_TIME_MS =500 ;

    @Before
    public void setup(){
        normalInstance = new ClassWithSlowMethods();
        normalInstance.setStringAttribute("test");
        normalInstance.setIntAttribute(123);
    }

    @Test
    public void testCreateProxy(){
        IClassWithSlowMethods proxy = AsyncProxy.createProxy(normalInstance);
        assertNotNull(proxy);
    }



    @Test
    public void testGetSlowMethod() throws InterruptedException {
        IClassWithSlowMethods proxy = AsyncProxy.createProxy(normalInstance);
        long start = System.currentTimeMillis();
        Object result = proxy.combineStringAndInt(WAIT_TIME_MS);
        int actualTime = (int) (System.currentTimeMillis() - start);
        assertTrue("method combineStringAndInt must run async", actualTime < WAIT_TIME_MS);
        System.out.println("Time spent before \"getting\" the result: " + actualTime +"ms");
        assertEquals(result, normalInstance.combineStringAndInt(WAIT_TIME_MS/1000));
    }

    @Test
    public void testCheckIfEquals() throws InterruptedException{
        IClassWithSlowMethods proxy = AsyncProxy.createProxy(normalInstance);
        long start = System.currentTimeMillis();
        Object result = proxy.calculateObject(WAIT_TIME_MS);
        int actualTime = (int) (System.currentTimeMillis() - start);
        assertTrue("method calculateObject must run async", actualTime < WAIT_TIME_MS);
        System.out.println("Time spent before \"getting\" the result: " + actualTime +"ms");
        assertEquals(result, normalInstance.calculateObject(WAIT_TIME_MS/1000));
    }

    @Test
    public void testAwaitObject() throws Throwable{
        IClassWithSlowMethods proxy = AsyncProxy.createProxy(normalInstance);
        long start = System.currentTimeMillis();
        Object result = proxy.calculateObject(WAIT_TIME_MS);
        int actualTime = (int) (System.currentTimeMillis() - start);
        SomeObject normalObject = normalInstance.calculateObject(WAIT_TIME_MS/1000);
        assertTrue("method calculateObject must run async", actualTime < WAIT_TIME_MS);
        System.out.println("Time spent before \"getting\" the result: " + actualTime +"ms");
        SomeObject object = AsyncProxy.await(result);
        assertEquals(normalObject, object);
        object.getRandomInteger();
        String message = "Method invocations worked";
        object.setString(message);
        assertEquals(message, object.getString());
    }

   @Test(expected = Exception.class)
    public void testExceptionThrowingWorks() throws Throwable{
        IClassWithSlowMethods proxy = AsyncProxy.createProxy(normalInstance);

        long start = System.currentTimeMillis();
        Object result = proxy.throwException(WAIT_TIME_MS);
        int actualTime = (int) (System.currentTimeMillis() - start);
        assertTrue("method throwException must run async", actualTime < WAIT_TIME_MS);
        System.out.println("Time spent before \"getting\" the result: " + actualTime +"ms");
        try {
            AsyncProxy.await(result);
        }catch (Exception e){
            System.out.println("Exception message: " + e.getMessage());
            throw e;
        }
    }

    @Test
    public void testRunTwoSlowMethods() throws Throwable{
        IClassWithSlowMethods proxy = AsyncProxy.createProxy(normalInstance);

        long start = System.currentTimeMillis();
        Object result1 = proxy.calculateObject(WAIT_TIME_MS);
        Object result2 = proxy.combineStringAndInt(WAIT_TIME_MS);
        int actualTime = (int) (System.currentTimeMillis() - start);
        assertTrue("method calculateObject must run async", actualTime < WAIT_TIME_MS);
        System.out.println("Time spent before \"getting\" the results: " + actualTime +"ms");
        SomeObject object = AsyncProxy.await(result1);
        String string = AsyncProxy.await(result2);
        assertEquals(string, object.getString() + object.getAnInt());
        assertTrue((int) (System.currentTimeMillis() - start) < WAIT_TIME_MS * 2);
        System.out.println("Actual runtime: " + (System.currentTimeMillis() - start)+"ms");
    }

    @Test
    public void testRunSameSlowMethods() throws Throwable{
        IClassWithSlowMethods proxy = AsyncProxy.createProxy(normalInstance);

        ArrayList<Object> results = new ArrayList<>();
        ArrayList<SomeObject> actualObjects = new ArrayList<>();
        int resultCount = 100;
        long start = System.currentTimeMillis();
        for (int i = 0; i < 100; i++) {
            results.add(proxy.calculateObject(WAIT_TIME_MS));
        }
        int actualTime = (int) (System.currentTimeMillis() - start);
        assertTrue("method calculateObject must run async", actualTime < WAIT_TIME_MS);
        System.out.println("Time spent before \"getting\" the results: " + actualTime +"ms");
        for (Object result: results) {
            actualObjects.add(AsyncProxy.await(result));
        }
        assertEquals(actualObjects.size(), resultCount);
        assertTrue(actualObjects.stream().allMatch((someObject -> someObject.getString().equals(normalInstance.getStringAttribute()))));
        assertTrue((int) (System.currentTimeMillis() - start) < WAIT_TIME_MS * resultCount);
        System.out.println("Actual runtime: " + (System.currentTimeMillis() - start) +"ms");
    }

    @Test
    public void testEqualsProxy() throws Throwable{
        IClassWithSlowMethods proxy = AsyncProxy.createProxy(normalInstance);
        SomeObject test = new SomeObject(normalInstance.getStringAttribute(), normalInstance.getIntAttribute());
        long start = System.currentTimeMillis();
        Object result = proxy.calculateObject(WAIT_TIME_MS);
        test.setObject(result);
        int actualTime = (int) (System.currentTimeMillis() - start);
        assertTrue("method calculateObject must run async", actualTime < WAIT_TIME_MS);
        System.out.println("Time spent before \"getting\" the results: " + actualTime +"ms");
        assertTrue(test.objectEqualsThis());
        SomeObject object = test.getObject();
        assertEquals(test, object);
    }

    @Test
    public void testReturnFuture() throws Throwable{
        IClassWithSlowMethods proxy = AsyncProxy.createProxy(normalInstance, true);
        SomeObject test = new SomeObject(normalInstance.getStringAttribute(), normalInstance.getIntAttribute());
        long start = System.currentTimeMillis();
        Future<SomeObject> result = (Future<SomeObject>) proxy.calculateObject(WAIT_TIME_MS);
        int actualTime = (int) (System.currentTimeMillis() - start);
        assertTrue("method calculateObject must run async", actualTime < WAIT_TIME_MS);
        System.out.println("Time spent before \"getting\" the results: " + actualTime +"ms");
        assertEquals(test, result.get());
    }
}
