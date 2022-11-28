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

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.InjectSoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;


@ExtendWith(MockitoExtension.class)
@ExtendWith(SoftAssertionsExtension.class)
class SettingsTest {

  @Mock
  private ExtensionContext context;
  @Mock
  private DirectoryResolverResult directoryResolverResult;
  @Mock
  private AnnotationHelper annotationHelper;
  @InjectMocks
  private Settings underTest;

  @InjectSoftAssertions
  private SoftAssertions softly;

  @Test
  void noSettingsAnnotationAvailable() {
    try (MockedStatic<AnnotationHelper> annotationHelperStatic = mockStatic(AnnotationHelper.class)) {
      annotationHelperStatic.when(() -> AnnotationHelper.findMavenSettingsSourcesAnnotation(any())).thenReturn(Optional.empty());
      List<String> settings = underTest.settingsSetup();
      assertThat(settings).isEmpty();
    }
  }

  @Test
  void validateTheDefaultValues() {
    MavenSettingsSources annotation = Helper.createAnnotation(this.getClass(), MavenSettingsSources.class);
    softly.assertThat(annotation.sources()).isEmpty();
    softly.assertThat(annotation.settingsXml()).isEqualTo("settings.xml");
    softly.assertThat(annotation.resourcesUsage()).isEqualTo(ResourceUsage.DEFAULT);
  }

  @Test
  void name() {
    MavenSettingsSources annotation = Helper.createAnnotation(this.getClass(), MavenSettingsSources.class);

    System.out.println("loaded = " + annotation.getClass().getName());
    System.out.println("loaded = " + annotation.getClass());
    System.out.println("loaded.getComponentType() = " + annotation.getClass().getComponentType());
  }

}