package org.apache.maven.it;

import static org.apache.maven.assertj.MavenITAssertions.assertThat;

import org.apache.maven.jupiter.extension.MavenIT;
import org.apache.maven.jupiter.extension.MavenTest;
import org.apache.maven.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.Disabled;

@MavenIT
@Disabled
class ExperimentalIT {

  @MavenTest
  void first(MavenExecutionResult result) {
    assertThat(result).isSuccessful().log();
    assertThat(result).isSuccessful().project().hasTarget();
    assertThat(result).isSuccessful().cache();
  }

  @MavenTest
  void second(MavenExecutionResult result) {
    assertThat(result).log();
    assertThat(result).project().hasTarget().withEarFile();
    assertThat(result).project().hasTarget().withJarFile();
    assertThat(result).project().hasTarget().withWarFile();
    assertThat(result).cache();
  }
}
