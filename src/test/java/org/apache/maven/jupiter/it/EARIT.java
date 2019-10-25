package org.apache.maven.jupiter.it;

import static org.apache.maven.jupiter.assertj.MavenExecutionResultAssert.assertThat;
import static org.apache.maven.jupiter.assertj.MavenProjectResultAssert.assertThat;

import org.apache.maven.jupiter.extension.MavenIT;
import org.apache.maven.jupiter.extension.MavenTest;
import org.apache.maven.jupiter.extension.maven.MavenExecutionResult;
import org.apache.maven.jupiter.extension.maven.MavenProjectResult;

/**
 * Examples from Maven EAR Plugin
 *
 * Invoker Integration Test:
 * <ul>
 *   <li>basic</li>
 * </ul>
 *
 * @author Karl Heinz Marbaise
 */
@MavenIT
class EARIT {

  @MavenTest
  void basic(MavenExecutionResult result, MavenProjectResult project) {
    assertThat(result).isSuccessful();
    assertThat(project).hasTarget()
        .withEarFile()
        .containsOnlyOnce("META-INF/application.xml", "META-INF/appserver-application.xml");
  }

  @MavenTest
  void packaging_includes(MavenExecutionResult result, MavenProjectResult project) {
    assertThat(result).isSuccessful();
    assertThat(project).hasTarget()
        .withEarFile()
        .doesNotContain("commons-io-1.4.jar")
        .containsOnlyOnce("commons-lang-commons-lang-2.5.jar", "META-INF/application.xml", "META-INF/MANIFEST.MF");
  }

}
