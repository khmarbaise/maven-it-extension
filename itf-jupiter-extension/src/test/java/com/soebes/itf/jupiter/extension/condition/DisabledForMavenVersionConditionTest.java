package com.soebes.itf.jupiter.extension.condition;

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

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.lang.reflect.AnnotatedElement;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.parallel.ResourceAccessMode.READ_WRITE;
import static org.junit.jupiter.api.parallel.Resources.SYSTEM_PROPERTIES;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * @author Karl Heinz Marbaise
 */
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
  @Disabled("Not working yet.")
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
  @Disabled("Not working yet.")
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
  @Disabled("Not working yet.")
  void testingXXX() {
    DisabledForMavenVersionCondition disabledForMavenVersionCondition = new DisabledForMavenVersionCondition();

    ExtensionContext extensionContext = mock(ExtensionContext.class);

    when(extensionContext.getElement()).thenReturn(Optional.of(DisabledForMavenVersion.class));
    ConditionEvaluationResult conditionEvaluationResult = disabledForMavenVersionCondition.evaluateExecutionCondition(
        extensionContext);
    assertThat(conditionEvaluationResult.isDisabled()).isTrue();

  }
}