package com.soebes.itf.examples;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

import org.junit.jupiter.api.Disabled;

import com.soebes.itf.jupiter.extension.MavenIT;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

@MavenIT
@Disabled("Currently disabled based on MEAR-566")
class SixthIT {

  @MavenTest
  void basic(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

}