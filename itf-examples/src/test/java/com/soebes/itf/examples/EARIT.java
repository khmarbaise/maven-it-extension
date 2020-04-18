package com.soebes.itf.examples;

import com.soebes.itf.jupiter.extension.MavenIT;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenLog;
import com.soebes.itf.jupiter.maven.MavenProjectResult;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

/**
 * Examples taken from Maven EAR Plugin
 * <p>
 * Invoker Integration Test:
 * <ul>
 *   <li>test_project_root</li>
 *   <li>packaging_includes</li>
 *   <li>packaging_excludes</li>
 *   <li>transitive_excludes</li>
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
        .doesNotContain("commons-io-commons-io-1.4.jar")
        .containsOnlyOnce("commons-lang-commons-lang-2.5.jar", "META-INF/application.xml", "META-INF/MANIFEST.MF");
  }

  @MavenTest
  void packaging_excludes(MavenExecutionResult result, MavenProjectResult project) {
    assertThat(result).isSuccessful();
    assertThat(project).hasTarget()
        .withEarFile()
        .doesNotContain("commons-lang-commons-lang-2.5.jar")
        .containsOnlyOnce("META-INF/application.xml", "META-INF/MANIFEST.MF");
  }

  @MavenTest
  void resource_custom_directory(MavenExecutionResult result, MavenProjectResult project, MavenLog log) {
    assertThat(result).isSuccessful();
    assertThat(log).isSuccessful();
    assertThat(project).hasTarget()
        .withEarFile()
        .containsOnlyOnce("META-INF/application.xml", "APP-INF/classes/foo.properties");
  }

  @MavenTest
  void transitive_excludes(MavenExecutionResult result, MavenProjectResult project, MavenLog log) {
    assertThat(result).isSuccessful();
    assertThat(log).isSuccessful();
    assertThat(log).info().stream().filter(s -> s.startsWith("TESTING"));
    assertThat(log).debug().stream().filter(s -> s.endsWith("FAILURE"));
    //assertThat(log).buildFailure(); DOES NOT WORK YET

    assertThat(project).hasTarget()
        .withEarFile()
        .containsOnlyOnce("org.apache.maven-maven-core-3.0.jar", "META-INF/application.xml");
  }

  @MavenTest
  void skinny_wars_javaee5(MavenExecutionResult result, MavenProjectResult project, MavenLog log) {
    assertThat(result).isSuccessful();
    assertThat(project)
        .hasModule("war-module")
        .hasModule("ear-module");

    /*
    target (issue-4-maven-log-file-assertions *)$ unzip -t ear-module-1.0.ear
Archive:  ear-module-1.0.ear
    testing: META-INF/MANIFEST.MF     OK
    testing: META-INF/                OK
    testing: lib/                     OK
    testing: META-INF/maven/          OK
    testing: META-INF/maven/org.apache.maven.its.ear.skinnywars/   OK
    testing: META-INF/maven/org.apache.maven.its.ear.skinnywars/ear-module/   OK
    testing: org.apache.maven.its.ear.skinnywars-war-module-1.0.war   OK
    testing: META-INF/application.xml   OK
    testing: lib/commons-lang-commons-lang-2.5.jar   OK
    testing: META-INF/maven/org.apache.maven.its.ear.skinnywars/ear-module/pom.xml   OK
    testing: META-INF/maven/org.apache.maven.its.ear.skinnywars/ear-module/pom.properties   OK
No errors detected in compressed data of ear-module-1.0.ear.
     */
    assertThat(project)
        .withModule("ear-module")
        .withEarFile()
        .containsOnlyOnce("org.apache.maven.its.ear.skinnywars-war-module-1.0.war",
            "lib/commons-lang-commons-lang-2.5.jar",
            "META-INF/application.xml");
  }
}
