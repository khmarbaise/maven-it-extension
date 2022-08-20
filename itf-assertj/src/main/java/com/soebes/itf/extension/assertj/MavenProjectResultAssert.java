package com.soebes.itf.extension.assertj;

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

import com.soebes.itf.jupiter.maven.MavenProjectResult;
import com.soebes.itf.jupiter.maven.ProjectHelper;
import org.apache.maven.model.Model;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractPathAssert;
import org.assertj.core.api.Assertions;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;

/**
 * @author Karl Heinz Marbaise
 */
public class MavenProjectResultAssert extends AbstractAssert<MavenProjectResultAssert, MavenProjectResult> {

  private static final String THE_TARGET_DIRECTORY_DOES_NOT_EXIST = "The target directory of <%s> does not exist.";
  private static final String THE_EAR_FILE_DOES_NOT_EXIST = "The ear file <%s> does not exist or can not be read.";
  private static final String EXPECT_HAVING_A_MODULE = "expected having a module <%s> which does not exist";
  private static final String TARGET = "target";

  private Optional<MavenProjectResultAssert> parent;

  MavenProjectResultAssert(MavenProjectResult actual) {
    super(actual, MavenProjectResultAssert.class);
    this.parent = Optional.empty();
  }

  /**
   * An entry point for MavenExecutionResultAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(result).isSuccessful();</code>
   *
   * @param actual the MavenExecutionResult we want to make assertions on.
   * @return a new {@link MavenProjectResultAssert}
   */
  public static MavenProjectResultAssert assertThat(MavenProjectResult actual) {
    return new MavenProjectResultAssert(actual);
  }

  public MavenProjectResultAssert hasTarget() {
    isNotNull();
    Path target = this.actual.getTargetProjectDirectory().resolve(TARGET);

    if (!isDirectory(target) || !exists(target) || isHidden(target)) {
      failWithMessage(THE_TARGET_DIRECTORY_DOES_NOT_EXIST, actual.getTargetBaseDirectory().toAbsolutePath());
    }
    return myself;
  }

  public MavenProjectResultAssert has(String directory) {
    isNotNull();
    Path target = this.actual.getTargetProjectDirectory().resolve(directory);
    if (!isDirectory(target) || !exists(target) || isHidden(target)) {
      failWithMessage(THE_TARGET_DIRECTORY_DOES_NOT_EXIST, directory,
          actual.getTargetBaseDirectory().toAbsolutePath());
    }
    return myself;
  }

  public AbstractPathAssert<?> withFile(String fileName) {
    isNotNull();
    //FIXME: wrong way...need to reconsider.
    Path target = this.actual.getTargetProjectDirectory().resolve(TARGET);
    if (!isDirectory(target) || !exists(target) || isHidden(target)) {
      failWithMessage(THE_TARGET_DIRECTORY_DOES_NOT_EXIST, actual.getTargetBaseDirectory().toAbsolutePath());
    }
    Path fileNameFile = target.resolve(fileName);
    return Assertions.assertThat(fileNameFile);
  }

  public ArchiveAssert withEarFile() {
    isNotNull();

    Model model = this.actual.getModel();
    Path target = this.actual.getTargetProjectDirectory().resolve(TARGET);
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".ear";
    Path earFile = target.resolve(artifact);
    if (!isRegularFile(earFile) || !isReadable(earFile) || isHidden(earFile)) {
      failWithMessage(THE_EAR_FILE_DOES_NOT_EXIST, earFile.toAbsolutePath());
    }

    return new ArchiveAssert(earFile, this.actual.getModel(), this.myself);
  }

  public ArchiveAssert withJarFile() {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    Path target = this.actual.getTargetProjectDirectory().resolve(TARGET);
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".jar";
    Path jarFile = target.resolve(artifact);
    if (!isRegularFile(jarFile) && !isReadable(jarFile)) {
      failWithMessage(THE_EAR_FILE_DOES_NOT_EXIST, jarFile.toAbsolutePath());
    }
    return new ArchiveAssert(jarFile, this.actual.getModel(), this.myself);
  }

  public ArchiveAssert withWarFile() {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    Path target = this.actual.getTargetProjectDirectory().resolve(TARGET);
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".war";
    Path warFile = target.resolve(artifact);
    if (!isRegularFile(warFile) && !isReadable(warFile)) {
      failWithMessage(THE_EAR_FILE_DOES_NOT_EXIST, warFile.toAbsolutePath());
    }
    return new ArchiveAssert(warFile, this.actual.getModel(), this.myself);
  }

  public ArchiveAssert withRarFile() {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    Path target = this.actual.getTargetProjectDirectory().resolve(TARGET);
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".rar";
    Path rarFile = target.resolve(artifact);
    if (!isRegularFile(rarFile) && !isReadable(rarFile)) {
      failWithMessage(THE_EAR_FILE_DOES_NOT_EXIST, rarFile.toAbsolutePath());
    }
    return new ArchiveAssert(rarFile, this.actual.getModel(), this.myself);
  }

  public MavenProjectResultAssert contains(List<String> files) {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    Path target = this.actual.getTargetProjectDirectory().resolve(TARGET);
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".ear";
    Path earFile = target.resolve(artifact);

    try (JarFile jarFile = new JarFile(earFile.toFile())) {
      if (!files.stream()
          .allMatch(fileEntry -> jarFile.stream().anyMatch(jarEntry -> fileEntry.equals(jarEntry.getName())))) {
        failWithMessage(THE_EAR_FILE_DOES_NOT_EXIST, files);
      }
    } catch (IOException e) {
      failWithMessage("IOException happened. <%s> file:<%s>", e.getMessage(), earFile.toAbsolutePath());
    }
    return myself;
  }

  /**
   * A module can have a `target` directory or but in contradiction to a an aggregator project which does not have a
   * `target` directory. So it shouldn't be checked.
   *
   * @param moduleName The name of the module.
   * @return {@link MavenProjectResultAssert}
   */
  public MavenProjectResultAssert hasModule(String moduleName) {
    isNotNull();

    Path moduleNameFile = this.actual.getTargetProjectDirectory().resolve(moduleName);

    if (!exists(moduleNameFile) || !isHidden(moduleNameFile) && !isDirectory(moduleNameFile)) {
      failWithMessage(EXPECT_HAVING_A_MODULE, moduleName);
    }
    return myself;
  }

  public MavenProjectResultAssert withModule(String moduleName) {
    isNotNull();

    Path moduleNameFile = this.actual.getTargetProjectDirectory().resolve(moduleName);

    if (!exists(moduleNameFile) || !isHidden(moduleNameFile) && !isDirectory(moduleNameFile)) {
      failWithMessage(EXPECT_HAVING_A_MODULE, moduleName);
    }

    Model model = ProjectHelper.readProject(moduleNameFile.resolve("pom.xml"));
    //FIXME: Need to reconsider the following call. Maybe we need to use a different ProjectResult here?
    // because it conflicts with the assumption of MavenProjectResult.
    MavenProjectResult mavenProjectResult = new MavenProjectResult(moduleNameFile, moduleNameFile, this.actual.getTargetCacheDirectory(), model);
    return new MavenProjectResultAssert(mavenProjectResult);
  }


  @Override
  public MavenProjectResultAssert isEqualTo(Object expected) {
    objects.assertEqual(info, actual, expected);
    return myself;
  }

  private boolean isDirectory(Path path) {
    return Files.isDirectory(path);
  }

  private boolean exists(Path path) {
    return Files.exists(path);
  }

  private boolean isRegularFile(Path path) {
    return Files.isRegularFile(path);
  }

  private boolean isHidden(Path path) {
    try {
      return Files.isHidden(path);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  private boolean isReadable(Path path) {
    return Files.isReadable(path);
  }

  /**
   * @throws UnsupportedOperationException if this method is called.
   * @implNote java:S1133: Suppressing "Do not forget to remove this deprecated code someday." message.
   * @deprecated use {@link #isEqualTo} instead
   */
  @Override
  @Deprecated
  @SuppressWarnings("java:S1133")
  public boolean equals(Object obj) {
    throw new UnsupportedOperationException("'equals' is not supported...maybe you intended to call 'isEqualTo'");
  }

  /**
   * Always returns 1.
   *
   * @return 1.
   */
  @Override
  public int hashCode() {
    return 1;
  }
}
