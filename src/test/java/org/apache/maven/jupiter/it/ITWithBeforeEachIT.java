package org.apache.maven.jupiter.it;

import org.apache.maven.jupiter.extension.MavenIT;
import org.apache.maven.jupiter.extension.MavenTest;
import org.apache.maven.jupiter.extension.maven.MavenExecutionResult;
import org.junit.jupiter.api.BeforeEach;

@MavenIT
class ITWithBeforeEachIT {

  @BeforeEach
  void beforeEach(MavenExecutionResult result) {
  }

  @MavenTest
  void the_first_test_case(MavenExecutionResult result) {
  }
}
