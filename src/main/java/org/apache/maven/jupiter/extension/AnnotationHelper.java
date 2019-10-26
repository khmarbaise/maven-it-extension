package org.apache.maven.jupiter.extension;

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

import java.lang.reflect.Method;
import org.junit.platform.commons.util.Preconditions;

/**
 * @author Karl Heinz Marbaise
 */
class AnnotationHelper {

  static boolean isDebug(Method method) {
    checkParameterAndRequirements(method);
    return method.getAnnotation(MavenTest.class).debug();
  }

  static String[] getActiveProfiles(Method method) {
    checkParameterAndRequirements(method);
    return method.getAnnotation(MavenTest.class).activeProfiles();
  }

  static String[] getGoals(Method method) {
    checkParameterAndRequirements(method);
    return method.getAnnotation(MavenTest.class).goals();
  }

  static boolean hasActiveProfiles(Method method) {
    return getActiveProfiles(method).length > 0;
  }

  static boolean hasGoals(Method method) {
    return getGoals(method).length > 0;
  }

  private static void checkParameterAndRequirements(Method method) {
    Preconditions.notNull(method, "method is not allowed to be null.");
    if (!method.isAnnotationPresent(MavenTest.class)) {
      throw new IllegalStateException("MavenTest Annotation is not given on method: '" + method.getName() + "'");
    }
  }

}
