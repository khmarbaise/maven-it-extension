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

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.DisplayName;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenJupiterExtension
@GoalsCleanVerify
class MetaAnnotationGoalIT {

  @MavenTest
  @DisplayName("This will check the goals which are defined via the Meta annotation @GoalsCleanVerify")
  void clean_verify(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .containsSubsequence(
            "Scanning for projects...",
            "--- clean:3.4.0:clean (default-clean) @ kata-fraction ---",
            "--- enforcer:3.5.0:enforce (enforce-maven) @ kata-fraction ---",
            "--- jacoco:0.8.12:prepare-agent (default) @ kata-fraction ---",
            "--- resources:3.3.1:resources (default-resources) @ kata-fraction ---",
            "--- compiler:3.13.0:compile (default-compile) @ kata-fraction ---",
            "--- resources:3.3.1:testResources (default-testResources) @ kata-fraction ---",
            "--- compiler:3.13.0:testCompile (default-testCompile) @ kata-fraction ---",
            "--- surefire:3.3.1:test (default-test) @ kata-fraction ---",
            "Tests run: 34, Failures: 0, Errors: 0, Skipped: 0",
            "--- jar:3.4.2:jar (default-jar) @ kata-fraction ---",
            "--- site:3.12.1:attach-descriptor (attach-descriptor) @ kata-fraction ---",
            "Skipping because packaging 'jar' is not pom.",
            "--- jacoco:0.8.12:report (default) @ kata-fraction ---"
        );

    assertThat(result)
        .isSuccessful()
        .out()
        .warn().isEmpty();
  }

}