package com.soebes.itf.examples.options;

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

import com.soebes.itf.jupiter.extension.MavenCLIOptions;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenOption;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;
import static com.soebes.itf.jupiter.extension.MavenCLIOptions.LOG_FILE;
import static com.soebes.itf.jupiter.extension.MavenCLIOptions.QUIET;
import static com.soebes.itf.jupiter.extension.MavenCLIOptions.SHOW_VERSION;

@MavenJupiterExtension
class OptionsIT {

  @MavenTest
  void no_options_at_all(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .containsSubsequence(
            "--- enforcer:3.4.1:enforce (enforce-maven) @ kata-fraction ---",
            "--- jacoco:0.8.11:prepare-agent (default) @ kata-fraction ---",
            "--- resources:3.3.1:resources (default-resources) @ kata-fraction ---",
            "--- compiler:3.11.0:compile (default-compile) @ kata-fraction ---",
            "--- resources:3.3.1:testResources (default-testResources) @ kata-fraction ---",
            "--- compiler:3.11.0:testCompile (default-testCompile) @ kata-fraction ---",
            "--- surefire:3.2.2:test (default-test) @ kata-fraction ---",
            "--- jar:3.3.0:jar (default-jar) @ kata-fraction ---",
            "--- site:3.12.1:attach-descriptor (attach-descriptor) @ kata-fraction ---"
        );
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().isEmpty();
    assertThat(result)
        .isSuccessful()
        .out()
        .debug().isEmpty();
  }

  @MavenTest
  @MavenOption(MavenCLIOptions.VERBOSE)
  void option_debug(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .containsSubsequence(
            "--- enforcer:3.4.1:enforce (enforce-maven) @ kata-fraction ---",
            "--- jacoco:0.8.11:prepare-agent (default) @ kata-fraction ---",
            "--- resources:3.3.1:resources (default-resources) @ kata-fraction ---",
            "--- compiler:3.11.0:compile (default-compile) @ kata-fraction ---",
            "--- resources:3.3.1:testResources (default-testResources) @ kata-fraction ---",
            "--- compiler:3.11.0:testCompile (default-testCompile) @ kata-fraction ---",
            "--- surefire:3.2.2:test (default-test) @ kata-fraction ---",
            "--- jar:3.3.0:jar (default-jar) @ kata-fraction ---",
            "--- site:3.12.1:attach-descriptor (attach-descriptor) @ kata-fraction ---"
        );
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().isEmpty();
    assertThat(result)
        .isSuccessful()
        .out()
        .debug()
        .containsSubsequence(
            "Goal:          org.apache.maven.plugins:maven-enforcer-plugin:3.4.1:enforce (enforce-maven)",
            "Goal:          org.jacoco:jacoco-maven-plugin:0.8.11:prepare-agent (default)",
            "Goal:          org.apache.maven.plugins:maven-resources-plugin:3.3.1:resources (default-resources)",
            "Goal:          org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile (default-compile)",
            "Goal:          org.apache.maven.plugins:maven-resources-plugin:3.3.1:testResources (default-testResources)",
            "Goal:          org.apache.maven.plugins:maven-compiler-plugin:3.11.0:testCompile (default-testCompile)",
            "Goal:          org.apache.maven.plugins:maven-surefire-plugin:3.2.2:test (default-test)",
            "Goal:          org.apache.maven.plugins:maven-jar-plugin:3.3.0:jar (default-jar)",
            "Goal:          org.apache.maven.plugins:maven-site-plugin:3.12.1:attach-descriptor (attach-descriptor)"
        );
  }

  @MavenTest
  @MavenOption(QUIET)
  @MavenOption(SHOW_VERSION)
  @MavenOption(value = LOG_FILE, parameter = "test.log")
  void option_quiet_logfile(MavenExecutionResult result) throws IOException {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .isEmpty();

    assertThat(result)
        .isSuccessful()
        .out()
        .warn().isEmpty();

    assertThat(result)
        .isSuccessful()
        .out()
        .debug().isEmpty();

    Path baseDir = result.getMavenProjectResult().getTargetProjectDirectory();
    /*
       Ony the following lines will be written to the resulting test.log:
        Apache Maven 3.6.3 (cecedd343002696d0abb50b32b541b8a6ba2883f)
        Maven home: /...
        Java version: 11.0.8, vendor: AdoptOpenJDK, runtime: ...
        Default locale: en_GB, platform encoding: UTF-8
        OS name: "mac os x", version: "10.14.6", arch: "x86_64", family: "mac"
     */
    List<String> lines = Files.lines(baseDir.resolve( "test.log")).collect(Collectors.toList());
    assertThat(lines).describedAs("The log container more than five lines.", lines).hasSize(5);
  }


}