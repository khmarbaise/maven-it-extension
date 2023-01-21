package com.soebes.itf.examples.profiles;

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
import com.soebes.itf.jupiter.extension.MavenProfile;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.MavenVersion;
import com.soebes.itf.jupiter.extension.condition.DisabledForMavenVersion;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenJupiterExtension
class ProfileIT {

  @MavenTest
  void no_profile_at_all(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().isEmpty();
  }

  @MavenTest
  @MavenProfile("profile-1")
  void profile_1(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .contains("--- echo-maven-plugin:0.5.0:echo (echo-in-profile-1) @ kata-fraction ---");
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().containsExactly("Message for Profile 1");
  }

  @MavenTest
  @MavenProfile("profile-1")
  @MavenProfile("profile-2")
  void profile_1_2(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .containsSubsequence(
            "--- echo-maven-plugin:0.5.0:echo (echo-in-profile-1) @ kata-fraction ---",
            "--- echo-maven-plugin:0.5.0:echo (echo-in-profile-2) @ kata-fraction ---"
        );
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().containsExactly("Message for Profile 1", "Message for Profile 2");
  }

  @MavenTest
  @MavenProfile({"profile-1", "profile-2", "profile-3"})
  void profile_1_2_3(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .containsSubsequence(
            "--- echo-maven-plugin:0.5.0:echo (echo-in-profile-1) @ kata-fraction ---",
            "--- echo-maven-plugin:0.5.0:echo (echo-in-profile-2) @ kata-fraction ---",
            "--- echo-maven-plugin:0.5.0:echo (echo-in-profile-3) @ kata-fraction ---"
        );
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().containsExactly("Message for Profile 1", "Message for Profile 2", "Message for Profile 3");
  }

  @MavenTest
  @MavenProfile("unknown-profile")
  @DisabledForMavenVersion(MavenVersion.M3_8_7)
  void unknown_profile(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().containsExactly("The requested profile \"unknown-profile\" could not be activated because it does not exist.");
  }

}