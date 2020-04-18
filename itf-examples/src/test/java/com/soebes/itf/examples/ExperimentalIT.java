package com.soebes.itf.examples;

import com.soebes.itf.jupiter.extension.MavenIT;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.Disabled;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

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
