package org.apache.maven.jupiter.it;

import static org.apache.maven.jupiter.it.assertj.MavenExecutionResultAssert.assertThat;

import org.apache.maven.jupiter.it.extension.MavenIT;
import org.apache.maven.jupiter.it.extension.MavenTest;
import org.apache.maven.jupiter.it.extension.maven.MavenCache;
import org.apache.maven.jupiter.it.extension.maven.MavenExecutionResult;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * The following test cases are related together cause they are sharing a common cache. Furthermore the test cases or
 * running in a given order (defined by Order annotation).
 *
 * @author Karl Heinz Marbaise
 */
@MavenIT(mavenCache = MavenCache.Global)
@TestMethodOrder(OrderAnnotation.class)
class MavenIntegrationIT {

  @MavenTest(goals = {"clean", "install"})
  @Order(10)
  void setup(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

  @MavenTest(goals = {"clean", "install"})
  @Order(11)
  void setup_2(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

  @MavenTest(debug = true)
  void first_integration_test(MavenExecutionResult result) {
    System.out.println("MavenIntegrationIT.first_integration_test rc:" + result.getReturnCode());
    assertThat(result).isSuccessful();
  }

}
