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

import org.junit.jupiter.api.condition.OS;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
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
 * @implSpec Unfortunately there is currently no option to write tests which
 * contains toFile() of Path. I've tried with https://github.com/google/jimfs
 * but they also lack the support of toFile().
 */
class MavenLocator {

  private static final String MAVEN_HOME = "maven.home";

  MavenLocator() {
  }

  Optional<String> mavenHomeFromSystemProperty() {
    if (System.getProperties().containsKey(MAVEN_HOME)) {
      return Optional.of(System.getProperty(MAVEN_HOME));
    } else {
      return Optional.empty();
    }
  }

  private File toBatFile(Path path) {
    return Paths.get(path.toAbsolutePath() + ".bat").toFile();
  }

  private File toCmdFile(Path path) {
    return Paths.get(path.toAbsolutePath() + ".cmd").toFile();
  }

  private File toFile(Path path) {
    return Paths.get(path.toString()).toFile();
  }

  Optional<File> checkOnNoneWindows(Path mavenHomeLocation) {
    Path mvnBinPath = Paths.get(mavenHomeLocation.toString(), "mvn");
    File executable = toFile(mvnBinPath);
    if (executable.exists() && executable.isFile() && executable.canRead()) {
      return Optional.of(executable);
    }
    return Optional.empty();
  }

  Optional<File> checkOnWindows(Path mavenHomeLocation) {
    Path mvnBinPath = Paths.get(mavenHomeLocation.toString(), "mvn");

    // add ".bat" for Maven 3.0.5..3.2.5 if exists
    File batFile = toBatFile(mvnBinPath);
    if (batFile.exists() && batFile.isFile() && batFile.canRead()) {
      return Optional.of(batFile);
    }

    // add ".cmd" for Maven 3.3.1... if exists
    File cmdFile = toCmdFile(mvnBinPath);
    if (cmdFile.exists() && cmdFile.isFile() && cmdFile.canRead()) {
      return Optional.of(cmdFile);
    }

    return Optional.empty();
  }

  Optional<File> checkExecutable(Path mavenHomeLocation) {
    Path path = Paths.get(mavenHomeLocation.toString());
    if (OS.WINDOWS.isCurrentOs()) {
      return checkOnWindows(path);
    } else {
      return checkOnNoneWindows(path);
    }
  }

  Optional<File> checkExecutableViaPathEnvironment() {
    Pattern pathSeparatorPattern = Pattern.compile(File.pathSeparator);
    String path = System.getenv("PATH");
    String[] split = path.split(pathSeparatorPattern.toString());
    for (String item : split) {
      Optional<File> mvnLocation = checkExecutable(Paths.get(item));
      if (mvnLocation.isPresent()) {
        return mvnLocation;
      }
    }
    return Optional.empty();
  }

  Optional<File> findMvn() {
    Optional<String> s = mavenHomeFromSystemProperty();
    if (s.isPresent()) {
      Optional<File> file = checkExecutable(Paths.get(s.get(), "bin"));
      if (!file.isPresent()) {
        return Optional.empty();
      }
      return checkExecutableViaPathEnvironment();
    }
    return checkExecutableViaPathEnvironment();
  }
}
