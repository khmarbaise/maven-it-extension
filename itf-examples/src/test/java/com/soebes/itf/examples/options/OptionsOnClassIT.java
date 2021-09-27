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

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenJupiterExtension
@MavenOption(MavenCLIOptions.DEBUG)
class OptionsOnClassIT {

  @MavenTest
  void option_debug(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .containsSubsequence(
            "--- maven-enforcer-plugin:3.0.0:enforce (enforce-maven) @ kata-fraction ---",
            "--- jacoco-maven-plugin:0.8.7:prepare-agent (default) @ kata-fraction ---",
            "--- maven-resources-plugin:3.2.0:resources (default-resources) @ kata-fraction ---",
            "--- maven-compiler-plugin:3.8.1:compile (default-compile) @ kata-fraction ---",
            "--- maven-resources-plugin:3.2.0:testResources (default-testResources) @ kata-fraction ---",
            "--- maven-compiler-plugin:3.8.1:testCompile (default-testCompile) @ kata-fraction ---",
            "--- maven-surefire-plugin:3.0.0-M5:test (default-test) @ kata-fraction ---",
            "--- maven-jar-plugin:3.2.0:jar (default-jar) @ kata-fraction ---",
            "--- maven-site-plugin:3.9.1:attach-descriptor (attach-descriptor) @ kata-fraction ---"
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
            "Goal:          org.apache.maven.plugins:maven-enforcer-plugin:3.0.0:enforce (enforce-maven)",
            "Goal:          org.jacoco:jacoco-maven-plugin:0.8.7:prepare-agent (default)",
            "Goal:          org.apache.maven.plugins:maven-resources-plugin:3.2.0:resources (default-resources)",
            "Goal:          org.apache.maven.plugins:maven-compiler-plugin:3.8.1:compile (default-compile)",
            "Goal:          org.apache.maven.plugins:maven-resources-plugin:3.2.0:testResources (default-testResources)",
            "Goal:          org.apache.maven.plugins:maven-compiler-plugin:3.8.1:testCompile (default-testCompile)",
            "Goal:          org.apache.maven.plugins:maven-surefire-plugin:3.0.0-M5:test (default-test)",
            "Goal:          org.apache.maven.plugins:maven-jar-plugin:3.2.0:jar (default-jar)",
            "Goal:          org.apache.maven.plugins:maven-site-plugin:3.9.1:attach-descriptor (attach-descriptor)"
        );
  }

}