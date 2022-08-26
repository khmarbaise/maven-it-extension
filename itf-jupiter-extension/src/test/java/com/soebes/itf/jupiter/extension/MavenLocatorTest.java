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

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.ResourceAccessMode;
import org.junit.jupiter.api.parallel.ResourceLock;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.parallel.Resources.SYSTEM_PROPERTIES;

/**
 * Unit Test for {@link MavenLocator}.
 *
 * @author Karl Heinz Marbaise
 */
@ResourceLock(value = SYSTEM_PROPERTIES, mode = ResourceAccessMode.READ_WRITE)
class MavenLocatorTest {

  void create(FileSystem fileSystem, String pathToCreate, String fileToCreate) throws IOException {
    Path mvnBaseDirectory = fileSystem.getPath(pathToCreate);
    Files.createDirectories(mvnBaseDirectory);

    Path mvnBinary = mvnBaseDirectory.resolve(fileToCreate);
    Files.createFile(mvnBinary);
  }

  @Nested
  @DisplayName("Linux based")
  class Linux {
    static final String LINUX_MAVEN_BIN_DIRECTORY = "/tools/maven/bin";

    Optional<String> createLinuxEnvironmentPath() {
      return Optional.of("/test:/tools/maven/bin");
    }

    void createMvn(FileSystem wfs) throws IOException {
      create(wfs, LINUX_MAVEN_BIN_DIRECTORY, "mvn");
    }

    @Nested
    @DisplayName("Find Maven executable on Linux via 'maven.home' for")
    class SystemPropertyMavenHome {
      private Properties backup;
      private Optional<String> pathEnvironment;

      static final String LINUX_MAVEN_HOME_DIRECTORY = "/tools/maven";

      @BeforeEach
      void beforeEach() {
        backup = new Properties();
        backup.putAll(System.getProperties());
        this.pathEnvironment = Optional.empty();
        System.setProperty("maven.home", LINUX_MAVEN_HOME_DIRECTORY);
      }

      @AfterEach
      void restore() {
        System.setProperties(backup);
      }

      @Test
      @DisplayName("mvn")
      void work_on_linux() throws IOException {
        try (FileSystem wfs = MemoryFileSystemBuilder.newLinux().build("LinuxSystem")) {
          createMvn(wfs);

          MavenLocator mavenLocator = new MavenLocator(wfs, pathEnvironment, false);
          Optional<Path> mvn = mavenLocator.findMvn();
          assertThat(mvn).contains(wfs.getPath(LINUX_MAVEN_BIN_DIRECTORY, "mvn"));
        }
      }

    }

    @Nested
    @DisplayName("and find Maven executable via PATH for")
    class PathVariable {

      private Properties backup;
      private Optional<String> pathEnvironment;

      @BeforeEach
      void beforeEach() {
        backup = new Properties();
        backup.putAll(System.getProperties());
        this.pathEnvironment = createLinuxEnvironmentPath();
      }

      @AfterEach
      void restore() {
        System.setProperties(backup);
      }

      @Test
      @DisplayName("mvn")
      void find_via_path() throws IOException {
        try (FileSystem wfs = MemoryFileSystemBuilder.newLinux().build("LinuxSystem")) {
          createMvn(wfs);

          MavenLocator mavenLocator = new MavenLocator(wfs, pathEnvironment, false);
          Optional<Path> mvn = mavenLocator.findMvn();
          assertThat(mvn).contains(wfs.getPath(LINUX_MAVEN_BIN_DIRECTORY, "mvn"));
        }
      }

    }

    @Nested
    @DisplayName("Prefer maven.home over PATH")
    class PreferMavenHome {

      private final String LINUX_TOOLS_MAVEN_HOME_DIRECTORY = "/tools/apache-maven-3.6.3";
      private final String LINUX_TOOLS_MAVEN_BIN_DIRECTORY = "/tools/apache-maven-3.6.3/bin";

      private Properties backup;
      private Optional<String> pathEnvironment;

      void createMvn(FileSystem wfs) throws IOException {
        create(wfs, LINUX_TOOLS_MAVEN_BIN_DIRECTORY, "mvn");
      }

      @BeforeEach
      void beforeEach() {
        backup = new Properties();
        backup.putAll(System.getProperties());
        this.pathEnvironment = createLinuxEnvironmentPath();
        System.setProperty("maven.home", LINUX_TOOLS_MAVEN_HOME_DIRECTORY);
      }

      @AfterEach
      void restore() {
        System.setProperties(backup);
      }

      @Test
      @DisplayName("mvn")
      void priority_works() throws IOException {
        try (FileSystem wfs = MemoryFileSystemBuilder.newLinux().build("LinuxSystem")) {
          createMvn(wfs);
          MavenLocator mavenLocator = new MavenLocator(wfs, pathEnvironment, false);
          Optional<Path> mvn = mavenLocator.findMvn();
          assertThat(mvn).contains(wfs.getPath(LINUX_TOOLS_MAVEN_BIN_DIRECTORY, "mvn"));
        }
      }

    }

    @Nested
    @DisplayName("ITF_DEBUG")
    class ITFDebugging {

      private final String LINUX_TOOLS_MAVEN_HOME_DIRECTORY = "/tools/apache-maven-3.6.3";
      private final String LINUX_TOOLS_MAVEN_BIN_DIRECTORY = "/tools/apache-maven-3.6.3/bin";

      private Properties backup;
      private Optional<String> pathEnvironment;

      void createMvn(FileSystem wfs) throws IOException {
        create(wfs, LINUX_TOOLS_MAVEN_BIN_DIRECTORY, "mvnDebug");
      }

      @BeforeEach
      void beforeEach() {
        backup = new Properties();
        backup.putAll(System.getProperties());
        this.pathEnvironment = createLinuxEnvironmentPath();
        System.setProperty("maven.home", LINUX_TOOLS_MAVEN_HOME_DIRECTORY);
        System.setProperty("ITF_DEBUG", "true");
      }

      @AfterEach
      void restore() {
        System.setProperties(backup);
      }

      @Test
      @DisplayName("mvnDebug")
      void priority_works() throws IOException {
        try (FileSystem wfs = MemoryFileSystemBuilder.newLinux().build("LinuxSystem")) {
          createMvn(wfs);
          MavenLocator mavenLocator = new MavenLocator(wfs, pathEnvironment, false);
          Optional<Path> mvn = mavenLocator.findMvn();
          assertThat(mvn).contains(wfs.getPath(LINUX_TOOLS_MAVEN_BIN_DIRECTORY, "mvnDebug"));
        }
      }

    }

  }

  @Nested
  @DisplayName("Windows based")
  class Windows {
    static final String WINDOWS_MAVEN_BIN_DIRECTORY = "C:\\apache-maven-3.6.3\\bin";

    Optional<String> createWindowsEnvironmentPath() {
      return Optional.of("C:\\test;C:\\apache-maven-3.6.3\\bin");
    }

    void createMvnBat(FileSystem fileSystem) throws IOException {
      create(fileSystem, WINDOWS_MAVEN_BIN_DIRECTORY, "mvn.bat");
    }

    void createMvnCmd(FileSystem fileSystem) throws IOException {
      create(fileSystem, WINDOWS_MAVEN_BIN_DIRECTORY, "mvn.cmd");
    }

    @Nested
    @DisplayName("and find Maven executable via 'maven.home' for")
    class SystemPropertyMavenHome {
      private Properties backup;
      private Optional<String> pathEnvironment;

      static final private String WINDOWS_MAVEN_HOME_DIRECTORY = "C:\\apache-maven-3.6.3";

      @BeforeEach
      void beforeEach() {
        backup = new Properties();
        backup.putAll(System.getProperties());
        this.pathEnvironment = Optional.empty();
        System.setProperty("maven.home", WINDOWS_MAVEN_HOME_DIRECTORY);
      }

      @AfterEach
      void restore() {
        System.setProperties(backup);
      }

      @Test
      @DisplayName("mvn.bat")
      void work_on_windows_with_bat() throws IOException {
        try (FileSystem wfs = MemoryFileSystemBuilder.newWindows().build("WindowsSystem")) {
          createMvnBat(wfs);

          MavenLocator mavenLocator = new MavenLocator(wfs, pathEnvironment, true);
          Optional<Path> mvn = mavenLocator.findMvn();
          assertThat(mvn).contains(wfs.getPath(WINDOWS_MAVEN_BIN_DIRECTORY, "mvn.bat"));
        }
      }

      @Test
      @DisplayName("mvn.cmd")
      void work_on_windows_with_cmd() throws IOException {
        try (FileSystem wfs = MemoryFileSystemBuilder.newWindows().build("WindowsSystem")) {
          createMvnCmd(wfs);

          MavenLocator mavenLocator = new MavenLocator(wfs, pathEnvironment, true);
          Optional<Path> mvn = mavenLocator.findMvn();
          assertThat(mvn).contains(wfs.getPath(WINDOWS_MAVEN_BIN_DIRECTORY, "mvn.cmd"));
        }
      }

    }

    @Nested
    @DisplayName("and find Maven executable via PATH for")
    class PathVariable {

      private Properties backup;
      private Optional<String> pathEnvironment;

      @BeforeEach
      void beforeEach() {
        backup = new Properties();
        backup.putAll(System.getProperties());
        this.pathEnvironment = createWindowsEnvironmentPath();
      }

      @AfterEach
      void restore() {
        System.setProperties(backup);
      }

      @Test
      @DisplayName("mvn.bat")
      void work_on_windows_with_bat() throws IOException {
        try (FileSystem wfs = MemoryFileSystemBuilder.newWindows().build("WindowsSystem")) {
          createMvnBat(wfs);

          MavenLocator mavenLocator = new MavenLocator(wfs, pathEnvironment, true);
          Optional<Path> mvn = mavenLocator.findMvn();
          assertThat(mvn).contains(wfs.getPath(WINDOWS_MAVEN_BIN_DIRECTORY, "mvn.bat"));
        }
      }

      @Test
      @DisplayName("mvn.cmd")
      void work_on_windows_with_cmd() throws IOException {
        try (FileSystem wfs = MemoryFileSystemBuilder.newWindows().build("WindowsSystem")) {
          createMvnCmd(wfs);

          MavenLocator mavenLocator = new MavenLocator(wfs, pathEnvironment, true);
          Optional<Path> mvn = mavenLocator.findMvn();
          assertThat(mvn).contains(wfs.getPath(WINDOWS_MAVEN_BIN_DIRECTORY, "mvn.cmd"));
        }
      }

    }

    @Nested
    @DisplayName("Prefer maven.home over PATH")
    class PreferMavenHome {

      private final String WINDOWS_TOOLS_MAVEN_HOME_DIRECTORY = "C:\\tools\\maven";
      private final String WINDOWS_TOOLS_MAVEN_BIN_DIRECTORY = "C:\\tools\\maven\\bin";

      private Properties backup;
      private Optional<String> pathEnvironment;

      void createToolsBat(FileSystem wfs) throws IOException {
        create(wfs, WINDOWS_TOOLS_MAVEN_BIN_DIRECTORY, "mvn.bat");
      }

      void createToolsCmd(FileSystem wfs) throws IOException {
        create(wfs, WINDOWS_TOOLS_MAVEN_BIN_DIRECTORY, "mvn.cmd");
      }

      @BeforeEach
      void beforeEach() {
        backup = new Properties();
        backup.putAll(System.getProperties());
        this.pathEnvironment = createWindowsEnvironmentPath();
        System.setProperty("maven.home", WINDOWS_TOOLS_MAVEN_HOME_DIRECTORY);
      }

      @AfterEach
      void restore() {
        System.setProperties(backup);
      }

      @Test
      @DisplayName("mvn.bat")
      void priority_works_with_bat() throws IOException {
        try (FileSystem wfs = MemoryFileSystemBuilder.newWindows().build("WindowsSystem")) {
          createMvnBat(wfs);
          createToolsBat(wfs);
          MavenLocator mavenLocator = new MavenLocator(wfs, pathEnvironment, true);
          Optional<Path> mvn = mavenLocator.findMvn();
          assertThat(mvn).contains(wfs.getPath(WINDOWS_TOOLS_MAVEN_BIN_DIRECTORY, "mvn.bat"));
        }
      }

      @Test
      @DisplayName("mvn.cmd")
      void priority_works_with_cmd() throws IOException {
        try (FileSystem wfs = MemoryFileSystemBuilder.newWindows().build("WindowsSystem")) {
          createMvnCmd(wfs);
          createToolsCmd(wfs);

          MavenLocator mavenLocator = new MavenLocator(wfs, pathEnvironment, true);
          Optional<Path> mvn = mavenLocator.findMvn();
          assertThat(mvn).contains(wfs.getPath(WINDOWS_TOOLS_MAVEN_BIN_DIRECTORY, "mvn.cmd"));
        }
      }

    }

    @Nested
    @DisplayName("ITF_DEBUG")
    class ITFDebugging {

      private final String WINDOWS_TOOLS_MAVEN_HOME_DIRECTORY = "C:\\tools\\maven";
      private final String WINDOWS_TOOLS_MAVEN_BIN_DIRECTORY = "C:\\tools\\maven\\bin";

      private Properties backup;
      private Optional<String> pathEnvironment;

      void createToolsDebugBat(FileSystem wfs) throws IOException {
        create(wfs, WINDOWS_TOOLS_MAVEN_BIN_DIRECTORY, "mvnDebug.bat");
      }

      void createToolsDebugCmd(FileSystem wfs) throws IOException {
        create(wfs, WINDOWS_TOOLS_MAVEN_BIN_DIRECTORY, "mvnDebug.cmd");
      }

      @BeforeEach
      void beforeEach() {
        backup = new Properties();
        backup.putAll(System.getProperties());
        this.pathEnvironment = createWindowsEnvironmentPath();
        System.setProperty("maven.home", WINDOWS_TOOLS_MAVEN_HOME_DIRECTORY);
        System.setProperty("ITF_DEBUG", "true");
      }

      @AfterEach
      void restore() {
        System.setProperties(backup);
      }

      @Test
      @DisplayName("mvnDebug.bat")
      void priority_works_with_debug_bat() throws IOException {
        try (FileSystem wfs = MemoryFileSystemBuilder.newWindows().build("WindowsSystem")) {
          createMvnBat(wfs);
          createToolsDebugBat(wfs);
          MavenLocator mavenLocator = new MavenLocator(wfs, pathEnvironment, true);
          Optional<Path> mvn = mavenLocator.findMvn();
          assertThat(mvn).contains(wfs.getPath(WINDOWS_TOOLS_MAVEN_BIN_DIRECTORY, "mvnDebug.bat"));
        }
      }

      @Test
      @DisplayName("mvnDebug.cmd")
      void priority_works_with_debug_cmd() throws IOException {
        try (FileSystem wfs = MemoryFileSystemBuilder.newWindows().build("WindowsSystem")) {
          createMvnCmd(wfs);
          createToolsDebugCmd(wfs);

          MavenLocator mavenLocator = new MavenLocator(wfs, pathEnvironment, true);
          Optional<Path> mvn = mavenLocator.findMvn();
          assertThat(mvn).contains(wfs.getPath(WINDOWS_TOOLS_MAVEN_BIN_DIRECTORY, "mvnDebug.cmd"));
        }
      }

    }

  }


}