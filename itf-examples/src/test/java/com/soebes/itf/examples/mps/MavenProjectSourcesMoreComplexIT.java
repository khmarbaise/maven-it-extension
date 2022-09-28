package com.soebes.itf.examples.mps;

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

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenProjectSources;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenProjectResult;
import org.apache.maven.model.Model;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
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
 * Example integration test for programmatically generated project setup
 * with a dynamically generated parts.
 *
 * @author Karl Heinz Marbaise
 */
@MavenJupiterExtension
@MavenProjectSources(resourcesUsage = NONE)
class MavenProjectSourcesMoreComplexIT {

  private List<String> generateProject(String info) {
    return Arrays.asList(
        "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">",
        "<modelVersion>4.0.0</modelVersion>",
        "  <groupId>project-sources-it</groupId>",
        "  <artifactId>" + String.format("%s", info) + "</artifactId>",
        "  <version>1.0.0</version>",
        "  <properties>",
        "    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>",
        "  </properties>",
        "</project>"
    );
  }
  @BeforeEach
  void beforeEach(TestInfo testInfo, MavenProjectResult result) throws IOException {
    Class<?> testMethod = testInfo.getTestClass().orElseThrow(IllegalStateException::new);
    String artifactId = testMethod.getName().replace('$', '_');
    Path pomFile = result.getTargetProjectDirectory().resolve("pom.xml");
    Files.write(pomFile, generateProject(artifactId), StandardOpenOption.CREATE);
  }

  @MavenTest
  void root_generated(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
    Model model = result.getMavenProjectResult().getModel();
    assertThat(model).satisfies(m -> {
      assertThat(m.getGroupId()).isEqualTo("project-sources-it");
      assertThat(m.getArtifactId()).isEqualTo("com.soebes.itf.examples.mps.MavenProjectSourcesMoreComplexIT");
      assertThat(m.getVersion()).isEqualTo("1.0.0");
    });
  }

  @Nested
  class Level1 {
    @Nested
    class Level2 {
      @Nested
      class Level3 {
        @MavenTest
        void level_3_generated(MavenExecutionResult result) {
          assertThat(result).isSuccessful();
          Model model = result.getMavenProjectResult().getModel();
          assertThat(model).satisfies(m -> {
            assertThat(m.getGroupId()).isEqualTo("project-sources-it");
            assertThat(m.getArtifactId()).isEqualTo("com.soebes.itf.examples.mps.MavenProjectSourcesMoreComplexIT_Level1_Level2_Level3");
            assertThat(m.getVersion()).isEqualTo("1.0.0");
          });
        }

      }
      @MavenTest
      void level_2_generated(MavenExecutionResult result) {
        assertThat(result).isSuccessful();
        Model model = result.getMavenProjectResult().getModel();
        assertThat(model).satisfies(m -> {
          assertThat(m.getGroupId()).isEqualTo("project-sources-it");
          assertThat(m.getArtifactId()).isEqualTo("com.soebes.itf.examples.mps.MavenProjectSourcesMoreComplexIT_Level1_Level2");
          assertThat(m.getVersion()).isEqualTo("1.0.0");
        });
      }

    }
    @MavenTest
    void level_1_generated(MavenExecutionResult result) {
      assertThat(result).isSuccessful();
      Model model = result.getMavenProjectResult().getModel();
      assertThat(model).satisfies(m -> {
        assertThat(m.getGroupId()).isEqualTo("project-sources-it");
        assertThat(m.getArtifactId()).isEqualTo("com.soebes.itf.examples.mps.MavenProjectSourcesMoreComplexIT_Level1");
        assertThat(m.getVersion()).isEqualTo("1.0.0");
      });
    }

  }


}