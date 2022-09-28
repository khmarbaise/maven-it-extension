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
import com.soebes.itf.jupiter.extension.MavenProjectSources;
import com.soebes.itf.jupiter.extension.MavenSettingsSources;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenProjectResult;
import org.apache.maven.model.Model;
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
 * An example to generate an integration test project inclusive a supplemental {@code settings.xml}.
 * The {@code settings.xml} is generate in the {@code project} directory by the integration
 * test in the {@link #beforeEach(TestInfo, MavenProjectResult)} method.
 *
 * @author Karl Heinz Marbaise
 */
@MavenJupiterExtension
@MavenProjectSources(resourcesUsage = NONE)
@MavenSettingsSources(resourcesUsage = NONE)
class MavenSettingsSourceBasicIT {

  private static final List<String> POM_STATIC = Arrays.asList(
      "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">",
      "<modelVersion>4.0.0</modelVersion>",
      "  <groupId>com.soebes.itf.examples.settings</groupId>",
      "  <artifactId>maven-settings-source-basic-it-001</artifactId>",
      "  <version>1.0.0</version>",
      "  <properties>",
      "   <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>",
      "  </properties>",
      "  <build>",
      "    <pluginManagement>",
      "      <plugins>",
      "        <plugin>",
      "          <groupId>org.apache.maven.plugins</groupId>",
      "          <artifactId>maven-clean-plugin</artifactId>",
      "          <version>3.2.0</version>",
      "        </plugin>",
      "        <plugin>",
      "          <groupId>org.apache.maven.plugins</groupId>",
      "          <artifactId>maven-compiler-plugin</artifactId>",
      "          <version>3.10.1</version>",
      "        </plugin>",
      "        <plugin>",
      "          <groupId>org.apache.maven.plugins</groupId>",
      "          <artifactId>maven-resources-plugin</artifactId>",
      "          <version>3.3.0</version>",
      "        </plugin>",
      "        <plugin>",
      "          <groupId>org.apache.maven.plugins</groupId>",
      "          <artifactId>maven-jar-plugin</artifactId>",
      "          <version>3.3.0</version>",
      "        </plugin>",
      "        <plugin>",
      "          <groupId>org.apache.maven.plugins</groupId>",
      "          <artifactId>maven-surefire-plugin</artifactId>",
      "          <version>3.0.0-M7</version>",
      "        </plugin>",
      "      </plugins>",
      "    </pluginManagement>",
      "  </build>",
      "</project>"
  );

  private static final List<String> SETTINGS_STATIC = Arrays.asList(
      "<settings xmlns=\"http://maven.apache.org/SETTINGS/1.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"",
      "  xsi:schemaLocation=\"http://maven.apache.org/SETTINGS/1.0.0 https://maven.apache.org/xsd/settings-1.0.0.xsd\">",
      "  <activeProfiles>",
      "    <activeProfile>TESTPROFILE</activeProfile>",
      "  </activeProfiles>",
      "</settings>"
  );

  @BeforeEach
  void beforeEach(TestInfo testInfo, MavenProjectResult result) throws IOException {
    Path pomFile = result.getTargetProjectDirectory().resolve("pom.xml");
    Path settingsFile = result.getTargetProjectDirectory().resolve("settings.xml");
    Files.write(settingsFile, SETTINGS_STATIC, StandardOpenOption.CREATE);
    Files.write(pomFile, POM_STATIC, StandardOpenOption.CREATE);
  }

  @MavenTest
  @MavenDebug
  void generated_project(MavenExecutionResult result) {
    Path targetProjectDirectory = result.getMavenProjectResult().getTargetProjectDirectory();
    Path specified_settings = targetProjectDirectory.resolve("settings.xml");

    Model model = result.getMavenProjectResult().getModel();
    assertThat(model).satisfies(m -> {
      assertThat(m.getGroupId()).isEqualTo("com.soebes.itf.examples.settings");
      assertThat(m.getArtifactId()).isEqualTo("maven-settings-source-basic-it-001");
      assertThat(m.getVersion()).isEqualTo("1.0.0");
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
        .contains("The requested profile \"TESTPROFILE\" could not be activated because it does not exist.");
  }

}
