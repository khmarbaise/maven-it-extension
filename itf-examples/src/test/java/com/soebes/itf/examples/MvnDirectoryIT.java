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

import com.soebes.itf.jupiter.extension.MavenCLIOptions;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenOption;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenJupiterExtension
@MavenOption(MavenCLIOptions.NO_TRANSFER_PROGRESS)
@MavenOption(MavenCLIOptions.BATCH_MODE)
class MvnDirectoryIT {

  @MavenTest
  void non_hidden_files(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .containsSubsequence(
            "Scanning for projects...",
            // This means .mvn/maven.config with -T 2 option has been read.
            "Using the MultiThreadedBuilder implementation with a thread count of 2",
            "--- enforcer:3.5.0:enforce (enforce-maven) @ kata-fraction ---",
            "--- jacoco:0.8.13:prepare-agent (default) @ kata-fraction ---",
            "--- resources:3.3.1:resources (default-resources) @ kata-fraction ---",
            "--- compiler:3.14.0:compile (default-compile) @ kata-fraction ---",
            "Tests run: 34, Failures: 0, Errors: 0, Skipped: 0"
        );
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().isEmpty();
  }

  @MavenTest
  void hidden_files_only(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .doesNotContain("Using the MultiThreadedBuilder implementation with a thread count of 2")
        .containsSubsequence(
            "Scanning for projects...",
            "--- enforcer:3.5.0:enforce (enforce-maven) @ kata-fraction ---",
            "--- jacoco:0.8.13:prepare-agent (default) @ kata-fraction ---",
            "--- resources:3.3.1:resources (default-resources) @ kata-fraction ---",
            "--- compiler:3.14.0:compile (default-compile) @ kata-fraction ---",
            "Tests run: 34, Failures: 0, Errors: 0, Skipped: 0"
        );
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().isEmpty();
  }

}
