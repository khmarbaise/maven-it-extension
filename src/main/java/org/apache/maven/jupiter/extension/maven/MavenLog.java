package org.apache.maven.jupiter.extension.maven;

import java.nio.file.Path;

public class MavenLog {

  private Path stdout;
  private Path stderr;

  public MavenLog(Path stdout, Path stderr) {
    this.stdout = stdout;
    this.stderr = stderr;
  }

  public Path getStdout() {
    return stdout;
  }

  public Path getStderr() {
    return stderr;
  }
}
