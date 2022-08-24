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
@MavenGoal("clean")
class GoalsOnClassIT {

  @MavenTest
  @DisplayName("This will check the goal which is defined on the class.")
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
            "--- maven-clean-plugin:3.2.0:clean (default-clean) @ kata-fraction ---"
        );
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().isEmpty();
  }

}