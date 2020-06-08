# REST API SDK for SMS

Sinch Java SMS helper library, moved from https://github.com/clxcommunications/sdk-xms-java

This is the Java SDK for the Sinch REST SMS API for sending and receiving single or batch SMS messages. It also
supports scheduled sends, organizing your frequent recipients into
groups, and customizing your message for each recipient using
parameterization. It offers an asynchronous Java API that provides
convenient access to all the features of the SMS REST API.

The library is compatible with Java 8 and later.

## Using

To use this SDK in your own Java project you can put

```xml
<dependency>
  <groupId>com.sinch</groupId>
  <artifactId>sdk-xms</artifactId>
  <version>XYZ</version>
</dependency>
```

in your POM file, with `XYZ` replaced by the desired version.

## Building and installing

This project uses the Maven build tool and the typical Maven goals are
supported. To install the package to your local Maven repository it
therefore is sufficient to execute

    $ mvn clean install

in your terminal. The project will then be compiled and tested before
finally being installed in your local repository.

Note that Java 8 is required to build and run the test suite. The
installed JAR file is Java 6 compatible, however.

## Developing in Eclipse

To import the project into Eclipse it is necessary to install the m2e
plugin and the m2e-apt connector. Both are available at the Eclipse
Marketplace. Before importing the project ensure that automatic
configuration of JDT APT is enabled. The Immutables website has
[good instructions](https://immutables.github.io/apt.html#eclipse)
with screenshots.

After this initial setup development in Eclipse should be reasonably
straight forward.

## License

This project is licensed under the Apache License Version 2.0. See the
LICENSE.txt file for the license text.
