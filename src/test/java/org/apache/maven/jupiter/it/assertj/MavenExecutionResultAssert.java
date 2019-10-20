package org.apache.maven.jupiter.it.assertj;

import org.apache.maven.jupiter.it.extension.maven.MavenExecutionResult;
import org.assertj.core.api.AbstractAssert;

public class MavenExecutionResultAssert extends AbstractAssert<MavenExecutionResultAssert, MavenExecutionResult> {

  public MavenExecutionResultAssert(MavenExecutionResult actual) {
    super(actual, MavenExecutionResultAssert.class);
  }

  /**
   * An entry point for MavenExecutionResultAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(result).isSuccessful();</code>
   *
   * @param actual the MavenExecutionResult we want to make assertions on.
   * @return a new </code>{@link MavenExecutionResultAssert}</code>
   */
  public static MavenExecutionResultAssert assertThat(MavenExecutionResult actual) {
    return new MavenExecutionResultAssert(actual);
  }

  /**
   * @return {@link MavenExecutionResultAssert} for method chaining.
   * @throws AssertionError if the actual value is {@code null}.
   * @throws AssertionError if the actual value is not {@code true}.
   */
  public MavenExecutionResultAssert isSuccessful() {
    isNotNull();
    if (!this.actual.isSuccesful()) {
      failWithMessage("The build was not successful but was <%s> with returnCode:<%s>", actual.getResult(),
          actual.getReturnCode());
    }
    return myself;
  }
  /**
   * @return {@link MavenExecutionResultAssert} for method chaining.
   * @throws AssertionError if the actual value is {@code null}.
   * @throws AssertionError if the actual value is not {@code true}.
   */
  public MavenExecutionResultAssert isFailure() {
    isNotNull();
    if (!this.actual.isFailure()) {
      failWithMessage("The build was not successful but was <%s> with returnCode:<%s>", actual.getResult(),
          actual.getReturnCode());
    }
    return myself;
  }
}
