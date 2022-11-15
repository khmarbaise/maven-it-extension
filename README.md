<!---
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
# Integration Testing Framework Extension

[![Apache License, Version 2.0, January 2004](https://img.shields.io/github/license/apache/maven.svg?label=License)][license]
[![Build Status](https://cloud.drone.io/api/badges/khmarbaise/maven-it-extension/status.svg)](https://cloud.drone.io/khmarbaise/maven-it-extension)
[![Site](https://github.com/khmarbaise/maven-it-extension/workflows/SitePublishing/badge.svg)][published-site]
[![Issues](https://img.shields.io/github/issues/khmarbaise/maven-it-extension)](https://github.com/khmarbaise/maven-it-extension/issues)
[![Issues Closed](https://img.shields.io/github/issues-closed/khmarbaise/maven-it-extension)](https://github.com/khmarbaise/maven-it-extension/issues?q=is%3Aissue+is%3Aclosed)
[![codecov](https://codecov.io/gh/khmarbaise/maven-it-extension/branch/master/graph/badge.svg?token=DLPH6544C0)](https://codecov.io/gh/khmarbaise/maven-it-extension)

<!---
 -green will be done for 1.0.0 the first time.
 -orange used for SNAPSHOT
-->
| Release        | Maven Central                                       | Release Notes                                                     |                           Users Guide                          |
| -------------- | --------------------------------------------------- | ----------------------------------------------------------------: | -------------------------------------------------------------: |
| 0.11.0          | [![Maven Central][shield-central]][central-search]  | [![PDF][stable-pdf-releasenotes]][releasenotes-pdf]               | [![PDF][stable-pdf-usersguide]][usersguide-pdf]                |
|                |                                                     | [![HTML][stable-html-release]][releasenotes-html]                 | [![HTML][stable-html-usersgude]][usersguide-html]              |
| 0.12.0-SNAPSHOT | ![Maven Central][not-available-in-central]          | [![PDF][unstable-pdf-releasenotes]][unstable-releasenotes-pdf]    | [![PDF][unstable-pdf-usersguide]][unstable-usersguide-pdf]     |
|                |                                                     | [![HTML][unstable-html-releasenotes]][unstable-releasenotes-html] | [![HTML][unstable-html-usersguide]][unstable-usersguide-html]  |


# State
The project is in an early state but already being useful and can be used for real testing. 
The project release is available via [Central repository][central-search] (only the given version as show in above table) 
which makes it easy for you to use it out without the need to compile the code (You can of course do that if you like) 
yourself.

# General Overview
The basic thing about integration testing of Maven Plugins / Maven Extensions etc. is currently that the existing 
solutions are not a very concise and comprehensive which is based on the long development history of the Apache Maven
project. There are a lot of different approaches done over the time but from my perspective they all lack one thing: 
Simplicity. More detailed reasons etc. can be read in the [Background Guide][background-html]. This is the reason why 
I think it's time to come up with a more modern setup and started this project.

# The Basic Idea
The basic idea rest upon the option to write custom [extension with JUnit Jupiter][junit-jupiter-extension]
for [JUnit Jupiter][junit-jupiter-extension] testing framework which makes it very easy to get things done.

So in general the whole Integration Testing Framework in its core (itf-jupiter-extension) is a 
[JUnit Jupiter extension][junit-jupiter-extension] which delivers supplemental support for a testing
framework in general and in particular for using integration tests.
Of course there is a lot of convenience integrated into it to make integration testing of Maven plugins easier.

# Quick Start

## The General Requirements
The requirements to write integration tests with the integration testing framework are the following:

* JDK8+
* Apache Maven 3.8.4 or above.

## The Maven Configuration

The first thing before you can run integration tests is to add the following dependencies
(as minimum) to your `pom.xml`:
```xml
<project..>
   ...
  <dependencies>
   <dependency>
      <groupId>org.junit.jupiter</groupId>
      <artifactId>junit-jupiter-engine</artifactId>
      <scope>test</scope>
    </dependency>
    <dependency>
      <groupId>com.soebes.itf.jupiter.extension</groupId>
      <artifactId>itf-jupiter-extension</artifactId>
      <version>0.11.0</version>
      <scope>test</scope>
    </dependency>
    <dependency>
     <groupId>com.soebes.itf.jupiter.extension</groupId>
     <artifactId>itf-assertj</artifactId>
     <version>0.11.0</version>
     <scope>test</scope>
    </dependency>
  </dependencies>
  ..
</project..>
```

The first dependency `org.junit.jupiter:junit-jupiter-engine` is needed for
JUnit Jupiter (We recommend the most recent version) working and the second
one `com.soebes.itf.jupiter.extension:itf-jupiter-extension` is the extension to
get support for running in general integration tests as described later.
Finally, you have to add `com.soebes.itf.jupiter.extension:itf-assertj` which
contains custom assertions based on  [AssertJ][assertj].

The next part is to add `itf-maven-plugin` in your `pom.xml` file like this
which handles the first part which is involved:

```xml
<project...>
  ..
  <build>
    ..
    <plugin>
      <groupId>com.soebes.itf.jupiter.extension</groupId>
      <artifactId>itf-maven-plugin</artifactId>
      <version>0.11.0</version>
      <executions>
        <execution>
          <id>installing</id>
          <phase>pre-integration-test</phase>
          <goals>
            <goal>install</goal>
            <goal>resources-its</goal>
          </goals>
        </execution>
      </executions>
    </plugin>
    ..
  </build>
  ..
</project...>
```
The given version of `itf-jupiter-extension` as well as `itf-maven-plugin` should always be the
most recent version which is available via [Central repository][central-search].

Based on the concept we would like to run integration identified by the naming convention we have
to add the [maven-failsafe-plugin][maven-failsafe-plugin] to the `pom.xml` like this (This 
will execute the integration tests which checks the functionality; The second part which is involved).
```xml
<project...>
  ..
  <plugin>
    <groupId>org.apache.maven.plugins</groupId>
    <artifactId>maven-failsafe-plugin</artifactId>
    <configuration>
      <!--
       ! currently needed to run integration tests.
      -->
      <systemProperties>
        <maven.version>${maven.version}</maven.version>
        <maven.home>${maven.home}</maven.home>
      </systemProperties>
    </configuration>
    <executions>
      <execution>
        <goals>
          <goal>integration-test</goal>
          <goal>verify</goal>
        </goals>
      </execution>
    </executions>
  </plugin>
</project...>
```
Details about the given configuration can be read in the [users guide][usersguide-html].

## The Involved Parties
Writing an integration test for a Maven plugin means you have to have three parties:

 1. The component you would like to test (typically a Maven plugin etc.).
 2. The testing code where you check the functionality.
 3. The Project you would like to test with (where your Maven plugin usually is configured in to be used.)

### The Component code 
The component you write is located in your project in the usual location. This is of course
only an example of how it looks like in reality (usually more classes etc.):

```
.
└── src/
    └── main/
        └── java/
            └── org/
                └── plugin/
                    └── PluginMojo.java
```

### The Testing Code
The structure for an integration tests follows of course the convention over configuration paradigm.
Based on the conventions in Maven an integration test should be named like `*IT.java` and be located in the directory
structure as follows:
```
.
└── src/
    └── test/
        └── java/
            └── org/
                └── it/
                    └── FirstMavenIT.java
```

So now the real a test code looks like this:

```java
package org.it;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenJupiterExtension // <1>
public class FirstMavenIT {

 @MavenTest // <2>
 void the_first_test_case(MavenExecutionResult result) { // <3>
  assertThat(result).isSuccessful(); // <4>
 }
 
}
```
1. Maven Integration test annotation.
2. Maven Test Annotation.
3. Injected execution result
4. [Custom assertions][usersguide-html-assertions] to check the result of the build.

### The Project to Test With
The project you would like to use as a foundation for your test of the plugin. This is located in a special location
under `src/test/resources-its/..`. This is needed to prevent the confusion with the usual
`src/test/resources` in particular related to filtering etc. (details about the setup etc. in the
 [users guide][usersguide-html]).

```text
.
└── src/
    └── test/
        └── resources-its/
            └── org/
                └── it/
                    └── FirstMavenIT/
                        └── the_first_test_case/
                            ├── src/
                            └── pom.xml
```

## The Executed Test
After execution of the integration test the result will look like this:
 
```text
.
└──target/
   └── maven-it/
       └── org/
           └── it/
               └── FirstMavenIT/
                   └── the_first_test_case/
                       ├── .m2/
                       ├── project/
                       │   ├── src/
                       │   ├── target/
                       │   └── pom.xml
                       ├── mvn-stdout.log
                       ├── mvn-stderr.log
                       ├── mvn-arguments.log
                       └── other logs.
```
This gives you a first impression how an integration test can look like. There are a lot 
of example in [this project available](https://github.com/khmarbaise/maven-it-extension/blob/master/itf-examples/src/test/java/com/soebes/itf/examples/MavenProjectIT.java)
and of course I strongly recommend reading the [documentation][usersguide-html] 
(I know I don't like reading documentation either).

### Conclusion

If you like the framework don't hesitate to test and give feedback in particular if things don't work
as described or as you expected them to work.


### Building 

If you like to build the project from source yourself you need the following:

* JDK 18+
* Apache Maven 3.8.6+

The whole project can be built via:
```bash
mvn clean verify
```
This will take some time cause there are already integration tests in the itf-examples project which 
are executed and real integration tests for the `itf-maven-plugin` which is a bootstrap 
([eat-your-own-dog-food][food]) to use already parts of the framework (JUnit Jupiter extension) to
make the development of the plugin easier.

# Concept and Background Guide

The concept guide describe my ideas I have in mind (just not to forget them). It is
neither a roadmap nor about future releases etc. It's only intended to keep my ideas at a central location.

  * [![PDF](https://img.shields.io/badge/PDF-Conceptguide-orange)][conceptguide-pdf]
  * [![HTML](https://img.shields.io/badge/HTML-Conceptguide-orange)][conceptguide-html]

The background guide is a conclusion about the reason why I have started this project.

  * [![PDF](https://img.shields.io/badge/PDF-background-orange)][background-pdf]
  * [![HTML](https://img.shields.io/badge/HTML-background-orange)][background-html]

# GitHub Pages

Currently, we have two states of site: 

  * Release State of the generated Maven site https://khmarbaise.github.io/maven-it-extension/
  * The SNAPSHOT State of the Maven site https://khmarbaise.github.io/maven-it-extension/snapshot/


# Generate Code Coverage


Code coverage via:
```bash
mvn clean verify org.jacoco:jacoco-maven-plugin:report
```
  
[food]: https://en.wikipedia.org/wiki/Eating_your_own_dog_food
[jdkbuilds]: https://github.com/khmarbaise/maven-it-extension/actions?query=workflow%3AJDKBuilds
[mainbuilds]: https://github.com/khmarbaise/maven-it-extension/actions?query=workflow%3AMain
[published-site]: https://khmarbaise.github.io/maven-it-extension/
[license]: https://www.apache.org/licenses/LICENSE-2.0
[junit-jupiter]: https://junit.org/junit5/
[junit-jupiter-extension]: https://junit.org/junit5/docs/current/user-guide/#extensions
[assertj]: https://assertj.github.io/doc/
[maven-failsafe-plugin]: https://maven.apache.org/surefire/maven-failsafe-plugin/ 

[usersguide-html]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/usersguide/usersguide.html
[usersguide-html-assertions]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/usersguide/usersguide.html#_assertions
[usersguide-pdf]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/usersguide/usersguide.pdf
[releasenotes-html]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/release-notes/release-notes.html
[releasenotes-pdf]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/release-notes/release-notes.pdf

[unstable-usersguide-html]: https://khmarbaise.github.io/maven-it-extension/snapshot/itf-documentation/usersguide/usersguide.html
[unstable-usersguide-pdf]: https://khmarbaise.github.io/maven-it-extension/snapshot/itf-documentation/usersguide/usersguide.pdf
[unstable-releasenotes-html]: https://khmarbaise.github.io/maven-it-extension/snapshot/itf-documentation/release-notes/release-notes.html
[unstable-releasenotes-pdf]: https://khmarbaise.github.io/maven-it-extension/snapshot/itf-documentation/release-notes/release-notes.pdf

[stable-html-release]: https://img.shields.io/badge/0.11.0-HTML--Releasenotes-green
[stable-html-usersgude]: https://img.shields.io/badge/0.11.0-HTML--Usersguide-green
[stable-pdf-releasenotes]: https://img.shields.io/badge/0.11.0-PDF--Releasenotes-green
[stable-pdf-usersguide]: https://img.shields.io/badge/0.11.0-PDF--Usersguide-green

[unstable-pdf-releasenotes]: https://img.shields.io/badge/0.12.0--SNAPSHOT-PDF--Releasenotes-orange
[unstable-html-releasenotes]: https://img.shields.io/badge/0.12.0--SNAPSHOT-HTML--Releasenotes-orange
[unstable-pdf-usersguide]: https://img.shields.io/badge/0.12.0--SNAPSHOT-PDF--Usersguide-orange
[unstable-html-usersguide]: https://img.shields.io/badge/0.12.0--SNAPSHOT-HTML--Usersguide-orange

[shield-central]: https://img.shields.io/maven-central/v/com.soebes.itf.jupiter.extension/itf-jupiter-extension.svg?label=Maven%20Central
[central-search]: https://search.maven.org/search?q=g%3Acom.soebes.itf.jupiter.extension
[not-available-in-central]: https://img.shields.io/badge/Maven%20Central-NOT_AVAILABLE-red

[conceptguide-html]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/concept/Concept.html
[conceptguide-pdf]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/concept/Concept.pdf
[background-html]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/background/background.html
[background-pdf]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/background/background.pdf
