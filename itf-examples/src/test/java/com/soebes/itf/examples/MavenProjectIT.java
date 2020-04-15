package com.soebes.itf.examples;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import com.soebes.itf.jupiter.extension.MavenIT;
import com.soebes.itf.jupiter.extension.MavenProject;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@MavenIT
class MavenProjectIT {

  @MavenTest
  void packaging_includes(MavenExecutionResult result) {
  }

  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  @MavenProject("test_project")
  class NestedExample {

    @MavenTest(goals = "clean")
    @Order(10)
    void basic(MavenExecutionResult result) {
      System.out.println("(basic) result = " + result);
    }

    @MavenTest(goals = "install")
    @Order(20)
    void packaging_includes(MavenExecutionResult result) {
      System.out.println("(packaging_includes) result = " + result);
    }

  }


}
