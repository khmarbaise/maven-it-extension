package org.apache.maven.jupiter.it.extension.maven;

public class MavenExecutionConfiguration {

  private final MavenVersion[] mavenVersions;

  private final MavenCache mavenCache;

  public MavenExecutionConfiguration(MavenVersion[] mavenVersions, MavenCache mavenCache) {
    this.mavenVersions = mavenVersions;
    this.mavenCache = mavenCache;
  }

  public MavenVersion[] getMavenVersions() {
    return mavenVersions;
  }

  public MavenCache getMavenCache() {
    return mavenCache;
  }
}
