package org.apache.maven.it.examples;

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

import org.apache.maven.jupiter.extension.MavenIT;
import org.apache.maven.jupiter.extension.MavenRepository;
import org.apache.maven.jupiter.extension.MavenTest;
import org.apache.maven.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

/**
 * The {@code @Execution(ExecutionMode.SAME_THREAD} needs to be defined cause otherwise all three test cases run in
 * parallel and will influence each other which would result in failures.
 *
 * Based on convenience we have put the usage of {@code @Execution(ExecutionMode.SAME_THREAD} into the
 * {@link MavenRepository} annotation which makes it easier to use.
 */
@MavenIT
@MavenRepository
class MavenIntegrationExampleNestedGlobalRepoIT {

  @BeforeEach
  void beforeEachOne(MavenExecutionResult result) {
    System.out.println("beforeEachOne: result = " + result);
    System.out.println("beforeEachOne: MavenIntegrationExampleNestedGlobalRepoIT.beforeEach");
  }

  @BeforeEach
  void beforeEachTwo(MavenExecutionResult result) {
    System.out.println("beforeEachTwo: result = " + result);
    System.out.println("beforeEachTwo: MavenIntegrationExampleNestedGlobalRepoIT.beforeEach");
  }

  @MavenTest
  void packaging_includes(MavenExecutionResult result) {
  }

  @Nested
  class NestedExample {

    @MavenTest
    void basic(MavenExecutionResult executor) {
    }

    @MavenTest
    void packaging_includes(MavenExecutionResult result) {
      // ..
    }

  }

}
