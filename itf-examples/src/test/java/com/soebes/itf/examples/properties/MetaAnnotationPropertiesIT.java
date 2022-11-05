package com.soebes.itf.examples.properties;

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

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenJupiterExtension
@SkipTestAnnotation
class MetaAnnotationPropertiesIT {

  @MavenTest
  void property_skiptests(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .containsSubsequence(
            "--- maven-enforcer-plugin:3.0.0:enforce (enforce-maven) @ kata-fraction ---",
            "--- jacoco-maven-plugin:0.8.7:prepare-agent (default) @ kata-fraction ---",
            "--- maven-resources-plugin:3.2.0:resources (default-resources) @ kata-fraction ---",
            "--- maven-compiler-plugin:3.9.0:compile (default-compile) @ kata-fraction ---",
            "--- maven-resources-plugin:3.2.0:testResources (default-testResources) @ kata-fraction ---",
            "--- maven-compiler-plugin:3.9.0:testCompile (default-testCompile) @ kata-fraction ---",
            "--- maven-surefire-plugin:3.0.0-M5:test (default-test) @ kata-fraction ---",
            "Tests are skipped.",
            "--- maven-jar-plugin:3.2.2:jar (default-jar) @ kata-fraction ---",
            "--- maven-site-plugin:3.10.0:attach-descriptor (attach-descriptor) @ kata-fraction ---"
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

}