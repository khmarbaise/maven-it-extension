package com.soebes.itf.examples;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import com.soebes.itf.jupiter.extension.MavenIT;
import com.soebes.itf.jupiter.extension.MavenProject;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@MavenIT
@MavenProject("test_project_root")
@TestMethodOrder(OrderAnnotation.class)
class MavenProjectRootIT {

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

  @MavenTest(goals = "verify")
  @Order(30)
  void a_third_part(MavenExecutionResult result) {
    System.out.println("(basic) result = " + result);
  }

}
