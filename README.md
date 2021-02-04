# AsyncProxy

This library aims to remove Future<> from the source code and instead replace it with a proxy that can get the value. 

This library aims to make it easy to make async versions of the source class with almost no change (only change is replacing void methods with Void).

This library allows methods to be run async without needing to think about the Thread(s).

<b>Make sure to read the java docs and see the examples.</b>

## Example from test

``` java
        ownerszz.libraries.async.proxy.ClassWithSlowMethods normalInstance = new ownerszz.libraries.async.proxy.ClassWithSlowMethods();
        normalInstance.setStringAttribute("test");
        normalInstance.setIntAttribute(123);
        IClassWithSlowMethods proxy = AsyncProxy.createProxy(normalInstance);
        Object result = proxy.calculateObject(WAIT_TIME_MS);
        // Do some other stuff here
        ownerszz.libraries.async.proxy.SomeObject object = AsyncProxy.await(result);
        object.getRandomInteger();
        object.setString(message);
        assertEquals(message, object.getString());
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

#### Extract interface and update WebHookService (change void methods to return Void)

``` java
import core.MakeAsync;

public interface IWebhookService {
    @MakeAsync
    Object createWebHook(String registeredUrl, String filter, String token) throws Throwable;
    @MakeAsync
    Object getWebHook(String id) throws Throwable;
    @MakeAsync
    Object delete(String id) throws Throwable;
}

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


    public Void delete(String id) throws Throwable {
        RestUtils.delete(RestUtils.formatUrl(resourceUrl + "/{3}", endpointConfig, id), id, Webhook.class);
        return null;
    }
}
```

#### In the test or actual code do
``` java
 @Test
    public void testCreateAndDeleteWebHook() throws Throwable {
        WebhookService actual = getWebhookService(); 
        IWebhookService proxy = AsyncProxy.createProxy(actual);
        Object result = proxy.createWebHook(format("http://{0}:{1}" + ROOT_TRANSACTION,
                "123.456.78.90", "80"),
                "event=" + WebhookConstants.FILTER_NEW_POOL_TX,
                null);
        // Do something else
        Thread.sleep(2000);
        Webhook createdWebhook = AsyncProxy.await(result);
        logger.info("WebHook: " + GsonFactory.getGson().toJson(createdWebhook));
        //You can also use actual.getWebhook(createdWebhook.getId()) because we want the result now
        Webhook webhook1 = AsyncProxy.await(proxy.getWebHook(createdWebhook.getId()));
        //Make sure that we don't exit the test before deleting the webhook
        AsyncProxy.await(proxy.delete(webhook1.getId()));
    }
```
