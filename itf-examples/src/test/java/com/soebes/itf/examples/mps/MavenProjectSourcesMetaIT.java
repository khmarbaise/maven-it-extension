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

/**
 * Example integration test to demonstrate the usage
 * of {@code @MavenProjectSources(resourcesUsage = NONE)}
 * and create the appropriate project setup fully programmatically from within `@BeforeEach`
 * method. This means we do not have to have a source project setup in
 * `src/test/resources-its/com/soebes/itf/examples/mps/MavenProjectSourcesBasicIT/project_001`.
 *
 * @author Karl Heinz Marbaise
 */
@Programmatically
class MavenProjectSourcesMetaIT {

  private static final List<String> POM_STATIC = Arrays.asList(
      "<project xmlns=\"http://maven.apache.org/POM/4.0.0\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://maven.apache.org/POM/4.0.0 http://maven.apache.org/maven-v4_0_0.xsd\">",
      "<modelVersion>4.0.0</modelVersion>",
      "  <groupId>project-sources-it-meta-annotation</groupId>",
      "  <artifactId>project-sources-it-artifactid-001</artifactId>",
      "  <version>1.0.0</version>",
      "</project>"
  );

  @BeforeEach
  void beforeEach(TestInfo testInfo, MavenProjectResult result) throws IOException {
    Path pomFile = result.getTargetProjectDirectory().resolve("pom.xml");
    Files.write(pomFile, POM_STATIC, StandardOpenOption.CREATE);
  }

  @MavenTest
  void generated_project(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
    Model model = result.getMavenProjectResult().getModel();
    assertThat(model).satisfies(m -> {
      assertThat(m.getGroupId()).isEqualTo("project-sources-it-meta-annotation");
      assertThat(m.getArtifactId()).isEqualTo("project-sources-it-artifactid-001");
      assertThat(m.getVersion()).isEqualTo("1.0.0");
    });
  }


}
