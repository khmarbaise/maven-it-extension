package com.soebes.itf.jupiter.extension;

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

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;

import static org.assertj.core.api.Assertions.assertThat;

class ModelReaderTest {

  private ModelReader modelReader;

  @Nested
  @DisplayName("A pom file which contains artifactId and version")
  class PomWithoutGroupId {

    @BeforeEach
    void beforeEach() throws IOException, XmlPullParserException {
      InputStream resourceAsStream = this.getClass().getResourceAsStream("/pom.xml");
      PomReader pomReader = new PomReader(resourceAsStream);
      modelReader = new ModelReader(pomReader.getModel());
    }

    @Test
    @DisplayName("should get the correct version.")
    void get_version_should_result_in_correct_version() {
      assertThat(modelReader.getVersion()).isEqualTo("2.8-SNAPSHOT");
    }

    @Test
    @DisplayName("should get the correct artifactId.")
    void get_artifactId_should_result_in_correct_artifactId() {
      assertThat(modelReader.getArtifactId()).isEqualTo("versions-maven-plugin");
    }

    @Test
    @DisplayName("should get the groupId from parent.")
    void get_groupId_should_return_groupId() {
      assertThat(modelReader.getGroupId()).isEqualTo("org.codehaus.mojo");
    }

  }

  @Nested
  @DisplayName("A pom file which contains groupId,artifactId and version")
  class PomWithGAV {

    @BeforeEach
    void beforeEach() throws IOException, XmlPullParserException {
      InputStream resourceAsStream = this.getClass().getResourceAsStream("/pom-correct.xml");
      PomReader pomReader = new PomReader(resourceAsStream);
      modelReader = new ModelReader(pomReader.getModel());
    }

    @Test
    @DisplayName("should get the correct version.")
    void get_version_should_result_in_correct_version() {
      assertThat(modelReader.getVersion()).isEqualTo("2.8-SNAPSHOT");
    }

    @Test
    @DisplayName("should get the correct artifactId.")
    void get_artifactId_should_result_in_correct_artifactId() {
      assertThat(modelReader.getArtifactId()).isEqualTo("versions-maven-plugin");
    }

    @Test
    @DisplayName("should get the groupId")
    void get_groupId_should_return_groupId() {
      assertThat(modelReader.getGroupId()).isEqualTo("org.codehaus.dela");
    }

  }

  @Nested
  @DisplayName("A pom file which contains no version but the parent does.")
  class PomWithoutVersion {

    @BeforeEach
    void beforeEach() throws IOException, XmlPullParserException {
      InputStream resourceAsStream = this.getClass().getResourceAsStream("/pom-version.xml");
      PomReader pomReader = new PomReader(resourceAsStream);
      modelReader = new ModelReader(pomReader.getModel());
    }

    @Test
    @DisplayName("should get the version of the parent.")
    void get_version_should_result_in_correct_version() {
      assertThat(modelReader.getVersion()).isEqualTo("50");
    }

    @Test
    @DisplayName("should get the correct artifactId.")
    void get_artifactId_should_result_in_correct_artifactId() {
      assertThat(modelReader.getArtifactId()).isEqualTo("versions-maven-plugin");
    }

    @Test
    @DisplayName("should get the groupId from parent.")
    void get_groupId_should_return_groupId() {
      assertThat(modelReader.getGroupId()).isEqualTo("org.codehaus.dela");
    }

  }

}