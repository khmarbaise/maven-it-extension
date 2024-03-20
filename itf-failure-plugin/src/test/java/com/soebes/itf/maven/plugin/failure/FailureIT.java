package com.soebes.itf.maven.plugin.failure;

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
import static org.assertj.core.api.Assertions.atIndex;

@MavenJupiterExtension
class FailureIT {

    @MavenTest
  @DisplayName("The basic configuration should result in a successful build.")
  void basic_configuration(MavenExecutionResult project) {
    assertThat(project).isSuccessful().out().warn().contains("Neither executionException nor failureException has been set.");
  }

  @MavenTest
  void fail_with_mojo_execution_exception(MavenExecutionResult result) {
    assertThat(result).isFailure();

    assertThat(result).out().info().containsSequence("BUILD FAILURE");

    //TODO: Is there a simpler way to do this? Can we somehow create better support for that?
    assertThat(result).out().error().filteredOn(s -> s.startsWith("Failed to execute goal ")).satisfies(s -> {
      assertThat(s).startsWith("Failed to execute goal com.soebes.itf.jupiter.extension:itf-failure-plugin:");
      assertThat(s).endsWith(":failure (fail_the_build) on project fail_the_build: This is the ExecutionException. -> [Help 1]");
    }, atIndex(0));

    assertThat(result).out().error().containsOnlyOnce("[Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoExecutionException");

  }

  @MavenTest
  void fail_with_mojo_failure_exception(MavenExecutionResult result) {
    assertThat(result).isFailure();

    assertThat(result).out().info().containsSequence("BUILD FAILURE");

    //TODO: Is there a simpler way to do this? Can we somehow create better support for that?
    assertThat(result).out().error().filteredOn(s -> s.startsWith("Failed to execute goal ")).satisfies(s -> {
      assertThat(s).startsWith("Failed to execute goal com.soebes.itf.jupiter.extension:itf-failure-plugin:");
      assertThat(s).endsWith(":failure (fail_the_build) on project fail_the_build: This is the FailureException. -> [Help 1]");
    }, atIndex(0));

    assertThat(result).out().error().containsOnlyOnce("[Help 1] http://cwiki.apache.org/confluence/display/MAVEN/MojoFailureException");

  }
}
