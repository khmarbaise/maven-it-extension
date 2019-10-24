package org.apache.maven.jupiter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class FirstLogFileTest {

  @Test
  @Disabled("Not working yet.")
  void first() throws IOException {
    InputStream resourceAsStream = this.getClass().getResourceAsStream("/mvn-stdout.out");
    Stream<String> lines = new BufferedReader(new InputStreamReader(resourceAsStream, Charset.defaultCharset())).lines();
    lines.filter(s -> s.startsWith("[INFO]")).findFirst();
  }
}
