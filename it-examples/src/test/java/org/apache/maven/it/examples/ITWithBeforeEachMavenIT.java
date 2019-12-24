package org.apache.maven.it.examples;

import org.apache.maven.jupiter.extension.BeforeEachMaven;
import org.apache.maven.jupiter.extension.MavenIT;
import org.apache.maven.jupiter.extension.MavenTest;
import org.apache.maven.jupiter.maven.MavenExecutionResult;

@MavenIT
class ITWithBeforeEachMavenIT {

  @BeforeEachMaven
  void beforeEach() {
    System.out.println("* beforeEach of ITWithBeforeEachIT");
    //System.out.println("* result = " + executor);
  }

  @MavenTest
  void the_first_test_case(MavenExecutionResult result) {
    System.out.println("result = " + result);
  }
}
