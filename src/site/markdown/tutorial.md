Tutorial
========

The purpose of this document is to present the basic concepts of the CLX Communications HTTP REST Messaging API and how to use it from Java using the HTTP REST Messaging API SDK.

HTTP REST Messaging API basics
------------------------------

HTTP REST Messaging API is a REST API that is provided by CLX Communications for sending and receiving SMS messages. It also provides various other services supporting this task such as managing groups of recipients, tagging, and so forth.

Note, for brevity we will in this document refer to HTTP REST Messaging API as _XMS API_ and the HTTP REST Messaging API service or HTTP endpoint as _XMS_.

A great benefit of the XMS API is that it allows you to easily create and send _batch SMS messages_, that is, SMS messages that can have multiple recipients. When creating a batch message it is possible to use _message templates_, which allows each recipient to receive a personalized message.

To use XMS it is necessary to have a _service plan identifier_ and an _authentication token_, which can be obtained by creating an XMS service plan.

For full documentation of the XMS API please refer to the [REST API documentation site](https://manage.clxcommunications.com/developers/sms/xmsapi.html). The documentation site contains up-to-date information about, for example, status and error codes.

Interacting with XMS through Java
---------------------------------

Using this Java SDK, all interaction with XMS happens through an _API connection_, which can be created using the service plan identifier and authentication token. Further configuration can be performed on the API connection but in the typical case a service plan identifier and authentication token is sufficient.

Once an API connection has been established it is possible to send requests to XMS and receive its responses. This is done by calling a suitable method on the API connection, supplying arguments as necessary, and receiving the response either as a return value or through a supplied callback.

This SDK has a focus on asynchronous operation and all interaction with XMS happens asynchronously. Therefore, while synchronous methods are supplied within the library their use is discouraged in most practical applications.

The arguments passed to a connection method are sometimes very simple, fetching a previously create batch simply requires the batch identifier as argument. Other times the arguments are complicated, for example to create the batch it may be necessary to supply a large number of arguments that specify destination addresses, the message body, expiry times, and so on. For such complex arguments we use classes whose methods correspond to the different parameters that are relevant for the request. For each such class we also provide a builder class that lets you create the objects in a convenient manner. The easiest way to access these builders is through the [`ClxApi`](apidocs/index.html?com/clxcommunications/xms/ClxApi.html) class, which collects all builders that are needed for any of the API calls.

These abstract concepts are made concrete in the text that follows.

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

would make the connection object believe that the `batches` endpoint is `https://my.test.host:3000/my/base/path/v1/myplan/batches`.

The HTTP client used by API connection is by default restricted to only connect to HTTPS through TLSv1.2, it is therefore required to use a version of Java that supports this protocol. All versions of Java since [Java 6u121](http://www.oracle.com/technetwork/java/javase/overview-156328.html#R160_121) support TLSv1.2.

Sending batches
---------------

Creating a batch is typically one of the first things one would like to do when starting to use XMS. To create a batch we must specify, at a minimum, the originating address (typically a short code), one or more destination addresses (typically MSISDNs), and the message body. To send a simple hello world message to one recipient is then accomplished using

```java
MtBatchTextSmsResult result =
    conn.createBatch(ClxApi.batchTextSms()
        .from("12345")
        .addTo("987654321")
        .body("Hello, World!")
        .build())
```

You will notice a few things with this code. We are using a `conn` variable that corresponds to an API connection that we assume has been previously created. We are calling the `createBatch` method on the connection with a single argument that describes the batch we wish to create.

Describing the batch is done using an object satisfying the `MtBatchTextSmsCreate` interface. Such objects can most easily be created using the builder returned by `ClxApi.batchTextSms()`. For a batch with a binary body you would similarly describe it using a object satisfying the `MtBatchBinarySmsCreate` interface and typically use `ClxApi.batchBinarySms()` to build such objects.

The return value in this case is a `MtBatchTextSmsResult` object which contains not only the submitted batch information but also information included by XMS, such that the unique batch identifier, the creation time, etc. For example, to simply print the batch identifier we could add the code

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
    public void completed(BatchDeliveryReport result) {
        System.out.println("Batch id is " + result.id());
    }

    @Override
    public void cancelled() {
        System.out.println("Cancelled send");
    }

};

Future<MtBatchTextSmsResult> future =
    conn.createBatchAsync(ClxApi.batchTextSms()
        .from("12345")
        .addTo("987654321")
        .body("Hello, World!")
        .build(), callback)
```

The callback that we provided to this method will have one of its methods invoked as soon as the communication with XMS has concluded. Which of the methods that is invoked will depend on how the request went. If all went well then the `completed` method will be called with the result as argument. If some form of exception was thrown during the request then the `failed` method will be called with the exception as argument. For more about error handling see sec. [Handling errors](#Handling_errors). Finally, if the request was canceled, for example using `future.cancel(true)`, then the `cancelled` method is called.

Fetching batches
----------------

Handling errors
---------------

Custom connections
------------------

For most typical use cases the users of the XMS SDK do not have to worry about its internals. However, for some specialized cases the SDK does allow deep access.  In particular, if you have special needs concerning the way the SDK does HTTP traffic you can tell the SDK in the API connection to use a Apache HTTP Async Client of your choice. Do note, however, that in such cases the SDK assumes the client is started up and torn down externally to the API connection.

For example, consider a use case where you have two XMS service plans and you wish them to simultaneously interact with XMS from the same application. It may in this case be beneficial to maintain a single connection pool towards XMS for both plans. This requires creating a suitable instance of the [`HttpAsyncClient`](https://hc.apache.org/httpcomponents-asyncclient-dev/httpasyncclient/apidocs/org/apache/http/nio/client/HttpAsyncClient.html) class and using it when initializing the API connection. Note, the XMS SDK provides a concrete client class with a suitable configuration for XMS called [`ApiDefaultHttpAsyncClient`](apidocs/index.html?com/clxcommunications/xms/ApiDefaultHttpAsyncClient.html).

Thus, sharing the default HTTP client between two connections may in practice be accomplished with code such at the following.

```java
HttpClient client = new ApiDefaultHttpAsyncClient();
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

Glossary
--------

Term   | Meaning
-------|--------
MSISDN | Mobile Subscriber ISDN Number
