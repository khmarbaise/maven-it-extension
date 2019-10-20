package org.apache.maven.jupiter.it.extension;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DirectoryHelper {


  public static File getMavenBaseDir() {
    return new File(System.getProperty("basedir", System.getProperty("user.dir", ".")));
  }

  /**
   * Return the target directory of the current project.
   */
  public static File getTargetDir() {
    return new File(getMavenBaseDir(), "target");
  }

  // https://stackoverflow.com/a/1184263/1431016
  public static Path path(String string) {
    char escape = '%'; // ... or some other legal char.
    int len = string.length();
    StringBuilder builder = new StringBuilder(len);
    for (int i = 0; i < len; i++) {
      char ch = string.charAt(i);
      if (ch == ':') { // `:` is illegal in java.nio.file.Path
        builder.append('~');
        continue;
      }
      if (ch < ' '
          || ch >= 0x7F // || ch == fileSep || ... // add other illegal chars
          || (ch == '.' && i == 0) // we don't want to collide with "." or ".."!
          || ch == escape) {
        builder.append(escape);
        if (ch < 0x10) {
          builder.append('0');
        }
        builder.append(Integer.toHexString(ch));
        continue;
      }
      builder.append(ch);
    }
    return Paths.get(builder.toString());
  }
}
