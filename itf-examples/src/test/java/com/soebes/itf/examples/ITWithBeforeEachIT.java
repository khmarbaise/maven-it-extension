package com.soebes.itf.examples;

import org.junit.jupiter.api.BeforeEach;

import com.soebes.itf.jupiter.extension.MavenIT;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenExecutor;

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
