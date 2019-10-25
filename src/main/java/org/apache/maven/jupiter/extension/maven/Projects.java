package org.apache.maven.jupiter.extension.maven;

import java.util.List;

/**
 * @author Karl Heinz Marbaise
 */
public class Projects {

  //FIXME: Need to use a real tree structure
  // to support real project reactors with different levels etc.
  private List<MavenProjectResult> projects;

  public Projects(List<MavenProjectResult> projects) {
    this.projects = projects;
  }

  public List<MavenProjectResult> getProjects() {
    return projects;
  }
}
