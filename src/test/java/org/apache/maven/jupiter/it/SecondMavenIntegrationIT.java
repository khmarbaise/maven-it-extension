package org.apache.maven.jupiter.it;

import static org.apache.maven.jupiter.it.assertj.MavenExecutionResultAssert.assertThat;
import static org.apache.maven.jupiter.it.extension.maven.MavenVersion.M3_0_5;
import static org.apache.maven.jupiter.it.extension.maven.MavenVersion.M3_3_1;

import org.apache.maven.jupiter.it.extension.maven.MavenExecutionResult;
import org.apache.maven.jupiter.it.extension.MavenIT;
import org.apache.maven.jupiter.it.extension.MavenTest;
import org.apache.maven.jupiter.it.extension.maven.MavenCache;
import org.junit.jupiter.api.condition.DisabledOnJre;
import org.junit.jupiter.api.condition.JRE;

@MavenIT(mavenCache = MavenCache.Global, versions = {M3_0_5, M3_3_1})
@DisabledOnJre({JRE.JAVA_11})
class SecondMavenIntegrationIT {

  @MavenTest(profiles = {"run-its"})
  void first_integration_test(MavenExecutionResult result) {
    System.out.println("MavenIntegrationIT.first_integration_test");
    assertThat(result).isFailure();
  }

}