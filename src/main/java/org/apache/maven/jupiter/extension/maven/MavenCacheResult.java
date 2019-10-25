package org.apache.maven.jupiter.extension.maven;

import java.nio.file.Path;

public class MavenCacheResult {

  private Path stdout;

  public MavenCacheResult(Path stdout) {
    this.stdout = stdout;
  }

  public Path getStdout() {
    return stdout;
  }
}
