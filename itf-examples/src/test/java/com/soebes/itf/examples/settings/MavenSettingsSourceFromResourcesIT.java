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
 * An example to generate an integration test where we consume the settings-resource-it.xml from the
 * {@code resources-its} directory.
 *
 * @author Karl Heinz Marbaise
 */
@MavenJupiterExtension
@MavenSettingsSources(settingsXml = "settings-resource-it.xml")
class MavenSettingsSourceFromResourcesIT {

  @MavenTest
  @MavenDebug
  void the_given_project(MavenExecutionResult result) {
    Path targetProjectDirectory = result.getMavenProjectResult().getTargetProjectDirectory();
    Path specified_settings = targetProjectDirectory.resolve("settings-resource-it.xml");

    Model model = result.getMavenProjectResult().getModel();
    assertThat(model).satisfies(m -> {
      Assertions.assertThat(m.getGroupId()).isEqualTo("com.soebes.itf.examples.settings");
      Assertions.assertThat(m.getArtifactId()).isEqualTo("maven-settings-source-basic-it-001");
      Assertions.assertThat(m.getVersion()).isEqualTo("1.0.0");
    });

    assertThat(result)
        .isSuccessful()
        .out()
        .debug()
        .contains("Reading user settings from " + specified_settings);

    assertThat(result)
        .isSuccessful()
        .out()
        .warn()
        .contains("The requested profile \"PROFILE_FROM_RESOURCES_ITS\" could not be activated because it does not exist.");
  }

}
