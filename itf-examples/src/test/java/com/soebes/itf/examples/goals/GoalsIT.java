package com.soebes.itf.examples.goals;

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

import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.DisplayName;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenJupiterExtension
class GoalsIT {

  @MavenTest
  @DisplayName("This test checks that the default goal:package is used if no MavenGoal is defined at all.")
  void no_goals_at_all(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .containsSubsequence(
            "--- maven-enforcer-plugin:3.4.1:enforce (enforce-maven) @ kata-fraction ---",
            "--- jacoco-maven-plugin:0.8.11:prepare-agent (default) @ kata-fraction ---",
            "--- maven-resources-plugin:3.3.1:resources (default-resources) @ kata-fraction ---",
            "--- maven-compiler-plugin:3.11.0:compile (default-compile) @ kata-fraction ---",
            "--- maven-resources-plugin:3.3.1:testResources (default-testResources) @ kata-fraction ---",
            "--- maven-compiler-plugin:3.11.0:testCompile (default-testCompile) @ kata-fraction ---",
            "--- maven-surefire-plugin:3.2.2:test (default-test) @ kata-fraction ---",
            "--- maven-jar-plugin:3.3.0:jar (default-jar) @ kata-fraction ---",
            "--- maven-site-plugin:3.12.1:attach-descriptor (attach-descriptor) @ kata-fraction ---"
        );
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().isEmpty();

  }

  @MavenTest
  @MavenGoal("clean")
  void goal_clean(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .containsSubsequence(
            "Scanning for projects...",
            "-------------------< com.soebes.katas:kata-fraction >-------------------",
            "Building kata-fraction 1.0-SNAPSHOT",
            "--------------------------------[ jar ]---------------------------------",
            "--- maven-clean-plugin:3.3.2:clean (default-clean) @ kata-fraction ---"
        );
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().isEmpty();
  }

  @MavenTest
  @MavenGoal("clean")
  @MavenGoal("compile")
  void goal_clean_compile(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .containsSubsequence(
            "Scanning for projects...",
            "-------------------< com.soebes.katas:kata-fraction >-------------------",
            "Building kata-fraction 1.0-SNAPSHOT",
            "--------------------------------[ jar ]---------------------------------",
            "--- maven-clean-plugin:3.3.2:clean (default-clean) @ kata-fraction ---",
            "--- maven-enforcer-plugin:3.4.1:enforce (enforce-maven) @ kata-fraction ---",
            "--- jacoco-maven-plugin:0.8.11:prepare-agent (default) @ kata-fraction ---",
            "--- maven-resources-plugin:3.3.1:resources (default-resources) @ kata-fraction ---",
            "--- maven-compiler-plugin:3.11.0:compile (default-compile) @ kata-fraction ---"
        );
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().isEmpty();
  }

}