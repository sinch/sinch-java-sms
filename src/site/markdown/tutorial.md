Tutorial
========

The purpose of this document is to present the basic concepts of XMS and how to use it from Java using the CLX XMS SDK.

Interacting with XMS
--------------------

Most interaction with XMS will happen through an _API connection_, given a service username and authorization token the API connection knows how to communicate with the XMS endpoint. Further configuration can be performed on the API connection but in the typical case a service username and token is sufficient.

Once an API connection has been established it is possible to send requests and receive responses. This is typically done by calling a method on the API connection, supplying arguments as necessary, and receiving the response either as a return value or through a supplied callback.

This SDK has a focus on asynchronous operation and all interaction with XMS happens asynchronously. Thus, while synchronous methods are supplied within the library their use is discouraged in most typical applications. Indeed the synchronous methods are simply thin wrappers over the corresponding asynchronous methods!

The arguments passed to a connection method are sometimes very simple, fetching a previously create batch simply requires the batch identifier as argument. Other times the arguments are complicated, for example to create the batch it may be necessary to supply a large number of arguments – specifying destination addresses, the message body, expiry times, and so on. For such complex arguments we use classes whose methods correspond to the different parameters that are relevant for request. For each such class we also provide a builder class that lets you create the objects in a convenient manner.

These abstract concepts are made concrete in the text that follows.

Connection management
---------------------

The first step in using the CLX SDK is to create an API connection object, this object is created from the `ApiConnection` class inside the `com.clxcommunications.xms` package and it describes everything we need in order to talk with the XMS endpoint. The minimal amount of information needed are the service username and the authorization token and these will be provided to you when creating an XMS service.

Assuming we have the service username "myuser" and token "mytoken" then concretely an API connection `conn` is created using a builder as follows

```java
ApiConnection conn = ApiConnection.builder()
    .user("myuser")
    .token("mytoken")
    .build();
```

And we can go on to start the connection – which includes creating connection pools and spinning up worker threads – by calling the `start` method on the connection.

```java
conn.start()
```

Since it is very typical to instantiate the API connection and immediately starting it we provide a `start` method directly in the builder. In code this appears as follows.

```java
ApiConnection conn = ApiConnection.builder()
    .user("myuser")
    .token("mytoken")
    .start();
```

Once started the connection can be used to interact with with XMS in the ways described in the following sections of this tutorial.

Note, it is important to close down the API connection when it is no longer needed, typically when your application is shutting down. This is done by calling the `close` methods of the connection:

```java
conn.close();
```

By default the connection will use `https://api.mblox.com/xms/v1` as XMS endpoint. This can be overridden using the `endpoint` method in the API connection builder. For example, the code

```java
ApiConnection conn = ApiConnection.builder()
    .user("myuser")
    .token("mytoken")
    .endpoint("https://my.test.host:3000/my/base/path")
    .build();
```

would make the connection object believe that the `batches` endpoint is `https://my.test.host:3000/my/base/path/myuser/batches`.

Sending batches
---------------

Creating a batch is typically one of the first things one would like to do when starting to use XMS. When creating a batch we specify, at a minimum, the originating address (typically a short code), one or more destination addresses (typically MSISDNs), and the message body. To send a simple hello world message to one recipient then is accomplished using

```java
MtBatchTextSmsResult result =
    conn.createBatch(ClxApi.batchTextSms()
        .from("12345")
        .addTo("987654321")
        .body("Hello, World!")
        .build())
```

You will notice a few things with this code. We are using a `conn` variable that corresponds to an API connection that we assume has been previously created. We are calling the `createBatch` method on that object with a single argument that describes the batch we wish to create.

Describing the batch is done using an object satisfying the `MtBatchTextSmsCreate` interface. Such objects can most easily be created using the builder returned by `ClxApi.batchTextSms()`. For a batch with a binary body you would similarly describe it using a object satisfying the `MtBatchBinarySmsCreate` interface and typically use `ClxApi.batchBinarySms()` to build such objects.

The return value in this case is a `MtBatchTextSmsResult` object which contains not only the submitted batch information but also information included by XMS, such that the unique batch identifier, the creation time, etc. For example, to simply print the batch identifier we could add the code
```java
System.out.println("Batch id is " + result.id())
```

There is also an asynchronous method corresponding to `createBatch` above, called `createBatchAsync`. This method will return immediately with a `Future<MtBatchTextSmsResult>` return value and takes an extra argument that corresponds to a callback that is invoked when the request actually completes. In the asynchronous setting the above example becomes

```java
callback = new FutureCallback<MtBatchTextSmsResult> {

    // Stuff goes here.

};
Future<MtBatchTextSmsResult> future =
    conn.createBatchAsync(ClxApi.batchTextSms()
        .from("12345")
        .addTo("987654321")
        .body("Hello, World!")
        .build(), callback)
```

Fetching batches
----------------


Custom connections
------------------

For most typical use cases the users of the CLX SDK do not have to worry about the internals of the CLX SDK. However, for some specialized cases the SDK does allow deep access.  In particular, if you have special needs concerning the way the SDK does HTTP traffic you can tell the SDK in the API connection to use a Apache HTTP Async Client of your choice. Do note, however, that in such cases the SDK assumes the client is started up and torn down externally to the API connection.

For example, consider a use case where you have two XMS users that you wish to simultaneously interact with XMS from the same application. It may in this case be beneficial to maintain a single connection pool towards XMS for both users. In practice this may be accomplished with code such at the following.

```java
HttpClient client = new ApiHttpClient(…);
client.start()

ApiConnection conn1 = ApiConnection.builder()
    .httpClient(client)
    .token(token1)
    .user(user1)
    .start();
ApiConnection conn2 = ApiConnection.builder()
    .httpClient(client)
    .token(token2)
    .user(user2)
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
