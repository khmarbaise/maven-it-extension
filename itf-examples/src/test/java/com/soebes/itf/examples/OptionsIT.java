package com.soebes.itf.examples;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;
import static com.soebes.itf.jupiter.extension.MavenOptions.DEBUG;
import static com.soebes.itf.jupiter.extension.MavenOptions.LOG_FILE;

import com.soebes.itf.jupiter.extension.MavenIT;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

@MavenIT
class OptionsIT {

  // mvn -X --log-file test.log
  // The log file is located within the project directory.
  @MavenTest(options = {DEBUG, LOG_FILE, "test.log"})
  void first_integration_test(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

  // Using two system properties -Daccent=true -Dgolem=three
  @MavenTest(systemProperties = { "accent=true", "golem=three"})
  void second_integration_test(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

}