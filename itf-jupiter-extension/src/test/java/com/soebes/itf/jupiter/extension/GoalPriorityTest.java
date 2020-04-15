package com.soebes.itf.jupiter.extension;

import static com.soebes.itf.jupiter.extension.GoalPriority.goals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.PreconditionViolationException;

/**
 * @author Karl Heinz Marbaise
 */
@DisplayName("Goal priority should")
class GoalPriorityTest {

  @Test
  @DisplayName("result in goals of MavenIT")
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
  @DisplayName("result in goals of MavenTest while goals of MavenIT are empty.")
  void should_return_goals_of_maven_test_while_it_goals_are_emtpy() {
    String[] goals = goals(new String[]{}, new String[]{"c"});
    assertThat(goals).containsExactly("c");
  }

  @Test
  @DisplayName("fail with PreconditionViolationException based on null for mavenITGoals.")
  void should_fail_with_null_for_first_parameter() {
    assertThatExceptionOfType(PreconditionViolationException.class).isThrownBy(() -> goals(null, null))
      .withMessage("mavenITGoals is not allowed to be null.");
  }

  @Test
  @DisplayName("fail with PreconditionViolationException based on null for mavenTestGoals.")
  void should_fail_with_null_for_second_parameter() {
    assertThatExceptionOfType(PreconditionViolationException.class).isThrownBy(() -> goals(new String[]{}, null))
      .withMessage("mavenTestGoals is not allowed to be null.");
  }

}