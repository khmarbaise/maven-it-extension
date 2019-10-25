package org.apache.maven.jupiter.extension.maven;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public final class ProjectHelper {

  private ProjectHelper() {
    // intentionally private.
  }

  public static Model readProject(File directory) {
    MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
    try {
      InputStream is = new FileInputStream(new File(directory, "pom.xml"));
      Model read = mavenXpp3Reader.read(is);
      is.close();
      return read;
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (XmlPullParserException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
