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
 * of using things like {@code org.junit.jupiter.api.condition.OS.WINDOWS.isCurrentOs()} because
 * they contain static initializers etc. which can't be tested.
 */
class MavenLocator {

  /**
   * The name of the system property.
   */
  private static final String MAVEN_HOME = "maven.home";

  /**
   * The name of the system property to activate remote debugging.
   */
  private static final String ITF_DEBUG = "ITF_DEBUG";


  private final FileSystem fileSystem;
  private final Optional<String> pathEnvironment;
  private final boolean isRunningOnWindows;
  private final String  mvnExecutable;

  /**
   * @param fileSystem The {@link FileSystem} which is used.
   * @param pathEnvironment The value of {@code PATH} if it exists otherwise {@link Optional#empty()}.
   * @param isRunningOnWindows {@code true} when running on Windows, false otherwise.
   */
  MavenLocator(FileSystem fileSystem, Optional<String> pathEnvironment, boolean isRunningOnWindows) {
    this.fileSystem = fileSystem;
    this.pathEnvironment = pathEnvironment;
    this.isRunningOnWindows = isRunningOnWindows;
    this.mvnExecutable = Boolean.getBoolean(ITF_DEBUG) ? "mvnDebug" : "mvn";
  }

  private Path intoPath(String s) {
    return this.fileSystem.getPath(s);
  }

  private Path intoBin(Path p) {
    return p.resolve("bin");
  }

  private Optional<String> mavenHomeFromSystemProperty() {
    return Optional.ofNullable(System.getProperty(MAVEN_HOME));
  }

  private Path toMvn(Path p) {
    return p.resolve(this.mvnExecutable);
  }

  private Path toBat(Path p) {
    return p.resolve(this.mvnExecutable + ".bat");
  }

  private Path toCmd(Path p) {
    return p.resolve(this.mvnExecutable + ".cmd");
  }

  private boolean isExecutable(Path s) {
    return Files.isRegularFile(s)
        && Files.isReadable(s)
        && Files.isExecutable(s);
  }

  private Optional<Path> executableNonWindows(Path s) {
    return Optional.of(toMvn(s))
        .filter(this::isExecutable);
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
    if (!this.pathEnvironment.isPresent()) {
      return Optional.empty();
    }

    String pathSeparator = this.isRunningOnWindows ? ";" : ":";
    Pattern pathSeparatorPattern = Pattern.compile(Pattern.quote(pathSeparator));
    return pathSeparatorPattern.splitAsStream(pathEnvironment.get())
        .map(this::intoPath)
        .map(this::executable)
        .filter(Optional::isPresent)
        .findFirst()
        .orElse(Optional.empty());
  }

  Optional<Path> findMvn() {
    Optional<Path> path = mavenHomeFromSystemProperty()
        .map(this::intoPath)
        .map(this::intoBin)
        .flatMap(this::executable);
    if (path.isPresent()) {
      return path;
    }
    return checkExecutableViaPathEnvironment();
  }
}
