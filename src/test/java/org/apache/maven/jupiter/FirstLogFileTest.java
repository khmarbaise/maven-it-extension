package org.apache.maven.jupiter;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class FirstLogFileTest {

  @Test
  void first() {
    InputStream resourceAsStream = this.getClass().getResourceAsStream("/mvn-stdout.out");
    Stream<String> lines = new BufferedReader(new InputStreamReader(resourceAsStream, Charset.defaultCharset())).lines();
    lines.filter(s -> s.startsWith("[INFO]")).findFirst();
  }
}
