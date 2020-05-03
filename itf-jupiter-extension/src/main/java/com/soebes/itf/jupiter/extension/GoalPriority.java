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

import static com.soebes.itf.jupiter.extension.Preconditions.requireNotNull;

/**
 * @author Karl Heinz Marbaise
 */
final class GoalPriority {

  /**
   * The goals which are defined by {@link MavenTest} can overwrite the goals defined by {@link MavenJupiterExtension}. In {@link
   * MavenJupiterExtension} there will be defined a set of default goals.
   *
   * @param mavenITGoals The goals which are defined in {@link MavenJupiterExtension}
   * @param mavenTestGoals The goals which are defined in {@link MavenTest}
   * @return The goals which are defined by {@link MavenTest} if have been overwritten.
   */
  static String[] goals(String[] mavenITGoals, String[] mavenTestGoals) {
    requireNotNull(mavenITGoals, "mavenITGoals is not allowed to be null.");
    requireNotNull(mavenTestGoals, "mavenTestGoals is not allowed to be null.");
    if (mavenTestGoals.length == 0) {
      return mavenITGoals;
    } else {
      return mavenTestGoals;
    }
  }
}
