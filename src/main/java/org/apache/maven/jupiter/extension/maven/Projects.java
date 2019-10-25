package org.apache.maven.jupiter.extension.maven;

import java.util.List;

public class Projects {

  //FIXME: Need to use a real tree structure
  // to support real project reactors with different levels etc.
  private List<MavenProject> projects;

  public Projects(List<MavenProject> projects) {
    this.projects = projects;
  }

  public List<MavenProject> getProjects() {
    return projects;
  }
}
