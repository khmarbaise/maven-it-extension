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

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.support.AnnotationSupport;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Karl Heinz Marbaise
 */
class AnnotationHelper {

  private AnnotationHelper() {
    // prevent instantiation.
  }

  /**
   * @param context {@link ExtensionContext}
   * @return {@code true} if we have any {@link MavenProfile @MavenProfile} defined or {@code false} otherwise.
   */
  static boolean hasProfiles(ExtensionContext context) {
    return !profiles(context).isEmpty();
  }

  /**
   * Get the profiles from the annotation.
   *
   * @param context {@link ExtensionContext}
   * @return The stream with the profiles.
   */
  static List<String> profiles(ExtensionContext context) {
    List<MavenProfile> mavenProfilesOnTestMethod = AnnotationSupport.findRepeatableAnnotations(context.getTestMethod(), MavenProfile.class);
    List<String> profiles = mavenProfilesOnTestMethod.stream().flatMap(profile -> Stream.of(profile.value())).collect(Collectors.toList());
    if (!profiles.isEmpty()) {
      return profiles;
    }

    List<MavenProfile> repeatableAnnotationsOnClass = AnnotationSupport.findRepeatableAnnotations(context.getTestClass(), MavenProfile.class);
    return repeatableAnnotationsOnClass.stream().flatMap(profile -> Stream.of(profile.value())).collect(Collectors.toList());
  }
  /**
   * @param context {@link ExtensionContext}
   * @return {@code true} if we have any {@link MavenGoal @MavenGoal} defined or {@code false} otherwise.
   */
  static boolean hasGoals(ExtensionContext context) {
    return !goals(context).isEmpty();
  }

  /**
   * Get the goals from the annotation either on test method level
   * or on test class level.
   *
   * @param context {@link ExtensionContext}
   * @return The stream with the goals.
   */
  static List<String> goals(ExtensionContext context) {
    List<MavenGoal> goalAnnotations = AnnotationSupport.findRepeatableAnnotations(context.getTestMethod(), MavenGoal.class);
    List<String> stringStream = goalAnnotations.stream().flatMap(goal -> Stream.of(goal.value())).collect(Collectors.toList());
    if (!stringStream.isEmpty()) {
      return stringStream;
    }

    List<MavenGoal> repeatableAnnotationsOnClass = AnnotationSupport.findRepeatableAnnotations(context.getTestClass(), MavenGoal.class);
    return repeatableAnnotationsOnClass.stream().flatMap(goal -> Stream.of(goal.value())).collect(Collectors.toList());
  }

  /**
   * @param context {@link ExtensionContext}
   * @return {@code true} if we have any {@link MavenOption @MavenOption} defined or {@code false} otherwise.
   */
  static boolean hasOptions(ExtensionContext context) {
    return !options(context).isEmpty();
  }

  /**
   * Get the options from the annotation either on test method level
   * or on test class level.
   *
   * @param context {@link ExtensionContext}
   * @return The stream with the options.
   */
  static List<String> options(ExtensionContext context) {
    List<MavenOption> mavenOptionsOnTestMethod = AnnotationSupport.findRepeatableAnnotations(context.getTestMethod(), MavenOption.class);
    List<String> options = mavenOptionsOnTestMethod.stream()
        .flatMap(option -> option.parameter().isEmpty() ? Stream.of(option.value()) : Stream.of(option.value(), option.parameter()))
        .collect(Collectors.toList());
    if (!options.isEmpty()) {
      return options;
    }

    List<MavenOption> mavenOptionsOnTestClass = AnnotationSupport.findRepeatableAnnotations(context.getTestClass(), MavenOption.class);
    return mavenOptionsOnTestClass.stream().flatMap(option -> option.parameter().isEmpty() ? Stream.of(option.value()) : Stream.of(option.value(), option.parameter()))
        .collect(Collectors.toList());
  }

  /**
   * @param context {@link ExtensionContext}
   * @return {@code true} if we have any {@link SystemProperty @SystemProperty} defined or {@code false} otherwise.
   */
  static boolean hasSystemProperties(ExtensionContext context) {
    return !systemProperties(context).isEmpty();
  }

  private static Optional<Class<?>> findAnnotation(ExtensionContext context,
                                                   Class<? extends Annotation> annotationClass) {
    Optional<ExtensionContext> current = Optional.of(context);
    while (current.isPresent()) {
      Optional<Class<?>> testClassNumber1 = current.get().getTestClass();
      if (testClassNumber1.isPresent()) {
        Class<?> testClass = testClassNumber1.get();
        if (testClass.isAnnotationPresent(annotationClass)) {
          return Optional.of(testClass);
        }
      }
      current = current.get().getParent();
    }
    return Optional.empty();
  }

  static Optional<Class<?>> findMavenRepositoryAnnotation(ExtensionContext context) {
    return findAnnotation(context, MavenRepository.class);
  }

  static Optional<Class<?>> findMavenProjectAnnotation(ExtensionContext context) {
    return findAnnotation(context, MavenProject.class);
  }

  static Optional<MavenProjectSources> findMavenProjectSourcesAnnotation(ExtensionContext context) {
    Method method = context.getTestMethod().orElseThrow(IllegalStateException::new);

    boolean methodAnnotationPresent = method.isAnnotationPresent(MavenProjectSources.class);
    if (methodAnnotationPresent) {
      return Optional.of(method.getAnnotation(MavenProjectSources.class));
    }

    Optional<Class<?>> firstFinding = findAnnotation(context, MavenProjectSources.class);
    if (firstFinding.isPresent()) {
      MavenProjectSources annotation = firstFinding.get().getAnnotation(MavenProjectSources.class);
      return Optional.of(annotation);
    }

    Optional<MavenProjectSources> mavenProjectLocationAnnotation = AnnotationSupport.findAnnotation(context.getTestClass(), MavenProjectSources.class);
    if (mavenProjectLocationAnnotation.isPresent()) {
      MavenProjectSources annotation = mavenProjectLocationAnnotation.get();
      return Optional.of(annotation);
    }

    return Optional.empty();
  }

  static Optional<MavenSettingsSources> findMavenSettingsSourcesAnnotation(ExtensionContext context) {
    Method method = context.getTestMethod().orElseThrow(IllegalStateException::new);

    boolean methodAnnotationPresent = method.isAnnotationPresent(MavenSettingsSources.class);
    if (methodAnnotationPresent) {
      return Optional.of(method.getAnnotation(MavenSettingsSources.class));
    }

    Optional<Class<?>> firstFinding = findAnnotation(context, MavenSettingsSources.class);
    if (firstFinding.isPresent()) {
      MavenSettingsSources annotation = firstFinding.get().getAnnotation(MavenSettingsSources.class);
      return Optional.of(annotation);
    }

    Optional<MavenSettingsSources> mavenProjectLocationAnnotation = AnnotationSupport.findAnnotation(context.getTestClass(), MavenSettingsSources.class);
    if (mavenProjectLocationAnnotation.isPresent()) {
      MavenSettingsSources annotation = mavenProjectLocationAnnotation.get();
      return Optional.of(annotation);
    }

    return Optional.empty();
  }

  static List<SystemProperty> systemProperties(ExtensionContext context) {
    Method method = context.getTestMethod().orElseThrow(IllegalStateException::new);

    List<SystemProperty> properties = new ArrayList<>(AnnotationSupport.findRepeatableAnnotations(method, SystemProperty.class));

    List<Object> allInstances = context.getTestInstances().orElseThrow(IllegalStateException::new).getAllInstances();
    for (int i = allInstances.size()-1; i >=0; i--) {
      properties.addAll(AnnotationSupport.findRepeatableAnnotations(allInstances.get(i).getClass(), SystemProperty.class));
    }

    return properties;
  }

  static Optional<Class<?>> findMavenPredefinedRepositoryAnnotation(ExtensionContext context) {
    return findAnnotation(context, MavenPredefinedRepository.class);
  }

  static Optional<MavenProjectLocation> findMavenProjectLocationAnnotation(ExtensionContext context) {
    Method method = context.getTestMethod().orElseThrow(IllegalStateException::new);

    boolean methodAnnotationPresent = method.isAnnotationPresent(MavenProjectLocation.class);
    if (methodAnnotationPresent) {
      return Optional.of(method.getAnnotation(MavenProjectLocation.class));
    }

    Optional<Class<?>> mavenProjectLocationAnnotation = findAnnotation(context, MavenProjectLocation.class);
    if (mavenProjectLocationAnnotation.isPresent()) {
      MavenProjectLocation annotation = mavenProjectLocationAnnotation.get().getAnnotation(MavenProjectLocation.class);
      return Optional.of(annotation);
    }

    return Optional.empty();
  }

}
