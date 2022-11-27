package com.soebes.itf.examples.settings;

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

import com.soebes.itf.jupiter.extension.MavenDebug;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenSettingsSources;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenProjectResult;
import org.apache.maven.model.Model;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.TestInfo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.List;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;
import static com.soebes.itf.jupiter.extension.ResourceUsage.NONE;

/**
 * An example to generate an integration test where only a {@code settings.xml} is being generated.
 * It is intentionally named different.
 *
 * The project is given via the {@code resources-its} content and {@code NOT} being generated with
 * {@link #beforeEach(TestInfo, MavenProjectResult)}.
 *
 * @author Karl Heinz Marbaise
 */
@MavenJupiterExtension
@MavenSettingsSources(resourcesUsage = NONE)
class MavenSettingsSourceGenerateSettingsOnlyDifferentNameIT {

  private static final List<String> SETTINGS_STATIC = Arrays.asList(
      "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"",
      "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd\">",
      "  <activeProfiles>",
      "    <activeProfile>TESTPROFILE_FROM</activeProfile>",
      "  </activeProfiles>",
      "</settings>"
  );

  @BeforeEach
  void beforeEach(TestInfo testInfo, MavenProjectResult result) throws IOException {
    Path settingsFile = result.getTargetProjectDirectory().resolve("settings-from.xml");
    Files.write(settingsFile, SETTINGS_STATIC, StandardOpenOption.CREATE);
  }

  @MavenTest
  @MavenDebug
  void the_given_project(MavenExecutionResult result) {
    Path targetProjectDirectory = result.getMavenProjectResult().getTargetProjectDirectory();
    Path specified_settings = targetProjectDirectory.resolve("settings.xml");

    Model model = result.getMavenProjectResult().getModel();
    assertThat(model).satisfies(m -> {
      Assertions.assertThat(m.getGroupId()).isEqualTo("com.soebes.itf.examples.settings");
      Assertions.assertThat(m.getArtifactId()).isEqualTo("maven-settings-source-basic-it-001");
      Assertions.assertThat(m.getVersion()).isEqualTo("1.0.0");
    });

    /*
        [DEBUG] Reading global settings from /Users/khm/tools/maven/conf/settings.xml
        [DEBUG] Reading user settings from /Users/khm/.m2/settings.xml
        [DEBUG] Reading global toolchains from /Users/khm/tools/maven/conf/toolchains.xml
        [DEBUG] Reading user toolchains from /Users/khm/.m2/toolchains.xml
        [DEBUG] Using local repository at /Users/khm/ws-git-soebes/maven-it-extension/itf-examples/target/maven-it/com/soebes/itf/examples/settings/MavenSettingsSourceBasicIT/generated_project/.m2/repository
     */
    assertThat(result)
        .isSuccessful()
        .out()
        .debug()
        .contains("Reading user settings from " + specified_settings);

    assertThat(result)
        .isSuccessful()
        .out()
        .warn()
        .contains("The requested profile \"TESTPROFILE_FROM\" could not be activated because it does not exist.");
  }

}
