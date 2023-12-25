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
import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenOption;
import com.soebes.itf.jupiter.extension.MavenProfile;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.Nested;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

/**
 * Checking how annotations like {@link MavenOption}, {@link MavenGoal}, {@link MavenProfile} and {@link SystemProperty}
 * are applied to class- method or nested class levels.
 *
 * @author Karl Heinz Marbaise
 */
@MavenJupiterExtension
@MavenGoal({"help:evaluate"})
@SystemProperty(value = "expression", content = "project.version")
@SystemProperty("forceStdout")
@MavenOption(MavenCLIOptions.NON_RECURSIVE)
@MavenOption(MavenCLIOptions.QUIET)
@MavenProfile("pbase")
class AnnotationOnClassAndInheritedIT {

  @MavenTest
  @MavenProfile("on_class_level")
  void on_class_level(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .plain()
        .contains("1.0-SNAPSHOT");
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().isEmpty();
    assertThat(result)
        .isSuccessful()
        .out()
        .debug().isEmpty();

    var argumentsLog = result.getMavenProjectResult().getTargetBaseDirectory().resolve("mvn-arguments.log");

    assertThat(argumentsLog)
        .content()
        .containsSubsequence(
            "-Pon_class_level,pbase",
            "-Dexpression=project.version",
            "-DforceStdout",
            "--non-recursive",
            "--quiet",
            "help:evaluate"
        );
  }

  @MavenTest
  @MavenOption(MavenCLIOptions.FAIL_AT_END)
  @SystemProperty("anotherProperty")
  @MavenProfile("pmethod")
  void on_method_level(MavenExecutionResult result) {
    assertThat(result)
        .isSuccessful()
        .out()
        .plain()
        .contains("1.0-SNAPSHOT");
    assertThat(result)
        .isSuccessful()
        .out()
        .warn().isEmpty();
    assertThat(result)
        .isSuccessful()
        .out()
        .debug().isEmpty();
    var argumentsLog = result.getMavenProjectResult().getTargetBaseDirectory().resolve("mvn-arguments.log");

    assertThat(argumentsLog)
        .content()
        .containsSubsequence(
            "-Ppmethod,pbase",
            "-DanotherProperty",
            "-Dexpression=project.version",
            "-DforceStdout",
            "--fail-at-end",
            "--non-recursive",
            "--quiet",
            "help:evaluate"
        );
  }

  @Nested
  @SystemProperty("NestedClass")
  @MavenProfile("pNestedClass")
  class NestedClass {
    @MavenTest
    @MavenOption(MavenCLIOptions.FAIL_NEVER)
    @SystemProperty("anotherPropertyOnNestedClass")
    @SystemProperty("on_nested_class_level")
    void on_nested_class_level(MavenExecutionResult result) {
      assertThat(result)
          .isSuccessful()
          .out()
          .plain()
          .contains("1.0-SNAPSHOT");
      assertThat(result)
          .isSuccessful()
          .out()
          .warn().isEmpty();
      assertThat(result)
          .isSuccessful()
          .out()
          .debug().isEmpty();
      var argumentsLog = result.getMavenProjectResult().getTargetBaseDirectory().resolve("mvn-arguments.log");

      assertThat(argumentsLog)
          .content()
          .containsSubsequence(
              "-PpNestedClass,pbase",
              "-DanotherPropertyOnNestedClass",
              "-Don_nested_class_level",
              "-DNestedClass",
              "-Dexpression=project.version",
              "-DforceStdout",
              "--fail-never",
              "--non-recursive",
              "--quiet",
              "help:evaluate"
          );
    }

    @Nested
    @MavenOption(MavenCLIOptions.ERRORS)
    @SystemProperty("SecondLevel")
    class SecondLevel {
      @MavenTest
      @MavenOption(MavenCLIOptions.FAIL_AT_END)
      @SystemProperty("anotherPropertyOnSecondLevelNestedClass")
      void on_second_level_nested_class_level(MavenExecutionResult result) {
        assertThat(result)
            .isSuccessful()
            .out()
            .plain()
            .contains("1.0-SNAPSHOT");
        assertThat(result)
            .isSuccessful()
            .out()
            .warn().isEmpty();
        assertThat(result)
            .isSuccessful()
            .out()
            .debug().isEmpty();
        var argumentsLog = result.getMavenProjectResult().getTargetBaseDirectory().resolve("mvn-arguments.log");

        assertThat(argumentsLog)
            .content()
            .containsSubsequence(
                "-PpNestedClass,pbase",
                "-DanotherPropertyOnSecondLevelNestedClass",
                "-DSecondLevel",
                "-DNestedClass",
                "-Dexpression=project.version",
                "-DforceStdout",
                "--fail-at-end",
                "--errors",
                "--non-recursive",
                "--quiet",
                "help:evaluate"
            );
      }

      @Nested
      @SystemProperty("ThirdLevel")
      @MavenProfile("pThirdLevel")
      class ThirdLevel {
        @MavenTest
        @MavenOption(MavenCLIOptions.FAIL_NEVER)
        @MavenGoal("help:system")
        @SystemProperty("anotherPropertyOnSecondLevelNestedClass")
        void on_third_level_nested_class_level(MavenExecutionResult result) {
          assertThat(result)
              .isSuccessful()
              .out()
              .plain()
              .contains("1.0-SNAPSHOT");
          assertThat(result)
              .isSuccessful()
              .out()
              .warn().isEmpty();
          assertThat(result)
              .isSuccessful()
              .out()
              .debug().isEmpty();
          var argumentsLog = result.getMavenProjectResult().getTargetBaseDirectory().resolve("mvn-arguments.log");

          assertThat(argumentsLog)
              .content()
              .containsSubsequence(
                  "-PpThirdLevel,pNestedClass,pbase",
                  "-DanotherPropertyOnSecondLevelNestedClass",
                  "-DThirdLevel",
                  "-DSecondLevel",
                  "-Dexpression=project.version",
                  "-DforceStdout",
                  "--fail-never",
                  "--errors",
                  "--non-recursive",
                  "--quiet",
                  "help:system",
                  "help:evaluate"
              );
        }

      }
    }
  }

}