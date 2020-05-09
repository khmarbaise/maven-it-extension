package com.soebes.itf.jupiter.extension;

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

import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

/**
 * It's the intention to find the {@code mvn} executable.
 * <p>
 * The {@code maven.home} is set during the execution of the
 * test by the user.
 * The {@code maven.home} has precedence over searching
 * of Maven via {@code PATH}. This gives the opportunity
 * to overload the given Maven version in cases where needed.
 *
 * @author Karl Heinz Marbaise
 * @implNote Currently {@code maven.home} is given to the maven-failsafe-plugin
 * configuration as a system property.
 * The parameter {@code isRunningOnWindows} is used instead
 * of using things like {@code org.junit.jupiter.api.condition.OS.WINDOWS.isCurrentOs()} cause
 * they contain static initializers etc. which can't be tested.
 */
class MavenLocator {

  /**
   * The name of the system property.
   */
  private static final String MAVEN_HOME = "maven.home";


  private final FileSystem fileSystem;
  private final Map<String, String> environment;
  private final boolean isRunningOnWindows;

  /**
   * @param fileSystem The {@link FileSystem} which is used.
   * @param environment The environment which is used. Needed to find {@code PATH}
   * @param isRunningOnWindows {@code true} when running on Windows, false otherwise.
   */
  MavenLocator(FileSystem fileSystem, Map<String, String> environment, boolean isRunningOnWindows) {
    this.fileSystem = fileSystem;
    this.environment = environment;
    this.isRunningOnWindows = isRunningOnWindows;
  }

  private Path intoPath(String s) {
    return this.fileSystem.getPath(s);
  }

  private Path intoBin(Path p) {
    return p.resolve("bin");
  }
  private Optional<String> mavenHomeFromSystemProperty() {
    if (System.getProperties().containsKey(MAVEN_HOME)) {
      //TODO: Need to reconsider in cases where defined {@code maven.home} with empty value?
      return Optional.of(System.getProperty(MAVEN_HOME));
    } else {
      return Optional.empty();
    }
  }

  private Path toMvn(Path p) {
    return p.resolve("mvn");
  }

  private Path toBat(Path p) {
    return p.resolve("mvn.bat");
  }

  private Path toCmd(Path p) {
    return p.resolve("mvn.cmd");
  }

  private boolean isExecutable(Path s) {
    return Files.isRegularFile(s)
        && Files.isReadable(s)
        && Files.isExecutable(s);
  }


  Optional<Path> executableNonWindows(Path s) {
    Path mvn = toMvn(s);
    if (isExecutable(mvn)) {
      return Optional.of(mvn);
    }

    return Optional.empty();
  }

  private Optional<Path> executableWindows(Path s) {
    Path mvnBat = toBat(s);
    //Maven 3.0.5...3.2.5
    if (isExecutable(mvnBat)) {
      return Optional.of(mvnBat);
    }

    //Maven 3.3.1...
    Path mvnCmd = toCmd(s);
    if (isExecutable(mvnCmd)) {
      return Optional.of(mvnCmd);
    }
    return Optional.empty();
  }

  private Optional<Path> executable(Path p) {
    if (this.isRunningOnWindows) {
      return executableWindows(p);
    } else {
      return executableNonWindows(p);
    }
  }

  private Optional<Path> checkExecutableViaPathEnvironment() {
    String PATH_SEPARATOR = this.isRunningOnWindows ? ";" : ":";
    //TODO: Check why FileSystem.getPathSeparator does not exist?
    Pattern pathSeparatorPattern = Pattern.compile(Pattern.quote(PATH_SEPARATOR));
    if (environment.containsKey("PATH")) {
      String pathEnv = environment.get("PATH");
      String[] splittedParts = pathEnv.split(pathSeparatorPattern.toString());
      for (String item : splittedParts) {
        Path path = intoPath(item);
        Optional<Path> executable = executable(path);
        if (executable.isPresent()) {
          return executable;
        }
      }
    }
    return Optional.empty();
  }

  Optional<Path> findMvn() {
    Optional<String> s = mavenHomeFromSystemProperty();
    if (s.isPresent()) {
      Path path = intoBin(intoPath(s.get()));
      Optional<Path> executable = executable(path);
      if (executable.isPresent()) {
        return executable;
      }
    }
    return checkExecutableViaPathEnvironment();
  }
}
