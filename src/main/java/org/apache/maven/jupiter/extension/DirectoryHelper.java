package org.apache.maven.jupiter.extension;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * @author Karl Heinz Marbaise
 */
public class DirectoryHelper {


  public static File getMavenBaseDir() {
    return new File(System.getProperty("basedir", System.getProperty("user.dir", ".")));
  }

  public static String toFullyQualifiedPath(final Package context, final String resourceName) {
    return context.getName().replace('.', '/') + "/" + resourceName;
  }
  public static String toFullyQualifiedPath(final Package context) {
    return toFullyQualifiedPath(context, "");
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
