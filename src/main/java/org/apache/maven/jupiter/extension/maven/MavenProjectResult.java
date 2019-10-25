package org.apache.maven.jupiter.extension.maven;

import java.io.File;
import org.apache.maven.model.Model;

/**
 * @author Karl Heinz Marbaise
 */
public class MavenProjectResult {

  private final File baseDir;
  private final Model model;

  public MavenProjectResult(File baseDir, Model model) {
    this.baseDir = baseDir;
    this.model = model;
  }

  public File getBaseDir() {
    return baseDir;
  }

  public Model getModel() {
    return model;
  }
}
