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
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.Nested;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

/**
 * Example integration test to demonstrate the usage
 * of {@code @SystemProperty(..)} on a class level and to make
 * sure it's found on the class level. This means
 * that the given testing method {@code property_skipttest(..)}
 * to the given test method.
 *
 * @author Karl Heinz Marbaise
 */
@MavenJupiterExtension
@SystemProperty("skipTests")
class PropertiesOnNestedClassIT {

  @Nested
  class Level1 {
    @MavenTest
    void property_skiptests(MavenExecutionResult result) {
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
              "Tests are skipped.",
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

  }

}