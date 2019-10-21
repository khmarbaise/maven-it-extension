package org.apache.maven.jupiter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.jupiter.api.Test;

class FirstLogFileTest {

  @Test
  void first() throws IOException {
    Path p = Paths.get("/mvn-stdout.out");
    Files.readAllLines(p).stream().filter(s -> s.startsWith("[INFO]")).findFirst();
  }
}
