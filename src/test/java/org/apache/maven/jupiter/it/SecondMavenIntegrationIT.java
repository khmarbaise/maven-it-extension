package org.apache.maven.jupiter.it;

import static org.apache.maven.jupiter.assertj.MavenExecutionResultAssert.assertThat;
import static org.apache.maven.jupiter.extension.maven.MavenVersion.M3_0_5;
import static org.apache.maven.jupiter.extension.maven.MavenVersion.M3_3_1;

import java.io.IOException;
import java.nio.file.Files;
import org.apache.maven.jupiter.extension.MavenIT;
import org.apache.maven.jupiter.extension.MavenTest;
import org.apache.maven.jupiter.extension.maven.MavenCacheResult;
import org.apache.maven.jupiter.extension.maven.MavenExecutionResult;
import org.apache.maven.jupiter.extension.maven.MavenLog;

@MavenIT(versions = {M3_0_5, M3_3_1})
class SecondMavenIntegrationIT {

  @MavenTest(activeProfiles = {"run-its"})
  void first_integration_test(MavenExecutionResult result, MavenLog mavenLog, MavenCacheResult mavenCacheResult) throws IOException {
    System.out.println("MavenIntegrationIT.first_integration_test");
    assertThat(result).isFailure();
//    assertThat(mavenLog).stdOut().hasWarning();
//    assertThat(mavenLog).stdErr().isEmpty();

//    assertThat(mavenCacheResult).containsArtifact("g:a:v");
//    assertThat(mavenCacheResult).contains().g("G").a("A").v("1.0").c("class").t("type");

    System.out.println("cacheLocation = " + mavenCacheResult.getStdout().toFile().getAbsolutePath());

//    assertThat(result).log()...
//    assertThat(result).cache()...

    // Pre loading log file into memory ? Drawback Memory usage ?
    Files.readAllLines(mavenLog.getStdout())
        .stream()
        .filter(s -> s.startsWith("[WARNING]"))
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Not found WARNING"));
  }

}