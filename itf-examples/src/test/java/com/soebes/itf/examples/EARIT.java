package com.soebes.itf.examples;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.soebes.itf.jupiter.extension.MavenIT;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenLog;
import com.soebes.itf.jupiter.maven.MavenProjectResult;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

/**
 * Examples taken from Maven EAR Plugin
 * <p>
 * Invoker Integration Test:
 * <ul>
 *   <li>test_project_root</li>
 *   <li>packaging_includes</li>
 *   <li>packaging_excludes</li>
 *   <li>transitive_excludes</li>
 * </ul>
 *
 * @author Karl Heinz Marbaise
 */
@MavenIT
class EARIT {

  @MavenTest
  void basic(MavenExecutionResult result) {
    assertThat(result).isSuccessful()
        .project()
        .hasTarget()
        .withEarFile()
        .containsOnlyOnce("META-INF/application.xml", "META-INF/appserver-application.xml");
  }

  @MavenTest
  void packaging_includes(MavenExecutionResult result) {
    assertThat(result).isSuccessful()
        .project()
        .hasTarget()
        .withEarFile()
        .doesNotContain("commons-io-commons-io-1.4.jar")
        .containsOnlyOnce("commons-lang-commons-lang-2.5.jar", "META-INF/application.xml", "META-INF/MANIFEST.MF");
  }

  @MavenTest
  void packaging_excludes(MavenExecutionResult result) {
    assertThat(result).isSuccessful()
        .project()
        .hasTarget()
        .withEarFile()
        .doesNotContain("commons-lang-commons-lang-2.5.jar")
        .containsOnlyOnce("META-INF/application.xml", "META-INF/MANIFEST.MF");
  }

  @MavenTest
  void resource_custom_directory(MavenExecutionResult result, MavenProjectResult project, MavenLog log) {
    assertThat(result).isSuccessful();
    assertThat(log).isSuccessful();
    assertThat(project).hasTarget()
        .withEarFile()
        .containsOnlyOnce("META-INF/application.xml", "APP-INF/classes/foo.properties");
  }

  @MavenTest
  void transitive_excludes(MavenExecutionResult result) {
    assertThat(result).isSuccessful()
        .project()
        .hasTarget()
        .withEarFile()
        .containsOnlyOnce("org.apache.maven-maven-core-3.0.jar", "META-INF/application.xml");

//    assertThat(log).info().stream().filter(s -> s.startsWith("TESTING"));
//    assertThat(log).debug().stream().filter(s -> s.endsWith("FAILURE"));
    //assertThat(log).buildFailure(); DOES NOT WORK YET

  }

  @MavenTest
  void skinny_wars_javaee5(MavenExecutionResult result, MavenProjectResult project) {
    assertThat(result).isSuccessful()
        .project()
        .hasModule("war-module")
        .hasModule("ear-module");

    /*
    $ unzip -t ear-module-1.0.ear
Archive:  ear-module-1.0.ear
    testing: META-INF/MANIFEST.MF     OK
    testing: META-INF/                OK
    testing: lib/                     OK
    testing: META-INF/maven/          OK
    testing: META-INF/maven/org.apache.maven.its.ear.skinnywars/   OK
    testing: META-INF/maven/org.apache.maven.its.ear.skinnywars/ear-module/   OK
    testing: org.apache.maven.its.ear.skinnywars-war-module-1.0.war   OK
    testing: META-INF/application.xml   OK
    testing: lib/commons-lang-commons-lang-2.5.jar   OK
    testing: META-INF/maven/org.apache.maven.its.ear.skinnywars/ear-module/pom.xml   OK
    testing: META-INF/maven/org.apache.maven.its.ear.skinnywars/ear-module/pom.properties   OK
No errors detected in compressed data of ear-module-1.0.ear.
     */
    assertThat(project)
        .withModule("ear-module")
        .withEarFile()
        .containsOnlyOnce("org.apache.maven.its.ear.skinnywars-war-module-1.0.war",
            "lib/commons-lang-commons-lang-2.5.jar",
            "META-INF/application.xml");
  }
}
