Tutorial
========

The purpose of this document is to present the basic concepts of the CLX Communications HTTP REST Messaging API and how to use it from Java using the HTTP REST Messaging API SDK.

HTTP REST Messaging API basics
------------------------------

HTTP REST Messaging API is a REST API that is provided by CLX Communications for sending and receiving SMS messages. It also provides various other services supporting this task such as managing groups of recipients, tagging, and so forth.

Note, for brevity we will in this document refer to HTTP REST Messaging API as _XMS API_ and the HTTP REST Messaging API service or HTTP endpoint as _XMS_.

A great benefit of the XMS API is that it allows you to easily create and send _batch SMS messages_, that is, SMS messages that can have multiple recipients. When creating a batch message it is possible to use _message templates_, which allows each recipient to receive a personalized message.

To use XMS it is necessary to have a _service plan identifier_ and an _authentication token_, which can be obtained by creating an XMS service plan.

For full documentation of the XMS API please refer to the [REST API documentation site](https://www.clxcommunications.com/docs/sms/http-rest.html). The documentation site contains up-to-date information about, for example, status and error codes.

Interacting with XMS through Java
---------------------------------

Using this Java SDK, all interaction with XMS happens through an _API connection_, which can be created using the service plan identifier and authentication token. Further configuration can be performed on the API connection but in the typical case a service plan identifier and authentication token is sufficient.

Once an API connection has been established it is possible to send requests to XMS and receive its responses. This is done by calling a suitable method on the API connection, supplying arguments as necessary, and receiving the response either as a return value or through a supplied callback.

This SDK has a focus on asynchronous operation and all interaction with XMS happens asynchronously. Therefore, while synchronous methods are supplied within the library their use is discouraged in most practical applications.

The arguments passed to a connection method are sometimes very simple, fetching a previously create batch simply requires the batch identifier as argument. Other times the arguments are complicated, for example to create the batch it may be necessary to supply a large number of arguments that specify destination addresses, the message body, expiry times, and so on. For such complex arguments we use classes whose methods correspond to the different parameters that are relevant for the request. For each such class we also provide a builder class that lets you create the objects in a convenient manner. The easiest way to access these builders is through the [`ClxApi`](apidocs/index.html?com/clxcommunications/xms/ClxApi.html) class, which collects all builders that are needed for any of the API calls.

In general the terms used in XMS carry through to the Java API with one major exception. The REST API uses the terms _to_ and _from_ to indicate a message originator and message destination, respectively. In the Java API these are instead denoted _recipient_ and _sender_. The cause of this name change is to have less confusing and more idiomatic Java method names.

Connection management
---------------------

The first step in using the XMS SDK is to create an API connection object, this object is instantiated from the [`ApiConnection`](apidocs/index.html?com/clxcommunications/xms/ApiConnection.html) class inside the [`com.clxcommunications.xms`](apidocs/index.html?com/clxcommunications/xms/package-summary.html) package and it describes everything we need in order to talk with the XMS API endpoint. The minimal amount of information needed are the service plan identifier and the authentication token and, as previously mentioned, these will be provided to you when creating an XMS service.

Assuming we have been given the service plan identifier "myplan" and authentication token "mytoken" then an API connection `conn` is created using a builder as follows

```java
ApiConnection conn = ApiConnection.builder()
    .servicePlanId("myplan")
    .token("mytoken")
    .build();
```

And we can go on to start the connection – which includes creating connection pools and spinning up worker threads – by calling the `start` method on the connection:

```java
conn.start()
```

Since it is very common to instantiate the API connection and immediately starting it we provide a `start` method directly in the builder. In code this appears as follows.

```java
ApiConnection conn = ApiConnection.builder()
    .servicePlanId("myplan")
    .token("mytoken")
    .start();
```

Once started the connection can be used to interact with with XMS in the ways described in the following sections of this tutorial.

Note, it is important to close down the API connection when it is no longer needed, typically when your application is shutting down. This is done by calling the `close` method of the connection:

```java
conn.close();
```

By default the connection will use `https://api.clxcommunications.com/xms` as XMS endpoint. This can be overridden using the `endpoint` method in the API connection builder. For example, the code

```java
ApiConnection conn = ApiConnection.builder()
    .servicePlanId("myplan")
    .token("mytoken")
    .endpoint("https://my.test.host:3000/my/base/path")
    .build();
```

would make the connection object believe that the [`batches`](https://www.clxcommunications.com/docs/sms/http-rest.html#batches-endpoint) endpoint is `https://my.test.host:3000/my/base/path/v1/myplan/batches`.

The HTTP client used by the API connection is by default restricted to only connect to HTTPS through TLSv1.2, it is therefore required to use a version of Java that supports this protocol. All versions of Java since [Java 6u121](http://www.oracle.com/technetwork/java/javase/overview-156328.html#R160_121) support TLSv1.2.

Sending batches
---------------

Creating a batch is typically one of the first things one would like to do when starting to use XMS. To create a batch we must specify, at a minimum, the originating address (typically a short code), one or more recipient addresses (typically MSISDNs), and the message body. Sending a simple hello world message to one recipient is then accomplished using

```java
MtBatchTextSmsResult result =
    conn.createBatch(ClxApi.batchTextSms()
        .sender("12345")
        .addRecipient("987654321")
        .body("Hello, World!")
        .build())
```

You will notice a few things with this code. We are using a `conn` variable that corresponds to an API connection that we assume has been previously created. We are calling the `createBatch` method on the connection with a single argument that describes the batch we wish to create.

Describing the batch is done using an object satisfying the `MtBatchTextSmsCreate` interface. Such objects can most easily be created using the builder returned by `ClxApi.batchTextSms()`. For a batch with a binary body you would similarly describe it using a object satisfying the `MtBatchBinarySmsCreate` interface and typically use `ClxApi.batchBinarySms()` to build such objects.

The return value in this case is a `MtBatchTextSmsResult` object that contains not only the submitted batch information but also information included by XMS, such that the unique batch identifier, the creation time, etc. For example, to simply print the batch identifier we could add the code

```java
System.out.println("Batch id is " + result.id())
```

There is also an asynchronous method corresponding to `createBatch` above, called `createBatchAsync`. This method will return immediately with a `Future<MtBatchTextSmsResult>` return value and takes an extra argument that corresponds to a callback that is invoked when the request actually completes. In the asynchronous setting the above example becomes

```java
FutureCallback<MtBatchTextSmsResult> callback = new FutureCallback<MtBatchTextSmsResult> {

    @Override
    public void failed(Exception ex) {
        System.err.println("Failed to send " + ex.getMessage());
    }

    @Override
    public void completed(MtBatchTextSmsResult result) {
        System.out.println("Batch id is " + result.id());
    }

    @Override
    public void cancelled() {
        System.out.println("Cancelled send");
    }

};

Future<MtBatchTextSmsResult> future =
    conn.createBatchAsync(ClxApi.batchTextSms()
        .sender("12345")
        .addRecipient("987654321")
        .body("Hello, World!")
        .build(), callback);
```

The callback that we provided to this method will have one of its methods invoked as soon as the communication with XMS has concluded. Which of the methods that is invoked will depend on how the request went. If all went well then the `completed` method will be called with the result as argument. If some form of exception was thrown during the request then the `failed` method will be called with the exception as argument. For more about error handling see sec. [Handling errors](#Handling_errors). Finally, if the request was canceled, for example using `future.cancel(true)`, then the `cancelled` method is called.

It is not much harder to create a more complicated batch, for example, here we create a parameterized message with multiple recipients and a scheduled send time:

```java
MtBatchTextSmsResult result =
    conn.createBatch(ClxApi.batchTextSms()
        .sender("12345")
        .addRecipient("987654321", "123456789", "555555555")
        .body("Hello, ${name}!")
        .putParameter("name",
            ClxApi.parameterValues()
                .putSubstitution("987654321", "Mary")
                .putSubstitution("123456789", "Joe")
                .defaultValue("valued customer")
                .build())
        .sendAt(OffsetDateTime.of(2016, 12, 20, 10, 0, 0, 0, ZoneOffset.UTC))
        .build())
```

Fetching batches
----------------

If you have a batch identifier and would like to retrieve information concerning that batch then it is sufficient to use the `fetchBatch` or `fetchBatchAsync` method. Thus, if the desired batch identifier is available in the variable `batchId` then one could write

```java
BatchId batchId = // …
MtBatchSmsResult result = conn.fetchBatch(batchId)
System.out.println("Batch id is " + result.id());
```

Note, since `fetchBatch` does not know ahead of time whether the fetched batch is textual or binary it returns a value of the type `MtBatchSmsResult`. This type is the base class of `MtBatchTextSmsResult` and `MtBatchBinarySmsResult` and if the message body is desired then the result would have to be cast to one of these.

The asynchronous case is not much more complicated

```java
FutureCallback<MtBatchSmsResult> callback = new FutureCallback<MtBatchSmsResult> {

    @Override
    public void failed(Exception ex) {
        // …
    }

    @Override
    public void completed(MtBatchSmsResult result) {
        System.out.println("Batch id is " + result.id());
    }

    @Override
    public void cancelled() {
        // …
    }

};

BatchId batchId = // …
Future<MtBatchTextSmsResult> future = conn.fetchBatchAsync(batchId, callback);
```

Listing batches
---------------

Once you have created a few batches it may be interesting to retrieve a list of all your batches. Retrieving listings of batches is done through a _paged result_. This means that a single request to XMS may not retrieve all batches. As a result, when calling the `fetchBatches` method on your connection object it will not simply return a list of batches but rather a [`PagedFetcher`](apidocs/index.html?com/clxcommunications/xms/PagedFetcher.html) object. The paged fetcher in turn can be used to fetch specific pages, iterate over all pages, or directly iterate over all batches while transparently performing necessary page requests.

To limit the number of fetched batches it is also possible to supply a filter that will restrict the fetched batches, for example to those sent after a particular date or having a specific tag or sender.

More specifically, to print the identifier of each batch sent on 2016-12-01 and having the tag "signup_notification", we may write something like the following.

```java
BatchFilter filter = ClxApi.batchFilter()
    .addTag("signup_notification")
    .startDate(LocalDate.of(2016, 12, 1))
    .endDate(LocalDate.of(2016, 12, 2))
    .build();

PagedFetcher<MtBatchSmsResult> fetcher = conn.fetchBatches(filter);

for (MtBatchTextSmsResult batch : fetcher.elements()) {
    System.out.println("Batch ID: " + batch.id());
}
```

Other XMS requests
------------------

We have only shown explicitly how to create, list and fetch batches but the same principles apply to all other XMS calls within the SDK. For example, to fetch a group one could use the previously given instructions for fetching batches and simply use `fetchGroup` with a group identifier.

Handling errors
---------------

Any error that occurs during an API operation will result in an exception being thrown. The exceptions produced specifically by the SDK all inherit from [`ApiException`](apidocs/index.html?com/clxcommunications/xms/ApiException.html) and they are

[`ConcurrentException`](apidocs/index.html?com/clxcommunications/xms/ConcurrentException.html)
:   In synchronous API calls this exception wraps other checked exceptions that may occur during an XMS request, for example if the XMS server response contains invalid JSON then this exception will be thrown and calling `getCause()` on this exception will return a `JsonParseException` object coming from the [Jackson](http://wiki.fasterxml.com/JacksonHome) JSON library.

[`ErrorResponseException`](apidocs/index.html?com/clxcommunications/xms/ErrorResponseException.html)
:   If the XMS server responded with a JSON error object containing an error code and error description.

[`NotFoundException`](apidocs/index.html?com/clxcommunications/xms/NotFoundException.html)
:   If the XMS server response indicated that the desired resource does not exist. In other words, if the server responded with HTTP status 404 Not Found. During a fetch batch or group operation this exception would typically indicate that the batch or group identifier is incorrect.

[`UnauthorizedException`](apidocs/index.html?com/clxcommunications/xms/UnauthorizedException.html)
:   Thrown if the XMS server determined that the authentication token was invalid for the service plan.

[`UnexpectedResponseException`](apidocs/index.html?com/clxcommunications/xms/UnexpectedResponseException.html)
:   If the HTTP response from XMS server had an HTTP status that the SDK did not expect and cannot handle, the complete HTTP response can be retrieved from the exception object using the [`getResponse`](apidocs/com/clxcommunications/xms/UnexpectedResponseException.html#getResponse--) method.

In asynchronous requests `ConcurrentException` is not used and the `failed` method in your callback will receive, for example, `JsonParseException` unwrapped. Thus, if you wish to handle `JsonParseException` in a special way in asynchronous code then the `failed` method in the callback could read

```java
@Override
public void failed(Exception ex) {
    if (ex instanceof JsonParseException) {
        System.err.println("Server sent invalid JSON");
    } else {
        System.err.println("Failed to send " + ex.getMessage());
    }
}
```

while the equivalent synchronous error handling would have to examine the `ConcurrentException` as per

```java
try {
    // Invoke synchronous API connection call here.
} catch (ConcurrentException ex) {
    if (ex.getCause() instanceof JsonParseException) {
        System.err.println("Server sent invalid JSON");
    } else {
        System.err.println("Failed to send " + ex.getCause().getMessage());
    }
} catch (Exception ex) {
    System.err.println("Failed to send " + ex.getMessage());
}
```

Custom connections
------------------

For most typical use cases the users of the XMS SDK do not have to worry about its internals. However, for some specialized cases the SDK does allow deep access.  In particular, if you have special needs concerning the way the SDK does HTTP traffic you can tell the SDK in the API connection to use a Apache HTTP Async Client of your choice. Do note, however, that in such cases the SDK assumes the client is started up and torn down externally to the API connection.

For example, consider a use case where you have two XMS service plans and you wish them to simultaneously interact with XMS from the same application. It may in this case be beneficial to maintain a single connection pool towards XMS for both plans. This requires creating a suitable instance of the [`HttpAsyncClient`](https://hc.apache.org/httpcomponents-asyncclient-dev/httpasyncclient/apidocs/org/apache/http/nio/client/HttpAsyncClient.html) class and using it when initializing the API connection. Note, the XMS SDK provides a concrete client class with a suitable configuration for XMS called [`ApiHttpAsyncClient`](apidocs/index.html?com/clxcommunications/xms/ApiHttpAsyncClient.html).

Thus, sharing the default HTTP client between two connections may in practice be accomplished with code such at the following.

```java
HttpClient client = ApiHttpAsyncClient.of();
client.start()

ApiConnection conn1 = ApiConnection.builder()
    .httpClient(client)
    .token(token1)
    .servicePlanId(spid1)
    .start();
ApiConnection conn2 = ApiConnection.builder()
    .httpClient(client)
    .token(token2)
    .servicePlanId(spid2)
    .start();

// Interact with conn1 and conn2

conn2.close();
conn1.close();
client.close();
```

Do note that the `start` and `stop` methods of the `client` variable are called explicitly. This differs from the typical case where starting and stopping the API connection will also start and stop the underlying HTTP connection.
