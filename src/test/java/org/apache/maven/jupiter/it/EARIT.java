package org.apache.maven.jupiter.it;

import static org.apache.maven.jupiter.assertj.MavenExecutionResultAssert.assertThat;

import org.apache.maven.jupiter.extension.MavenIT;
import org.apache.maven.jupiter.extension.MavenTest;
import org.apache.maven.jupiter.extension.maven.MavenExecutionResult;

/**
 * The following test cases are related together cause they are sharing a common cache. Furthermore the test cases or
 * running in a given order (defined by Order annotation).
 *
 * @author Karl Heinz Marbaise
 */
@MavenIT
class EARIT {

  @MavenTest
  void basic(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

}
