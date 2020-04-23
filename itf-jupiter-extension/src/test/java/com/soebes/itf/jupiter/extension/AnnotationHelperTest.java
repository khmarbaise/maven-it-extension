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

import net.bytebuddy.description.annotation.AnnotationDescription;
import net.bytebuddy.description.annotation.AnnotationDescription.Builder;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * @author Karl Heinz Marbaise
 */
class AnnotationHelperTest {

  private Method createMavenTestAnnotationForDefaults() {
    AnnotationDescription mavenTestAnnotationDescription = Builder.ofType(MavenTest.class).build();

    return createAnnotationPrepare(mavenTestAnnotationDescription);
  }

  private Method createMavenTestAnnotation(String... profiles) {
    AnnotationDescription mavenTestAnnotationDescription = Builder.ofType(MavenTest.class)
        .defineArray("activeProfiles", profiles)
        .build();

    return createAnnotationPrepare(mavenTestAnnotationDescription);
  }

  private Method createMavenTestAnnotationGoals(String... profiles) {
    AnnotationDescription mavenTestAnnotationDescription = Builder.ofType(MavenTest.class)
        .defineArray("goals", profiles)
        .build();

    return createAnnotationPrepare(mavenTestAnnotationDescription);
  }

  private Method createAnnotationPrepare(AnnotationDescription mavenTestAnnotationDescription) {
    MavenTest mavenTestAnnotation = mavenTestAnnotationDescription.prepare(MavenTest.class).load();

    Method method = mock(Method.class);
    when(method.isAnnotationPresent(ArgumentMatchers.any())).thenReturn(true);
    when(method.getAnnotation(ArgumentMatchers.any())).thenReturn(mavenTestAnnotation);
    return method;
  }

  @Nested
  @DisplayName("isDebug should")
  class IsDebug {

    @Test
    @DisplayName("fail with IllegalArgumentException for null parameter.")
    void should_fail_with_exeception_for_parameter_null() {
      assertThatIllegalArgumentException().isThrownBy(() -> AnnotationHelper.isDebug(null))
          .withMessage("method is not allowed to be null.");
    }

    @Test
    @DisplayName("fail when MavenTest annotation is not present.")
    void should_fail_with_illegal_state_exception() {
      Method method = mock(Method.class);
      when(method.isAnnotationPresent(ArgumentMatchers.any())).thenReturn(false);
      when(method.getName()).thenReturn("The unknown method.");

      assertThatIllegalStateException().isThrownBy(() -> AnnotationHelper.isDebug(method))
          .withMessage("MavenTest Annotation is not given on method: 'The unknown method.'");
    }

    @Test
    @DisplayName("result in false.")
    void should_result_false() {
      Method method = createMavenTestAnnotation(Boolean.FALSE);

      assertThat(AnnotationHelper.isDebug(method)).isFalse();
    }

    @Test
    @DisplayName("result in true.")
    void should_result_true() {
      Method method = createMavenTestAnnotation(Boolean.TRUE);

      assertThat(AnnotationHelper.isDebug(method)).isTrue();
    }

    @Test
    @DisplayName("return the default value which is false.")
    void should_return_default_value() {
      Method mavenTestAnnotationForDefaults = createMavenTestAnnotationForDefaults();
      assertThat(AnnotationHelper.isDebug(mavenTestAnnotationForDefaults)).isFalse();
    }

    private Method createMavenTestAnnotation(Boolean debugState) {
      AnnotationDescription mavenTestAnnotationDescription = Builder.ofType(MavenTest.class)
          .define("debug", debugState)
          .build();

      return createAnnotationPrepare(mavenTestAnnotationDescription);
    }
  }

  @Nested
  @DisplayName("hasActiveProfiles should")
  class HasActiveProfiles {

    @Test
    @DisplayName("fail with IllegalArgumentException for null parameter.")
    void should_fail_with_exeception_for_parameter_null() {
      assertThatIllegalArgumentException().isThrownBy(() -> AnnotationHelper.hasActiveProfiles(null))
          .withMessage("method is not allowed to be null.");
    }

    @Test
    @DisplayName("return true for active profiles.")
    void should_return_true_for_active_profiles() {
      Method mavenTestAnnotation = createMavenTestAnnotation("profile1");
      assertThat(AnnotationHelper.hasActiveProfiles(mavenTestAnnotation)).isTrue();
    }

    @Test
    @DisplayName("return false for no active profiles given.")
    void should_return_false_for_no_active_profile() {
      Method mavenTestAnnotation = createMavenTestAnnotation();
      assertThat(AnnotationHelper.hasActiveProfiles(mavenTestAnnotation)).isFalse();
    }

  }

  @Nested
  @DisplayName("getActiveProfiles should")
  class GetActiveProfiles {

    @Test
    @DisplayName("fail with IllegalArgumentException for null parameter.")
    void should_fail_with_exeception_for_parameter_null() {
      assertThatIllegalArgumentException().isThrownBy(() -> AnnotationHelper.getActiveProfiles(null))
          .withMessage("method is not allowed to be null.");
    }

    @Test
    @DisplayName("fail when MavenTest annotation is not present.")
    void should_fail_with_illegal_state_exception() {
      Method method = mock(Method.class);
      when(method.isAnnotationPresent(ArgumentMatchers.any())).thenReturn(false);
      when(method.getName()).thenReturn("The unknown method.");

      assertThatIllegalStateException().isThrownBy(() -> AnnotationHelper.getActiveProfiles(method))
          .withMessage("MavenTest Annotation is not given on method: 'The unknown method.'");
    }

    @Test
    @DisplayName("return the default value which is empty {}.")
    void should_return_default_value() {
      Method mavenTestAnnotationForDefaults = createMavenTestAnnotationForDefaults();
      assertThat(AnnotationHelper.getActiveProfiles(mavenTestAnnotationForDefaults)).isEmpty();
    }

    @Test
    @DisplayName("return the given two profiles via the annotation.")
    void should_return_the_given_profiles_on_the_annotation() {
      Method mavenTestAnnotation = createMavenTestAnnotation("profile1", "profile2");
      assertThat(AnnotationHelper.getActiveProfiles(mavenTestAnnotation)).containsExactly("profile1", "profile2");
    }

  }

  @Nested
  @DisplayName("getGoals should")
  class GetGoals {

    @Test
    @DisplayName("fail with IllegalArbumentException for null parameter.")
    void should_fail_with_exeception_for_parameter_null() {
      assertThatIllegalArgumentException().isThrownBy(() -> AnnotationHelper.getGoals(null))
          .withMessage("method is not allowed to be null.");
    }

    @Test
    @DisplayName("fail when MavenTest annotation is not present.")
    void should_fail_with_illegal_state_exception() {
      Method method = mock(Method.class);
      when(method.isAnnotationPresent(ArgumentMatchers.any())).thenReturn(false);
      when(method.getName()).thenReturn("The unknown method.");

      assertThatIllegalStateException().isThrownBy(() -> AnnotationHelper.getGoals(method))
          .withMessage("MavenTest Annotation is not given on method: 'The unknown method.'");
    }

    @Test
    @DisplayName("return the default value which is {} (empty)")
    void should_return_default_value() {
      Method mavenTestAnnotationForDefaults = createMavenTestAnnotationForDefaults();
      assertThat(AnnotationHelper.getGoals(mavenTestAnnotationForDefaults)).isEmpty();
    }

  }

  @Nested
  @DisplayName("hasGoals should")
  class HasGoals {

    @Test
    @DisplayName("fail with IllegalArgumentException for null parameter.")
    void should_fail_with_exeception_for_parameter_null() {
      assertThatIllegalArgumentException().isThrownBy(() -> AnnotationHelper.hasActiveProfiles(null))
          .withMessage("method is not allowed to be null.");
    }

    @Test
    @DisplayName("fail when MavenTest annotation is not present.")
    void should_fail_with_illegal_state_exception() {
      Method method = mock(Method.class);
      when(method.isAnnotationPresent(ArgumentMatchers.any())).thenReturn(false);
      when(method.getName()).thenReturn("The unknown method.");

      assertThatIllegalStateException().isThrownBy(() -> AnnotationHelper.hasGoals(method))
          .withMessage("MavenTest Annotation is not given on method: 'The unknown method.'");
    }

    @Test
    @DisplayName("return true for defined default goals.")
    void should_return_true_for_active_profiles() {
      Method mavenTestAnnotation = createMavenTestAnnotationForDefaults();
      assertThat(AnnotationHelper.hasGoals(mavenTestAnnotation)).isFalse();
    }

    @Test
    @DisplayName("return true for defined default goals.")
    void should_return_false_if_no_goals_are_defines() {
      Method mavenTestAnnotation = createMavenTestAnnotationGoals();
      assertThat(AnnotationHelper.hasGoals(mavenTestAnnotation)).isFalse();
    }

  }
}