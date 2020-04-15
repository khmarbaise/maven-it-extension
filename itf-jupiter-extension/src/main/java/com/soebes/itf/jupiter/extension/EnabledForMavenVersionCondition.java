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

import static org.junit.jupiter.api.extension.ConditionEvaluationResult.disabled;
import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

import java.util.Optional;
import java.util.stream.Stream;

import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.Preconditions;

import com.soebes.itf.jupiter.maven.MavenVersion;

/**
 * {@link ExecutionCondition} for {@link EnabledForMavenVersion @EnabledForMaven}.
 *
 * @author Karl Heinz Marbaise
 * @see EnabledForMavenVersion
 * @since 0.1
 */
class EnabledForMavenVersionCondition implements ExecutionCondition {

  static final ConditionEvaluationResult ENABLED_ON_CURRENT_MAVEN_VERSION = enabled(
      "Enabled on Maven version: " + System.getProperty("maven.version"));

  static final ConditionEvaluationResult DISABLED_ON_CURRENT_MAVEN_VERSION = disabled(
      "Disabled on Maven version: " + System.getProperty("maven.version"));

  private static final ConditionEvaluationResult ENABLED_BY_DEFAULT = enabled("@EnabledForMavenVersion is not present");

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    Optional<EnabledForMavenVersion> optional = findAnnotation(context.getElement(), EnabledForMavenVersion.class);
    if (optional.isPresent()) {
      MavenVersion[] versions = optional.get().value();
      Preconditions.condition(versions.length > 0, "You must declare at least one version in @EnabledForMavenVersion");
      return Stream.of(versions).anyMatch(MavenVersion::isCurrentVersion) ? ENABLED_ON_CURRENT_MAVEN_VERSION
          : DISABLED_ON_CURRENT_MAVEN_VERSION;
    }
    return ENABLED_BY_DEFAULT;
  }

}
