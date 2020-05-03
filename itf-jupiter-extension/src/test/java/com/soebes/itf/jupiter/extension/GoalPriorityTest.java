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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.soebes.itf.jupiter.extension.GoalPriority.goals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * @author Karl Heinz Marbaise
 */
@DisplayName("Goal priority should")
class GoalPriorityTest {

  @Test
  @DisplayName("result in goals of MavenJupiterExtension")
  void should_return_goals_of_maven_it() {
    String[] goals = goals(new String[]{"a", "b"}, new String[]{});
    assertThat(goals).containsExactly("a", "b");
  }

  @Test
  @DisplayName("result in goals of MavenTest")
  void should_return_goals_of_maven_test() {
    String[] goals = goals(new String[]{"a", "b"}, new String[]{"c"});
    assertThat(goals).containsExactly("c");
  }

  @Test
  @DisplayName("result in goals of MavenTest while goals of MavenJupiterExtension are empty.")
  void should_return_goals_of_maven_test_while_it_goals_are_emtpy() {
    String[] goals = goals(new String[]{}, new String[]{"c"});
    assertThat(goals).containsExactly("c");
  }

  @Test
  @DisplayName("fail with IllegalArgumentException based on null for mavenITGoals.")
  void should_fail_with_null_for_first_parameter() {
    assertThatIllegalArgumentException().isThrownBy(() -> goals(null, null))
        .withMessage("mavenITGoals is not allowed to be null.");
  }

  @Test
  @DisplayName("fail with IllegalArgumentException based on null for mavenTestGoals.")
  void should_fail_with_null_for_second_parameter() {
    assertThatIllegalArgumentException().isThrownBy(() -> goals(new String[]{}, null))
        .withMessage("mavenTestGoals is not allowed to be null.");
  }

}