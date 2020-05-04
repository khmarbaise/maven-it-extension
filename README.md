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
[![javadoc](https://javadoc.io/badge2/com.soebes.itf.jupiter.extension/itf-maven-plugin/javadoc.svg)](https://javadoc.io/doc/com.soebes.itf.jupiter.extension/itf-maven-plugin)
[![JDKBuilds](https://github.com/khmarbaise/maven-it-extension/workflows/JDKBuilds/badge.svg)][jdkbuilds]
[![Main](https://github.com/khmarbaise/maven-it-extension/workflows/Main/badge.svg)][mainbuilds]
[![Site](https://github.com/khmarbaise/maven-it-extension/workflows/SitePublishing/badge.svg)][published-site]
[![Issues](https://img.shields.io/github/issues/khmarbaise/maven-it-extension)](https://github.com/khmarbaise/maven-it-extension/issues)
[![Issues Closed](https://img.shields.io/github/issues-closed/khmarbaise/maven-it-extension)](https://github.com/khmarbaise/maven-it-extension/issues?q=is%3Aissue+is%3Aclosed)
 
<!---
 -green will be done for 1.0.0 the first time.
 -orange used for SNAPSHOT
-->
| Release        | Maven Central                                       | Release Notes                                                     |                           Users Guide                          |
| -------------- | --------------------------------------------------- | ----------------------------------------------------------------: | -------------------------------------------------------------: |
| 0.5.0          | [![Maven Central][shield-central]][central-search]  | [![PDF][stable-pdf-releasenotes]][releasenotes-pdf]               | [![PDF][stable-pdf-usersguide]][usersguide-pdf]                |
|                |                                                     | [![HTML][stable-html-release]][releasenotes-html]                 | [![HTML][stable-html-usersgude]][usersguide-html]              |
| 0.6.0-SNAPSHOT | ![Maven Central][not-available-in-central]          | [![PDF][unstable-pdf-releasenotes]][unstable-releasenotes-pdf]    | [![PDF][unstable-pdf-usersguide]][unstable-usersguide-pdf]     |
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

## The Basic Idea
The basic idea rest upon the option to write custom [extension with JUnit Jupiter][junit-jupiter-extension]
for [JUnit Jupiter][junit-jupiter-extension] testing framework which makes it very easy to get things done.

So in general the whole Integration Testing Framework in it's core (itf-jupiter-extension) is a 
[JUnit Jupiter extension][junit-jupiter-extension] which delivers supplemental support for a testing
framework in general and in particular for using integration tests.
Of course there is a lot of convenience integrated into it to make integration testing of Maven plugins easier.

## The Involved Parties
Writing an integration test for a Maven plugin means you have to have three parties:

 1. The component you would like to test (typically a Maven plugin etc.).
 2. The testing code where you check the functionality.
 2. The Project you would like to test with (where your Maven plugin usually is configured in to be used.)

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

import static org.assertj.core.api.Assertions.assertThat;

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

@MavenJupiterExtension // <1>
class FirstMavenIT {

  @MavenTest // <2>
  void the_first_test_case(MavenExecutionResult result) { //<3>
    assertThat(result).build().isSuccessful(); // <4>
  }

}
```
1. Maven Integration test annotation.
2. Maven Test Annotation.
3. Injected execution result
4. Custom assertions to check the result of the build.

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
                       └── orther logs.
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

* JDK 8+
* Apache Maven 3.6.3

The whole project can be built via:
```bash
mvn clean verify
```
This will take some time cause there are already integration tests in the itf-examples project which 
are executed and real integration tests for the `itf-maven-plugin` which is a bootstrap 
([eat-your-own-dog-food][food]) to use already parts of the framework (JUnit Jupiter extension) to
make the development of the plugin easier.

# Concept and Background Guide

The concept guide decribe my ideas I have in mind (just not to forget them). It is
neither a roadmap or about future releases etc. It's only intended to keep my ideas at a central location.

  * [![PDF](https://img.shields.io/badge/PDF-Conceptguide-orange)][conceptguide-pdf]
  * [![HTML](https://img.shields.io/badge/HTML-Conceptguide-orange)][conceptguide-html]

The background guide is a conclusion about the reason I had to start this project.

  * [![PDF](https://img.shields.io/badge/PDF-background-orange)][background-pdf]
  * [![HTML](https://img.shields.io/badge/HTML-background-orange)][background-html]

[food]: https://en.wikipedia.org/wiki/Eating_your_own_dog_food
[jdkbuilds]: https://github.com/khmarbaise/maven-it-extension/actions?query=workflow%3AJDKBuilds
[mainbuilds]: https://github.com/khmarbaise/maven-it-extension/actions?query=workflow%3AMain
[published-site]: https://khmarbaise.github.io/maven-it-extension/
[license]: https://www.apache.org/licenses/LICENSE-2.0
[junit-jupiter]: https://junit.org/junit5/
[junit-jupiter-extension]: https://junit.org/junit5/docs/current/user-guide/#extensions
[assertj]: https://assertj.github.io/doc/

[usersguide-html]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/usersguide/usersguide.html
[usersguide-pdf]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/usersguide/usersguide.pdf
[releasenotes-html]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/usersguide/release-notes/release-notes.html
[releasenotes-pdf]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/usersguide/release-notes/release-notes.pdf

[unstable-usersguide-html]: https://khmarbaise.github.io/maven-it-extension/snapshot/itf-documentation/usersguide/usersguide.html
[unstable-usersguide-pdf]: https://khmarbaise.github.io/maven-it-extension/snapshot/itf-documentation/usersguide/usersguide.pdf
[unstable-releasenotes-html]: https://khmarbaise.github.io/maven-it-extension/snapshot/itf-documentation/usersguide/release-notes/release-notes.html
[unstable-releasenotes-pdf]: https://khmarbaise.github.io/maven-it-extension/snapshot/itf-documentation/usersguide/release-notes/release-notes.pdf

[stable-html-release]: https://img.shields.io/badge/0.5.0-HTML--Releasenotes-green
[stable-html-usersgude]: https://img.shields.io/badge/0.5.0-HTML--Usersguide-green
[stable-pdf-releasenotes]: https://img.shields.io/badge/0.5.0-PDF--Releasenotes-green
[stable-pdf-usersguide]: https://img.shields.io/badge/0.5.0-PDF--Usersguide-green

[unstable-pdf-releasenotes]: https://img.shields.io/badge/0.6.0--SNAPSHOT-PDF--Releasenotes-orange
[unstable-html-releasenotes]: https://img.shields.io/badge/0.6.0--SNAPSHOT-HTML--Releasenotes-orange
[unstable-pdf-usersguide]: https://img.shields.io/badge/0.6.0--SNAPSHOT-PDF--Usersguide-orange
[unstable-html-usersguide]: https://img.shields.io/badge/0.6.0--SNAPSHOT-HTML--Usersguide-orange

[shield-central]: https://img.shields.io/maven-central/v/com.soebes.itf.jupiter.extension/itf-jupiter-extension.svg?label=Maven%20Central
[central-search]: https://search.maven.org/search?q=g%3Acom.soebes.itf.jupiter.extension
[not-available-in-central]: https://img.shields.io/badge/Maven%20Central-NOT_AVAILABLE-red

[conceptguide-html]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/concept/Concept.html
[conceptguide-pdf]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/concept/Concept.pdf
[background-html]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/background/background.html
[background-pdf]: https://khmarbaise.github.io/maven-it-extension/itf-documentation/background/background.pdf
