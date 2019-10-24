package org.apache.maven.jupiter.it.extension.maven;

public class MavenProject {

  private String groupId;

  private String artifactId;

  private String version;

  private String classifier;

  private String type;

  public MavenProject(String groupId, String artifactId, String version, String classifier, String type) {
    this.groupId = groupId;
    this.artifactId = artifactId;
    this.version = version;
    this.classifier = classifier;
    this.type = type;
  }

  public String getGroupId() {
    return groupId;
  }

  public String getArtifactId() {
    return artifactId;
  }

  public String getVersion() {
    return version;
  }

  public String getClassifier() {
    return classifier;
  }

  public String getType() {
    return type;
  }
}
