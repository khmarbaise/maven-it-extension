package com.soebes.itf.examples;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;
import static com.soebes.itf.jupiter.maven.MavenVersion.M3_0_5;
import static com.soebes.itf.jupiter.maven.MavenVersion.M3_3_1;

import java.io.IOException;
import java.nio.file.Files;

import org.junit.jupiter.api.Disabled;

import com.soebes.itf.jupiter.extension.EnabledForMavenVersion;
import com.soebes.itf.jupiter.extension.MavenIT;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenCacheResult;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenLog;

@MavenIT
@EnabledForMavenVersion({M3_0_5, M3_3_1})
@Disabled
class SecondMavenIntegrationIT {

  @MavenTest(activeProfiles = {"run-its"})
  void first_integration_test(MavenExecutionResult result, MavenLog mavenLog, MavenCacheResult mavenCacheResult)
      throws IOException {
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