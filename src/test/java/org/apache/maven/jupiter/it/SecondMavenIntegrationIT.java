package org.apache.maven.jupiter.it;

import static org.apache.maven.jupiter.it.assertj.MavenExecutionResultAssert.assertThat;
import static org.apache.maven.jupiter.it.extension.maven.MavenVersion.M3_0_5;
import static org.apache.maven.jupiter.it.extension.maven.MavenVersion.M3_3_1;

import org.apache.maven.jupiter.it.extension.MavenIT;
import org.apache.maven.jupiter.it.extension.MavenTest;
import org.apache.maven.jupiter.it.extension.maven.MavenExecutionResult;

@MavenIT(versions = {M3_0_5, M3_3_1})
class SecondMavenIntegrationIT {

  @MavenTest(profiles = {"run-its"})
  void first_integration_test(MavenExecutionResult result) {
    System.out.println("MavenIntegrationIT.first_integration_test");
    assertThat(result).isFailure();
  }

}