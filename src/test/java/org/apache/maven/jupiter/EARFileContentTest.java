package org.apache.maven.jupiter;

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

      jarFile.stream().forEach(jarEntry -> System.out.println("jarEntry = " + jarEntry));
      System.out.println("---------------");
      jarFile.stream().forEach(jarEntry -> System.out.println("jarEntry = " + jarEntry.getName()));

//      assertThat(jarFile.stream()).extracting(jarEntry -> jarEntry.getName()).containsOnly(files);
    }
  }
}
