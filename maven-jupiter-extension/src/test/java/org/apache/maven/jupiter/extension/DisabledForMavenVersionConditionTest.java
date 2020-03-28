package org.apache.maven.jupiter.extension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.parallel.ResourceAccessMode.READ_WRITE;
import static org.junit.jupiter.api.parallel.Resources.SYSTEM_PROPERTIES;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.Properties;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.parallel.ResourceLock;

class DisabledForMavenVersionConditionTest {

  private Properties backup;

  @BeforeEach
  void beforeEach() {
    backup = new Properties();
    backup.putAll(System.getProperties());
  }

  @AfterEach
  void afterEach() {
    System.setProperties(backup);
  }

  @Test
  @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ_WRITE)
  void customPropertyIsNotSetByDefault() {
    System.setProperty("maven.version", "3.5.1");
    DisabledForMavenVersionCondition disabledForMavenVersionCondition = new DisabledForMavenVersionCondition();

    ExtensionContext extensionContext = mock(ExtensionContext.class);

    when(extensionContext.getElement()).thenReturn(Optional.of(DisabledForMavenVersion.class));
    ConditionEvaluationResult conditionEvaluationResult = disabledForMavenVersionCondition.evaluateExecutionCondition(
        extensionContext);
    assertThat(conditionEvaluationResult.isDisabled()).isTrue();

  }
  @Test
  @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ_WRITE)
  void check_for_xxx() {
    System.setProperty("maven.version", "3.5.1");
    DisabledForMavenVersionCondition disabledForMavenVersionCondition = new DisabledForMavenVersionCondition();

    ExtensionContext extensionContext = mock(ExtensionContext.class);

    AnnotatedElement annotatedElement = mock(AnnotatedElement.class);

    when(annotatedElement.isAnnotationPresent(eq(DisabledForMavenVersion.class))).thenReturn(true);

    when(extensionContext.getElement()).thenReturn(Optional.of(annotatedElement));
    ConditionEvaluationResult conditionEvaluationResult = disabledForMavenVersionCondition.evaluateExecutionCondition(
        extensionContext);
    assertThat(conditionEvaluationResult.isDisabled()).isTrue();

  }

  @Test
  @ResourceLock(value = SYSTEM_PROPERTIES, mode = READ_WRITE)
  void testingXXX() {
    DisabledForMavenVersionCondition disabledForMavenVersionCondition = new DisabledForMavenVersionCondition();

    ExtensionContext extensionContext = mock(ExtensionContext.class);

    when(extensionContext.getElement()).thenReturn(Optional.of(DisabledForMavenVersion.class));
    ConditionEvaluationResult conditionEvaluationResult = disabledForMavenVersionCondition.evaluateExecutionCondition(
        extensionContext);
    assertThat(conditionEvaluationResult.isDisabled()).isTrue();

  }
}