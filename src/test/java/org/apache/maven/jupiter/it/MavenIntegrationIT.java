package org.apache.maven.jupiter.it;

import static org.apache.maven.jupiter.it.assertj.MavenExecutionResultAssert.assertThat;
import static org.apache.maven.jupiter.it.extension.maven.MavenVersion.M3_0_5;
import static org.apache.maven.jupiter.it.extension.maven.MavenVersion.M3_3_1;

import org.apache.maven.jupiter.it.extension.MavenExecutionResult;
import org.apache.maven.jupiter.it.extension.MavenIT;
import org.apache.maven.jupiter.it.extension.MavenTest;
import org.apache.maven.jupiter.it.extension.maven.MavenCache;

/**
 * @author Karl Heinz Marbaise
 */
@MavenIT(mavenCache = MavenCache.Global, versions = {M3_0_5, M3_3_1})
class MavenIntegrationIT {

  //  @BeforeAll
  //  void beforeAll() {
  //  }

  //  @BeforeEach
  //  void beforeEach() {
  //    // How to install one or more setup Project's before each test.
  //  }
  //
  @MavenTest(debug = true)
  void first_integration_test(MavenExecutionResult result) {
    System.out.println("MavenIntegrationIT.first_integration_test rc:" + result.getReturnCode());
    assertThat(result).isSuccessful();
  }

}
