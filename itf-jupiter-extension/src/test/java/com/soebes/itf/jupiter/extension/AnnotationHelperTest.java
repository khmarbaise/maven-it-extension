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

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.LauncherSession;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;
import org.junit.platform.testkit.engine.EngineTestKit;
import org.junit.platform.testkit.engine.Events;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static com.soebes.itf.jupiter.extension.AnnotationHelper.profiles;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnnotationHelperTest {

  @Mock
  private ExtensionContext extensionContext;

  @Test
  void no_profiles_given() {
    assertThat(profiles(extensionContext)).isEmpty();
  }

  @Test
  void single_profile() throws NoSuchMethodException {
    when(extensionContext.getTestMethod()).thenReturn(Optional.of(ProfileAnnotationOnMethod.class.getDeclaredMethod("name")));
    List<String> profiles = profiles(extensionContext);
    assertThat(profiles).containsExactly("profile-on-method");
  }

  @Test
  void profile_annotation_on_class_level() throws NoSuchMethodException {
    when(extensionContext.getTestClass()).thenReturn(Optional.of(ProfileAnnotationOnClass.class));
    when(extensionContext.getTestMethod()).thenReturn(Optional.of(ProfileAnnotationOnClass.class.getDeclaredMethod("name")));
    List<String> profiles = profiles(extensionContext);
    assertThat(profiles).containsExactly("profile-on-class");
  }

  @Test
  void profile_annotation_on_nested_class_level() throws NoSuchMethodException {
    when(extensionContext.getTestClass()).thenReturn(Optional.of(ProfileAnnotationOnNestedClass.NestedClass.class));
    when(extensionContext.getTestMethod()).thenReturn(Optional.of(ProfileAnnotationOnNestedClass.NestedClass.class.getDeclaredMethod("name")));
    List<String> profiles = profiles(extensionContext);
    assertThat(profiles).containsExactly("profile-on-nest-class");
  }

  @Test
  void testKitFirst() {
    Events events = EngineTestKit.engine("junit-jupiter")
        .selectors(selectClass(ProfileAnnotationOnNestedClass.class))
        .execute()
        .allEvents();

    events.stream().forEach(s -> System.out.println("s = " + s));
  }

  @Test
  void launcher() {
    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
        .selectors(selectClass(ProfileAnnotationOnNestedClass.class)
        )
        .build();

    try (LauncherSession session = LauncherFactory.openSession()) {
      TestPlan testPlan = session.getLauncher().discover(request);

      TestIdentifier root = testPlan.getRoots().stream().findFirst().orElseThrow(IllegalStateException::new);

      Set<TestIdentifier> children = testPlan.getChildren(root);
      children.forEach(s -> System.out.println("s = " + s));

      System.out.println("testPlan = " + testPlan.getRoots());
    }


  }
}