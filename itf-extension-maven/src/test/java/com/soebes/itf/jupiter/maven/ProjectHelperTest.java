package com.soebes.itf.jupiter.maven;

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

import org.apache.maven.model.Model;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;

/**
 * @author Karl Heinz Marbaise
 */
class ProjectHelperTest {

  @Test
  void should_read_the_pom_file_without_any_issue() {
    InputStream resourceAsStream = this.getClass().getResourceAsStream("/pom.xml");
    Model model = ProjectHelper.readProject(resourceAsStream);

    assertThat(model.getArtifactId()).isEqualTo("versions-maven-plugin");
    assertThat(model.getVersion()).isEqualTo("2.8-SNAPSHOT");
  }

  @Test
  void fail_to_read_the_pom_file() throws IOException {
    try (InputStream resourceAsStream = this.getClass().getResourceAsStream("/pom-with-issue.xml")) {
      assertThatIllegalStateException().isThrownBy(() -> ProjectHelper.readProject(resourceAsStream))
          .withMessage("Failed to read pom.xml")
          .havingCause()
          .withMessage("expected > to finsh end tag not < from line 32 (position: TEXT seen ...<version>2.8-SNAPSHOT</version\\n  <... @33:4) ");
    }
  }

  @Test
  void should_read_pom_without_any_issue_as_path() {
    Path resourcesDirectory = Paths.get("src", "test", "resources", "pom.xml");
    Model model = ProjectHelper.readProject(resourcesDirectory);

    assertThat(model.getArtifactId()).isEqualTo("versions-maven-plugin");
    assertThat(model.getVersion()).isEqualTo("2.8-SNAPSHOT");
  }

  @Test
  void should_fail() {
    Path resourcesDirectory = Paths.get("src", "test", "resources", "unknown.xml");

    assertThatIllegalStateException().isThrownBy(() -> ProjectHelper.readProject(resourcesDirectory))
        .withMessage("Failed to read pom.xml")
        .havingCause()
        .withMessage("src/test/resources/unknown.xml");

  }

}