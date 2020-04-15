package com.soebes.itf.examples;

import org.junit.jupiter.api.Nested;

import com.soebes.itf.jupiter.extension.MavenIT;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

@MavenIT
class MavenIntegrationExampleNestedIT {

  @MavenTest
  void packaging_includes(MavenExecutionResult result) {
  }

  @Nested
  class NestedExample {

    @MavenTest
    void basic(MavenExecutionResult result) {
    }

    @MavenTest
    void packaging_includes(MavenExecutionResult result) {
    }

  }

}
