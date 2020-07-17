package com.soebes.itf.examples;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import com.soebes.itf.jupiter.extension.condition.EnabledForMavenVersion;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenCacheResult;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenLog;
import org.junit.jupiter.api.Disabled;

import java.io.IOException;
import java.nio.file.Files;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;
import static com.soebes.itf.jupiter.extension.MavenVersion.M3_0_5;
import static com.soebes.itf.jupiter.extension.MavenVersion.M3_3_1;

@MavenJupiterExtension
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