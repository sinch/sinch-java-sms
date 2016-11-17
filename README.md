XMS REST API SDK
================

This is the Java SDK for the CLX Communications REST API (also called
XMS) for batch SMS messaging. It provides an asynchronous Java API
that provides convenient access to all the features of XMS.

The library is compatible with Java 6 and later.

Using
-----

To use this SDK in your own Java project you can put

```xml
<dependency>
  <groupId>com.clxcommunications</groupId>
  <artifactId>xms</artifactId>
  <version>XYZ</version>
</dependency>
```

in your POM file, with `XYZ` replaced by the desired version.

Building and installing
-----------------------

This project uses the Maven build tool and the typical Maven goals are
supported. To install the package to your local Maven repository it
therefore is sufficient to execute

    $ mvn install

in your terminal. The project will then be compiled and tested before
finally being installed in your local repository.

Developing in Eclipse
---------------------

To import the project into Eclipse it is necessary to install the m2e
plugin and the m2e-apt connector. Both are available at the Eclipse
Marketplace. Before importing the project ensure that automatic
configuration of JDT APT is enabled. The Immutables website has
[good instructions](https://immutables.github.io/apt.html#eclipse)
with screenshots.

After this initial setup development in Eclipse should be reasonably
straight forward.

License
-------

This project is licensed under the Apache License Version 2.0. See the
COPYING file for the license text.
