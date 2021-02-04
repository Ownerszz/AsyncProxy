# AsyncProxy

This library aims to remove Future<> from the source code and instead replace it with a proxy that can get the value. 

This library aims to make it easy to make async versions of the source class with almost no change (only change is replacing void methods with Void).

This library allows methods to be run async without needing to think about the Thread(s).

<b>Make sure to read the java docs and see the examples/tests.</b>

## Example from test

``` java
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
```

## Example in production

#### Initial class
``` java
public final class WebhookService extends AbstractService {

    private static final String ABSOLUTE_PATH = "/{0}/{1}/{2}/hooks";

    private WebhookService(EndpointConfig endpointConfig) {
        super(endpointConfig);
    }

    @Override
    protected String getAbsolutePath() {
        return ABSOLUTE_PATH;
    }

    public Webhook createWebHook(String registeredUrl, String filter, String token) throws Throwable {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("url", registeredUrl);
        jsonObject.addProperty("filter", filter);
        jsonObject.addProperty("token", token);
        return RestUtils.post(RestUtils.formatUrl(resourceUrl, endpointConfig, null), jsonObject.toString(), null, Webhook.class);
    }


    public Webhook getWebHook(String id) throws Throwable {
        return RestUtils.get(RestUtils.formatUrl(resourceUrl + "/{3}", endpointConfig, id), id, Webhook.class);
    }


    public void delete(String id) throws Throwable {
        RestUtils.delete(RestUtils.formatUrl(resourceUrl + "/{3}", endpointConfig, id), id, Webhook.class);
    }
}
```
#### Add an Async version of every method that you want async
...........

#### In the test or actual code do
``` java
 @Test
    public void testCreateAndDeleteWebHook() throws Throwable {
        WebhookService actual = getWebhookService(); 
        WebhookService proxy = AsyncProxy.createProxy(actual);
        Webhook result = proxy.createWebHookAsync(format("http://{0}:{1}" + ROOT_TRANSACTION,
                "123.456.78.90", "80"),
                "event=" + WebhookConstants.FILTER_NEW_POOL_TX,
                null);
        // Do something else
        Thread.sleep(2000);
        logger.info("WebHook: " + GsonFactory.getGson().toJson(result));
        
        Webhook webhook1 = proxy.getWebHookAsync(createdWebhook.getId());
        //Make sure that we don't exit the test before deleting the webhook
        proxy.deleteAsync(webhook1.getId());
    }
```
