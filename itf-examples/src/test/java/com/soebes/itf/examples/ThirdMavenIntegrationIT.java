package com.soebes.itf.examples;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.JRE;

import com.soebes.itf.jupiter.extension.MavenIT;
import com.soebes.itf.jupiter.extension.MavenRepository;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

@MavenIT
@MavenRepository
@DisabledOnJre({JRE.JAVA_11})
@Disabled
class ThirdMavenIntegrationIT {

  @MavenTest(activeProfiles = {"run-its"})
  void first_integration_test(MavenExecutionResult result) {
    System.out.println("MavenIntegrationIT.first_integration_test");
    assertThat(result).isFailure();
  }

  @MavenTest
  void second_integration_test_case(MavenExecutionResult result) {
    System.out.println("MavenIntegrationIT.first_integration_test");
    assertThat(result).isFailure();
  }

}