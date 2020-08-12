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
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * @author Karl Heinz Marbaise
 */
class AnnotationHelper {

  private AnnotationHelper() {
    // prevent instantiation.
  }



  static boolean hasProfiles(ExtensionContext context) {
    return profiles(context).count() > 0;
  }

  /**
   * Get the profiles from the annotation.
   *
   * @param context {@link ExtensionContext}
   * @return The stream with the profiles if exist.
   */
  static Stream<String> profiles(ExtensionContext context) {
    //MavenProfile[] annotationsByType = context.getClass().getPackage().getAnnotationsByType(MavenProfile.class);
    List<MavenProfile> repeatableAnnotationsOnMethod = AnnotationSupport.findRepeatableAnnotations(context.getTestMethod(), MavenProfile.class);
    if (repeatableAnnotationsOnMethod.stream().flatMap(s -> Stream.of(s.value())).count() > 0) {
      return repeatableAnnotationsOnMethod.stream().flatMap(s -> Stream.of(s.value()));
    }

    List<MavenProfile> repeatableAnnotationsOnClass = AnnotationSupport.findRepeatableAnnotations(context.getTestClass(), MavenProfile.class);
    return repeatableAnnotationsOnClass.stream().flatMap(s -> Stream.of(s.value()));
  }

  static boolean hasGoals(ExtensionContext context) {
    return goals(context).count() > 0;
  }

  /**
   * Get the goals from the annotation.
   *
   * @param context {@link ExtensionContext}
   * @return The stream with the goals if exist.
   */
  static Stream<String> goals(ExtensionContext context) {
    List<MavenGoal> repeatableAnnotationsOnMethod = AnnotationSupport.findRepeatableAnnotations(context.getTestMethod(), MavenGoal.class);
    if (repeatableAnnotationsOnMethod.stream().flatMap(s -> Stream.of(s.value())).count() > 0) {
      return repeatableAnnotationsOnMethod.stream().flatMap(s -> Stream.of(s.value()));
    }

    List<MavenGoal> repeatableAnnotationsOnClass = AnnotationSupport.findRepeatableAnnotations(context.getTestClass(), MavenGoal.class);
    return repeatableAnnotationsOnClass.stream().flatMap(s -> Stream.of(s.value()));
  }

  static boolean hasOptions(ExtensionContext context) {
    return options(context).count() > 0;
  }

  static Stream<String> options(ExtensionContext context) {
    List<MavenOption> repeatableAnnotationsOnMethod = AnnotationSupport.findRepeatableAnnotations(context.getTestMethod(), MavenOption.class);
    if (repeatableAnnotationsOnMethod.stream().flatMap(s -> s.parameter().isEmpty() ? Stream.of(s.value()) : Stream.of(s.value(), s.parameter())).count() > 0) {
      return repeatableAnnotationsOnMethod.stream().flatMap(s -> s.parameter().isEmpty() ? Stream.of(s.value()) : Stream.of(s.value(), s.parameter()));
    }

    List<MavenOption> repeatableAnnotationsOnClass = AnnotationSupport.findRepeatableAnnotations(context.getTestClass(), MavenOption.class);
    return repeatableAnnotationsOnClass.stream().flatMap(s -> s.parameter().isEmpty() ? Stream.of(s.value()) : Stream.of(s.value(), s.parameter()));
  }

  static boolean hasSystemProperties(ExtensionContext context) {
    return systemProperties(context).size() > 0;
  }

  static List<SystemProperty> systemProperties(ExtensionContext context) {
    List<SystemProperty> repeatableAnnotationsOnMethod = AnnotationSupport.findRepeatableAnnotations(context.getTestMethod(), SystemProperty.class);
    if (repeatableAnnotationsOnMethod.stream().flatMap(s -> s.value().isEmpty() ? Stream.of(s.value()) : Stream.of(s.value(), s.value())).count() > 0) {
      return repeatableAnnotationsOnMethod;
    }

    List<SystemProperty> repeatableAnnotationsOnClass = AnnotationSupport.findRepeatableAnnotations(context.getTestClass(), SystemProperty.class);
    return repeatableAnnotationsOnClass;
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

  static Optional<Class<?>> findMavenPredefinedRepositoryAnnotation(ExtensionContext context) {
    return findAnnotation(context, MavenPredefinedRepository.class);
  }

}
