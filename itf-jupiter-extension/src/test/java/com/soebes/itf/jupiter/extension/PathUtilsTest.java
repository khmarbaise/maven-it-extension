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

import static org.assertj.core.api.Assertions.assertThat;

import com.github.marschall.memoryfilesystem.MemoryFileSystemBuilder;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class PathUtilsTest {

  private void writeFile(Path source, String content) throws IOException {
    Path hiddenFile = source.resolve(source);
    Files.write(hiddenFile, content.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * <pre>
   *   /
   *   +-- source
   *         +-- pom.xml
   *         +-- .hiddenfile
   *         +-- subsource
   *              +-- fileInSubSource.txt
   * </pre>
   */
  @Test
  void copyDirectory() throws IOException {
    try (FileSystem wfs = MemoryFileSystemBuilder.newLinux().build("LinuxSystem")) {
      Path rootSourceDirectory = wfs.getPath("/");
      Files.createDirectories(rootSourceDirectory);

      Path source = rootSourceDirectory.resolve("/source");
      Files.createDirectory(source);

      Path pomFile = source.resolve("pom.xml");
      String pomContent = "Test";
      writeFile(pomFile, pomContent);

      Path hiddenFile = source.resolve(".hiddenfile");
      String contentHiddenFile = "This is a hidden file";
      writeFile(hiddenFile, contentHiddenFile);

      Path sourceSub = rootSourceDirectory.resolve("/source").resolve("subsource");
      Files.createDirectory(sourceSub);

      Path fileInSubSource = sourceSub.resolve("fileInSubSource.txt");
      String contentFileInSubSource = "Content Of File in subdirectory of source";
      writeFile(fileInSubSource, contentFileInSubSource);

      Path target = rootSourceDirectory.resolve("/target");

      PathUtils.copyDirectoryRecursively(source, target);

      assertThat(target).isDirectory();

      Path expectedPomFile = target.resolve("/target").resolve("pom.xml");
      assertThat(expectedPomFile).isRegularFile().hasContent(pomContent);

      Path expectedHiddenFile = target.resolve("/target").resolve(".hiddenfile");
      assertThat(expectedHiddenFile).isRegularFile().hasContent(contentHiddenFile);

      Path expectedFileInSource = target.resolve("subsource").resolve("fileInSubSource.txt");
      assertThat(expectedFileInSource).isRegularFile().hasContent(contentFileInSubSource);
    }
  }

  @Test
  void deleteDir() throws IOException {
    try (FileSystem wfs = MemoryFileSystemBuilder.newLinux().build("LinuxSystem")) {
      Path rootSourceDirectory = wfs.getPath("/");
      Files.createDirectories(rootSourceDirectory);

      Path source = rootSourceDirectory.resolve("/source");
      Files.createDirectory(source);

      Path pomFile = source.resolve("pom.xml");
      String pomContent = "Test";
      writeFile(pomFile, pomContent);

      Path hiddenFile = source.resolve(".hiddenfile");
      String contentHiddenFile = "This is a hidden file";
      writeFile(hiddenFile, contentHiddenFile);

      Path sourceSub = rootSourceDirectory.resolve("/source").resolve("subsource");
      Files.createDirectory(sourceSub);

      Path fileInSubSource = sourceSub.resolve("fileInSubSource.txt");
      String contentFileInSubSource = "Content Of File in subdirectory of source";
      writeFile(fileInSubSource, contentFileInSubSource);

      PathUtils.deleteRecursively(source);
    }
  }

}