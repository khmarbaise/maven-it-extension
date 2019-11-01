package org.apache.maven.jupiter.it;

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

import static org.apache.maven.jupiter.assertj.MavenExecutionResultAssert.assertThat;

import org.apache.maven.jupiter.extension.MavenIT;
import org.apache.maven.jupiter.extension.MavenTest;
import org.apache.maven.jupiter.extension.maven.MavenCache;
import org.apache.maven.jupiter.extension.maven.MavenExecutionResult;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.JRE;

@MavenIT(mavenCache = MavenCache.Global)
@DisabledOnJre({JRE.JAVA_11})
@Disabled
class ThirdMavenIntegrationIT {

  @MavenTest(activeProfiles = {"run-its"})
  void first_integration_test(MavenExecutionResult result) {
    System.out.println("MavenIntegrationIT.first_integration_test");
    assertThat(result).isFailure();
  }

  @MavenTest
  void second_integration_test_case(MavenExecutionResult result) {
    System.out.println("MavenIntegrationIT.first_integration_test");
    assertThat(result).isFailure();
  }

}