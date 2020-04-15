package com.soebes.itf.examples;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;

import com.soebes.itf.jupiter.extension.MavenIT;
import com.soebes.itf.jupiter.extension.MavenRepository;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * The following test cases are related together cause they are sharing a common cache {@link MavenRepository}.
 * Furthermore the {@code setup*} cases running in a given order (defined by {@link OrderAnnotation}).
 *
 * @author Karl Heinz Marbaise
 */
@MavenIT(goals = {"install"})
@MavenRepository
@TestMethodOrder(OrderAnnotation.class)
@DisplayName("This is integration test Nr.1 with two setup projects.")
class MavenIntegrationIT {

  @MavenTest
  @Order(10)
  @DisplayName("where setup one is needed.")
  void setup(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

  @MavenTest
  @Order(20)
  @DisplayName("where setup two is needed.")
  void setup_2(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

  @MavenTest(debug = true, goals = {"clean", "verify"})
  @DisplayName("and the test case tries to check for resultion issue.")
  void first_integration_test(MavenExecutionResult result) {
    System.out.println("MavenIntegrationIT.first_integration_test rc:" + result.getReturnCode());
    assertThat(result).isSuccessful();
  }

}
