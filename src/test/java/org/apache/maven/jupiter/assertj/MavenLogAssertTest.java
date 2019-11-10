package org.apache.maven.jupiter.assertj;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;

class MavenLogAssertTest {

  Stream<String> createLogStream() {
    InputStream resourceAsStream = this.getClass().getResourceAsStream("/mvn-stdout.out");
    return new BufferedReader(new InputStreamReader(resourceAsStream, Charset.defaultCharset())).lines();
  }

  @Test
  void first_test() {
//    return Paths.get(loggingDirectory.toString(), this.prefix + "-stdout.out");

    //MavenLogAssert mavenLogAssert = new MavenLogAssert();

    List<String> infoList = createLogStream().filter(p -> p.startsWith("[INFO]"))
        .map(s -> s.substring(7))
        .collect(Collectors.toList());

    List<String> debugList = createLogStream().filter(p -> p.startsWith("[DEBUG]"))
        .map(s -> s.substring(7))
        .collect(Collectors.toList());
    List<String> errorList = createLogStream().filter(p -> p.startsWith("[ERROR]"))
        .map(s -> s.substring(7))
        .collect(Collectors.toList());
    List<String> warningList = createLogStream().filter(p -> p.startsWith("[WARNING]"))
        .map(s -> s.substring(9))
        .collect(Collectors.toList());

    List<String> collect = infoList.stream()
        .filter(s -> !s.startsWith("Downloading from "))
        .filter(s -> !s.startsWith("Downloaded from "))
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList());
    //infoList.forEach(s -> System.out.println(s));
  }
}