package org.apache.maven.it;

import org.apache.maven.jupiter.extension.MavenIT;
import org.apache.maven.jupiter.extension.MavenTest;
import org.apache.maven.jupiter.maven.MavenExecutionResult;
import org.apache.maven.jupiter.maven.MavenExecutor;
import org.junit.jupiter.api.BeforeEach;

@MavenIT
class ITWithBeforeEachIT {

  @BeforeEach
  void beforeEach(MavenExecutor executor) {
    System.out.println("* beforeEach of ITWithBeforeEachIT");
    //System.out.println("* result = " + executor);
  }

  @MavenTest
  void the_first_test_case(MavenExecutionResult result) {
    System.out.println("result = " + result);
  }
}
