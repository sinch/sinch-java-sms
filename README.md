# REST API SDK for SMS and MMS

This is the Java SDK for the Sinch REST SMS API for sending and receiving single or batch SMS/MMS messages. It also supports
scheduled sends, organizing your frequent recipients into groups, and customizing your message for each recipient using
parameterization. It offers an asynchronous Java API that provides convenient access to all the features of the SMS REST
API.

API reference: https://developers.sinch.com/docs/sms/api-reference

### The library is compatible with Java 8 and later.

## Using

To use this SDK, add it as a dependency

Maven

```xml

<dependency>
    <groupId>com.sinch</groupId>
    <artifactId>sdk-sms</artifactId>
    <version>2.2.1</version>
</dependency>
```

Gradle
```groovy
implementation 'com.sinch:sdk-sms:2.2.1'
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

#### Sending MMS Message, requires version 2.x

```java
String sender = "SENDER"; // Optional, must be valid phone number, short code or alphanumeric.
String [] recipients = {"1232323131", "3213123"};
MtBatchMmsResult batch =
conn.createBatch(
    SinchSMSApi.batchMms()
        .sender(sender)
        .addRecipient(recipients)
        .body(SinchSMSApi.mediaBody().url("https://en.wikipedia.org/wiki/Sinch_(company)#/media/File:Sinch_LockUp_RGB.png").message("Hello, world!").build())
        .build());
```

Please visit https://developers.sinch.com/docs/sms/getting-started/java/send-sms-with-java/ for more detailed instructions.

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

It will give you `sdk-sms-2.2.1-jar-with-dependencies.jar`

To skip local test
```bash
mvn package -Dmaven.test.skip=true
```

in your terminal. The project will then be compiled and tested before finally being installed in your local repository.

We recommend enabling annotation processing in your IDE https://immutables.github.io/apt.html.

## Sinch REST SMS API 1.x to 2.x Migration guide

In version 2.x MMS messages support was added into the library, which will require code changes if you want to upgrade.

1. Methods of ApiConnection for fetching or cancelling batches are returning MtBatchResult now, which you could cast to 
either MtBatchMmsResult or MtBatchSmsResult depending on message type;

2. RecipientDeliveryReport which is returned by ApiConnection.fetchDeliveryReport, you could cast to
RecipientDeliveryReportSms or RecipientDeliveryReportMms depending on message type;

3. BatchDeliveryReport.Status is not inner class anymore and could be used as Status;

## License

This project is licensed under the MIT License. See the LICENSE file for the license text.
