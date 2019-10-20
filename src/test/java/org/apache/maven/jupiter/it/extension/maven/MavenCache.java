package org.apache.maven.jupiter.it.extension.maven;

public enum MavenCache {
  /**
   * Global means having a single local cache
   * located in {@code target/maven-it/.m2/} ...
   */
  Global,
  /**
   * local means earch integration tests has it's
   * own locacl cache {@code target/maven-it/../IT-MethodName/.m2/} ...
   */
  Local
}
