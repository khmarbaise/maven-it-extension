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
[![Maven Central](https://img.shields.io/maven-central/v/com.soebes.itf.jupiter.extension/maven-it-extension.svg?label=Maven%20Central)](https://search.maven.org/#search%7Cgav%7C1%7Cg%3A%22com.soebes.itf.jupiter.extension%22%20AND%20%3A%22itf-extension%22)
[![JDKBuilds](https://github.com/khmarbaise/maven-it-extension/workflows/JDKBuilds/badge.svg)][jdkbuilds]
[![Main](https://github.com/khmarbaise/maven-it-extension/workflows/Main/badge.svg)][mainbuilds]
[![Issues](https://img.shields.io/github/issues/khmarbaise/maven-it-extension)](https://github.com/khmarbaise/maven-it-extension/issues)
[![Issues Closed](https://img.shields.io/github/issues-closed/khmarbaise/maven-it-extension)](https://github.com/khmarbaise/maven-it-extension/issues?q=is%3Aissue+is%3Aclosed)
 
* Users Guide
  * [![PDF](https://img.shields.io/badge/PDF-Usersguide-green)][usersguide-pdf]
  * [![HTML](https://img.shields.io/badge/HTML-Usersguide-green)][usersguide-html]

* Concept Guide (My Ideas)
  * [![PDF](https://img.shields.io/badge/PDF-Conceptguide-green)][conceptguide-pdf]
  * [![HTML](https://img.shields.io/badge/HTML-Conceptguide-green)][conceptguide-html]

* The Background Guide (More detailed why this framework exists.)
  * [![HTML](https://img.shields.io/badge/HTML-background-green)][background-html]

# General Overview
The basic thing about integration testing of Maven Plugins / Maven Extensions etc. 
is currently that the existing solutions are not a very concise and comprehensive solutions which
is based on the long development history of the Apache Maven project. Different
approaches have been done over the time. 

## Overview of the Current Situation

### Maven Invoker Plugin

In [Maven Invoker Plugin][maven-invoker-plugin] the following issues exist:

* Parallelizing does not work and is not easy to integrate based on
  current concept and code base.
  *  Apart from being implemented it would hard to express the prevention
of parallel execution in some situations.
* Separating caches for each build is hard to implement
* Get a common cache for a set of integration tests is even harder.
* A Concept like `BeforeEach` or `BeforeAll` is current not really possible.
  * The concept with `setup` project is not correctly working at the moment.
* Writing integration tests forces one to write in Groovy or Beanshell.
  * This means to enhance the number of dependencies. In days of Java 5 until 7 it had been an
advantage to use Groovy with it's supports for closures etc. which made it simpler and easier
to write things for integration tests, but since JDK 8 it is not necessary anymore.
* Integrationtest are not that expressive as they should be.
* Violation of [separation of concern paradigm](https://en.wikipedia.org/wiki/Separation_of_concerns)
  * Conditions
    * Assertions are hard to express cause one implicit assertion is that a build has to be successful (can be changed if necessary)
    * Conditions for the execution of a test for example are:
      * should be executed only on JDK11
      * should be executed only on Maven 3.3.9 and above
      * Several other conditions
    * are expressed within a single file [ìnvoker.properties](https://maven.apache.org/plugins/maven-invoker-plugin/integration-test-mojo.html#invokerPropertiesFile).

### Maven Verifier Plugin

The [Maven Verifier Plugin](https://maven.apache.org/plugins/maven-verifier-plugin) is intended to
write tests to check for the existence of files or the absence of files but in the end it is
very limited.

The [maven-verifier] is intended to write integration tests for Maven ...

### Maven Verifier Component

The [maven-verifier] is intended to write a kind of tests:

* You can set the command line parameters for an executed instance of Maven like `-s`, `-X` etc.
* Execute goals like `package` or alike.
* It contains some methods like `assertFilePresent`, `assertFileMatches`,
   `verifyArtifactPresence` etc. but not a comprehensive set of methods.
* Some parts are like Maven Invoker Plugin for example starting an external
process with Maven (something like starting Maven on command line.).
* Is JUnit 3 based.
* Manually [maintained TestSuite]([maintained])
to execute all integration tests of Maven Core.
* Each Testcase must be expressed by a separate [Test class](https://github.com/apache/maven-integration-testing/blob/master/core-it-suite/src/test/java/org/apache/maven/it/MavenIT0090EnvVarInterpolationTest.java).
* Manually [implemented conditionally execution](https://github.com/apache/maven-integration-testing/blob/master/core-it-suite/src/test/java/org/apache/maven/it/MavenITmng6391PrintVersionTest.java).
* Conditions for execution only based on a self implemented constructor part which defines the Maven version under which it should run or not.

### Maven Plugin Testing Harness

The [Maven Plugin Testing Harness]([maven-plugin-testing-harness]) is intended to write tests for using parameters correctly and
several other setup situations but the test setup is separated into a unit test like part in code
and a part which is pom like

* It's bound to versions of Maven core which might caused issues during testing with other versions
  of Maven.
* https://maven.apache.org/plugin-testing/maven-plugin-testing-harness/getting-started/index.html
* Also JUnit 4 based.

### Mock Repository Manager

Currently it's only possible to have a single instance of the mock repository manager running which
is based on the limited concept cause we need to define it in the `pom.xml`. Of course
we could start two or more instances but this would make the setup more or less unreadable.

### Why not Spock?

So you might ask why not using Spock or any other testing framework for such purposes?
Let me summarize the different aspects I had in mind:

* People often tend to write Java code (which is valid), cause
they don't know Groovy or don't want to learn a new language
just to write tests. This means in the end: Why Groovy? Just use Java.
* It's much easier for new contributors/devs to get into the
project if you only need to know Java to write plugins, unit
tests and integration tests. So removing a supplemental
barrier will help.
* Support for most recent Java versions which is a complete
blocker for the Apache Maven project, cause the Apache Maven Project is  running builds
in a very early stage (Early access) which would block us (see our builds for example [Maven EAR Plugin Build]([maven-ear-plugin-build]).
Currently spock is not yet tested/build against JDK11+ ?
So having a Testing framework which might not work on most
recent versions is a complete blocker.
* In earlier days I would have argued to use Spock based
on the language features but since JDK8 I don't see any advantage
in using Groovy over Java anymore.
* Spock does not support parallelizing of tests (full blocker for me)
* Good IDE Support for Groovy is at the moment only given in
IDEA IntelliJ as well as for DSL support for Spock.
That would block many people. This blocker based on the usage
of a particular IDE is not acceptable for an open source project
like the Apache Maven Project and from my point of view as
an Apache Maven PMC member this is simply a no go.

### Conclusion

It is needed to have a combination of [Maven Invoker Plugin]([maven-invoker-plugin]), Maven Verifier etc. into
a single Testing framework which should make it possible to make integration tests
easier to write and make them more expressive about what the intention of what a test exactly is.

It looks like a good solution to use existing frameworks like [JUnit Jupiter]([junit-jupiter]) and assertions like
[AssertJ]([assertj]) library to express what it's needed. This in result will give automatically
many advantages for example the integration into the IDE as well as writing the tests in
Java code and furthermore opens easy ways to use existing Java libraries.

Using [JUnit Jupiter]([junit-jupiter]) as base will solve lot of things which are already supported by [JUnit Jupiter]([junit-jupiter])
like conditional execution of Tests based on JRE or possible deactivation based on
properties etc.

Based on [AssertJ]([assertj]) it could be easy to express the assertions for test results in many ways and can
also being enhanced by writing custom assertions.



[jdkbuilds]: https://github.com/khmarbaise/maven-it-extension/actions?query=workflow%3AJDKBuilds
[mainbuilds]: https://github.com/khmarbaise/maven-it-extension/actions?query=workflow%3AMain
[usersguide-html]: https://khmarbaise.github.io/maven-it-extension/usersguide.html
[usersguide-pdf]: https://khmarbaise.github.io/maven-it-extension/usersguide.pdf
[conceptguide-html]: https://khmarbaise.github.io/maven-it-extension/Concept.html
[conceptguide-pdf]: https://khmarbaise.github.io/maven-it-extension/Concept.pdf
[background-html]: https://khmarbaise.github.io/maven-it-extension/background.html
[license]: https://www.apache.org/licenses/LICENSE-2.0
[junit-jupiter]: https://junit.org/junit5/
[junit-jupiter-extension]: https://junit.org/junit5/docs/current/user-guide/#extensions
[assertj]: https://assertj.github.io/doc/
[maven-plugins]: https://maven.apache.org/plugins/
[maven-invoker-plugin]: https://maven.apache.org/plugins/maven-invoker-plugin/
[maven-verifier]: https://maven.apache.org/shared/maven-verifier/
[github-versions-maven-plugin]: https://github.com/mojohaus/versions-maven-plugin
[maven-ear-plugin-build]: https://builds.apache.org/view/M-R/view/Maven/job/maven-box/job/maven-ear-plugin/job/master/
[maven-plugin-testing-harness]: https://maven.apache.org/plugin-testing/maven-plugin-testing-harness/index.html
[maintained]: https://github.com/apache/maven-integration-testing/blob/master/core-it-suite/src/test/java/org/apache/maven/it/IntegrationTestSuite.java
