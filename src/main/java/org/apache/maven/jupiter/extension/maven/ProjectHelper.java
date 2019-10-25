package org.apache.maven.jupiter.extension.maven;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;

public class ProjectHelper {

  public ProjectHelper() {
  }

  public static Model readProject() {
    MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
//    return mavenXpp3Reader.read();
    return null;
  }
}
