package org.apache.maven.jupiter.extension;

import static org.junit.jupiter.api.extension.ConditionEvaluationResult.enabled;
import static org.junit.platform.commons.util.AnnotationUtils.findAnnotation;

import java.util.Optional;
import org.apache.maven.jupiter.extension.maven.MavenVersion;
import org.junit.jupiter.api.extension.ConditionEvaluationResult;
import org.junit.jupiter.api.extension.ExecutionCondition;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.platform.commons.util.Preconditions;

/**
 * @author Karl Heinz Marbaise
 */
public class DisabledForMavenCondition implements ExecutionCondition {

  //FIXME: Need to reconsider how to evaluate the maven version which is running?
//  static final ConditionEvaluationResult ENABLED_ON_CURRENT_JRE = //
//      enabled("Enabled on JRE version: " + System.getProperty("java.version"));
//
//  static final ConditionEvaluationResult DISABLED_ON_CURRENT_JRE = //
//      disabled("Disabled on JRE version: " + System.getProperty("java.version"));

  private static final ConditionEvaluationResult ENABLED_BY_DEFAULT = enabled("@DisabledForMaven is not present");

  @Override
  public ConditionEvaluationResult evaluateExecutionCondition(ExtensionContext context) {
    Optional<DisabledForMaven> optional = findAnnotation(context.getElement(), DisabledForMaven.class);
    if (optional.isPresent()) {
      MavenVersion[] versions = optional.get().versions();
      Preconditions.condition(versions.length > 0, "You must declare at least one version in @DisabledForMaven");
//      return (Arrays.stream(versions).anyMatch(JRE::isCurrentVersion)) ? ENABLED_ON_CURRENT_JRE
//          : DISABLED_ON_CURRENT_JRE;
    }
    return ENABLED_BY_DEFAULT;
  }

}
