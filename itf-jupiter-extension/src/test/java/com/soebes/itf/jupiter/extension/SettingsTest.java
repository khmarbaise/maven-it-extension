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

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.dynamic.DynamicType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mockStatic;


@ExtendWith(MockitoExtension.class)
class SettingsTest {

  @Mock
  private ExtensionContext context;
  @Mock
  private DirectoryResolverResult directoryResolverResult;
  @Mock
  private AnnotationHelper annotationHelper;
  @InjectMocks
  private Settings underTest;

  @Test
  void noSettingsAnnotationAvailable() {
    try (MockedStatic<AnnotationHelper> annotationHelperStatic = mockStatic(AnnotationHelper.class)) {
      annotationHelperStatic.when(() -> AnnotationHelper.findMavenSettingsSourcesAnnotation(any())).thenReturn(Optional.empty());
      List<String> settings = underTest.settingsSetup();
      assertThat(settings).isEmpty();
    }
  }

  @Test
  void name() throws IOException {
    try (DynamicType.Unloaded<Object> make = getMake()) {

      Class<?> loaded = make
          .load(getClass().getClassLoader())
          .getLoaded();

      System.out.println("loaded = " + loaded.getName());
      System.out.println("loaded = " + make.getClass());
      System.out.println("loaded.getComponentType() = " + loaded.getComponentType());
    }

  }

  private static DynamicType.Unloaded<Object> getMake() {
    return new ByteBuddy()
        .subclass(Object.class)
        .annotateType(AnnotationDescription.Builder.ofType(MavenSettingsSources.class).build())
        .make();
  }


}