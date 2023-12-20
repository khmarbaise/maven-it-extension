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
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.DisplayName;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenJupiterExtension
@MavenProfile({"profile-1", "profile-2", "profile-3"})
class ProfileOnClassIT {

  @MavenTest
  void profile_1_2_3(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .containsSubsequence(
            "--- echo:0.5.0:echo (echo-in-profile-1) @ kata-fraction ---",
            "--- echo:0.5.0:echo (echo-in-profile-2) @ kata-fraction ---",
            "--- echo:0.5.0:echo (echo-in-profile-3) @ kata-fraction ---"
        );
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().containsExactly("Message for Profile 1", "Message for Profile 2", "Message for Profile 3");
  }

  @MavenTest
  @DisplayName("This tests shows that the profile on the method level takes precedence over the mavenProfile on the class level.")
  @MavenProfile("profile-1")
  void profile_1(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .info()
        .contains(
            "--- echo:0.5.0:echo (echo-in-profile-1) @ kata-fraction ---"
        );
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().containsExactly("Message for Profile 1");
  }

}