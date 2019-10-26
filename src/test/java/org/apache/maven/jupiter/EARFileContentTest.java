package org.apache.maven.jupiter;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.jar.JarFile;
import org.junit.jupiter.api.Test;

class EARFileContentTest {

  @Test
  void name() throws IOException {
    URL resource = this.getClass().getResource("/example-files/test-1.0.ear");
    File earFile = new File(resource.getFile());
    String[] files = {"META-INF/application.xml", "APP-INF/classes/foo.properties"};
    try (JarFile jarFile = new JarFile(earFile)) {

      assertThat(jarFile.stream()).extracting(jarEntry -> jarEntry.getName()).containsOnlyOnce(files);
//      boolean b = Stream.of(files)
//          .peek(s -> System.out.println("file = " + s))
//          .allMatch(fileItem -> jarFile.stream()
//              .peek(jarEntry -> System.out.println(" -> jarEntry = " + jarEntry))
//              .allMatch(jarEntry -> fileItem.equals(jarEntry.getName())));
//      System.out.println("b = " + b);
    }
  }
}
