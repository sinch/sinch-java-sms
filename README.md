# REST API SDK for SMS

This is the Java SDK for the Sinch REST SMS API for sending and receiving single or batch SMS messages. It also supports
scheduled sends, organizing your frequent recipients into groups, and customizing your message for each recipient using
parameterization. It offers an asynchronous Java API that provides convenient access to all the features of the SMS REST
API.

### The library is compatible with Java 8 and later.

## Using

To use this SDK, add it as a dependency

Maven

```xml

<dependency>
    <groupId>com.sinch</groupId>
    <artifactId>sdk-sms</artifactId>
    <version>1.1.0</version>
</dependency>
```

Gradle
```groovy
implementation 'com.sinch:sdk-sms:1.1.0'
```

## Quick Start

### Set up Api Client

```java
String servicePlanId = "SERVICE_PLAN_ID";
String token = "SERVICE_TOKEN";
ApiConnection conn =
    ApiConnection.builder()
        .servicePlanId(servicePlanId)
        .token(token)
        .start();
```

#### Sending Text Message

```java
String sender = "SENDER"; // Optional, must be valid phone number, short code or alphanumeric.
String [] recipients = {"1232323131", "3213123"};
MtBatchTextSmsResult batch =
conn.createBatch(
    SinchSMSApi.batchTextSms()
        .sender(sender)
        .addRecipient(recipients)
        .body("Something good")
        .build());
```

#### Sending Group Message

```java
// Creating simple Group
GroupResult group = conn.createGroup(SinchSMSApi.groupCreate().name("Subscriber").build());

// Adding members (numbers) into the group
conn.updateGroup(
  group.id(), SinchSMSApi.groupUpdate().addMemberInsertion("15418888", "323232").build());

// Sending a message to the group
MtBatchTextSmsResult batch = conn.createBatch(
  SinchSMSApi.batchTextSms()
      .addRecipient(group.id().toString())
      .body("Something good")
      .build());

System.out.println("Successfully sent batch " + batch.id());
```

## Building and installing

This project uses the Maven build tool, and the typical Maven goals are supported. To install the package to your local
Maven repository it therefore is sufficient to execute.

```bash
git clone https://github.com/sinch/sinch-java-sms.git
cd sinch-java-sms    
mvn clean install
```

Build .jar file
```bash
mvn package
```

It will give you `sdk-sms-1.0.7-jar-with-dependencies.jar`

To skip local test
```bash
mvn package -Dmaven.test.skip=true
```

in your terminal. The project will then be compiled and tested before finally being installed in your local repository.

We recommend enabling annotation processing in your IDE https://immutables.github.io/apt.html.

## License

This project is licensed under the Apache License Version 2.0. See the LICENSE.txt file for the license text.
