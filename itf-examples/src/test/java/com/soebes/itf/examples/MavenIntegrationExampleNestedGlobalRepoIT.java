package com.soebes.itf.examples;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import com.soebes.itf.jupiter.extension.MavenIT;
import com.soebes.itf.jupiter.extension.MavenRepository;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

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
