package org.apache.maven.jupiter.it;

import static org.apache.maven.jupiter.it.assertj.MavenExecutionResultAssert.assertThat;

import org.apache.maven.jupiter.it.extension.maven.MavenExecutionResult;
import org.apache.maven.jupiter.it.extension.MavenIT;
import org.apache.maven.jupiter.it.extension.MavenTest;
import org.apache.maven.jupiter.it.extension.maven.MavenCache;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * @author Karl Heinz Marbaise
 */
@MavenIT(mavenCache = MavenCache.Global)
@TestMethodOrder(OrderAnnotation.class)
class MavenIntegrationIT {

  @MavenTest(goals = {"clean", "install"})
  @Order(10)
  void before_first(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

  @MavenTest(debug = true)
  @Order(20)
  void first_integration_test(MavenExecutionResult result) {
    System.out.println("MavenIntegrationIT.first_integration_test rc:" + result.getReturnCode());
    assertThat(result).isSuccessful();
  }

}
