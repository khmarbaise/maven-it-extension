package org.apache.maven.assertj;

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
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.jar.JarFile;
import org.apache.maven.jupiter.maven.MavenProjectResult;
import org.apache.maven.jupiter.maven.ProjectHelper;
import org.apache.maven.model.Model;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.AbstractFileAssert;
import org.assertj.core.api.Assertions;

/**
 * @author Karl Heinz Marbaise
 */
public class MavenProjectResultAssert extends AbstractAssert<MavenProjectResultAssert, MavenProjectResult> {

  private Optional<MavenProjectResultAssert> parent;

  protected MavenProjectResultAssert(MavenProjectResult actual) {
    super(actual, MavenProjectResultAssert.class);
    this.parent = Optional.empty();
  }

  protected MavenProjectResultAssert(MavenProjectResult actual, MavenProjectResultAssert parent) {
    super(actual, MavenProjectResultAssert.class);
    this.parent = Optional.of(parent);
  }

  /**
   * An entry point for MavenExecutionResultAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(result).isSuccessful();</code>
   *
   * @param actual the MavenExecutionResult we want to make assertions on.
   * @return a new </code>{@link MavenProjectResultAssert}</code>
   */
  public static MavenProjectResultAssert assertThat(MavenProjectResult actual) {
    return new MavenProjectResultAssert(actual);
  }

  public MavenProjectResultAssert hasTarget() {
    isNotNull();
    File target = new File(this.actual.getBaseDir(), "target");
    if (!target.isDirectory() || !target.exists() || target.isHidden()) {
      failWithMessage("The target directory of <%s> does not exist.", actual.getBaseDir().getAbsolutePath());
    }
    return myself;
  }

  public MavenProjectResultAssert has(String directory) {
    isNotNull();
    File target = new File(this.actual.getBaseDir(), directory);
    if (!target.isDirectory() || !target.exists() || target.isHidden()) {
      failWithMessage("The given directory <%s> of <%s> does not exist.", directory,
          actual.getBaseDir().getAbsolutePath());
    }
    return myself;
  }

  public AbstractFileAssert<?> withFile(String fileName) {
    isNotNull();
    //FIXME: wrong way...need to reconsider.
    File target = new File(this.actual.getBaseDir(), "target");
    if (!target.isDirectory() || !target.exists() || target.isHidden()) {
      failWithMessage("The target directory of <%s> does not exist.", actual.getBaseDir().getAbsolutePath());
    }
    File fileNameFile = new File(target, fileName);
    return Assertions.assertThat(fileNameFile);
  }

  public ArchiveAssert withEarFile() {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    File target = new File(this.actual.getBaseDir(), "target");
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".ear";
    File earFile = new File(target, artifact);
    if (!earFile.isFile() || !earFile.canRead() || earFile.isHidden()) {
      failWithMessage("The ear file <%s> does not exist or can not be read.", earFile.getAbsolutePath());
    }

    return new ArchiveAssert(earFile, this.actual.getModel(), this.myself);
  }

  public ArchiveAssert withJarFile() {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    File target = new File(this.actual.getBaseDir(), "target");
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".jar";
    File jarFile = new File(target, artifact);
    if (!jarFile.isFile() && !jarFile.canRead()) {
      failWithMessage("The ear file <%s> does not exist or can not be read.", jarFile.getAbsolutePath());
    }
    return new ArchiveAssert(jarFile, this.actual.getModel(), this.myself);
  }

  public ArchiveAssert withWarFile() {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    File target = new File(this.actual.getBaseDir(), "target");
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".war";
    File warFile = new File(target, artifact);
    if (!warFile.isFile() && !warFile.canRead()) {
      failWithMessage("The ear file <%s> does not exist or can not be read.", warFile.getAbsolutePath());
    }
    return new ArchiveAssert(warFile, this.actual.getModel(), this.myself);
  }

  public ArchiveAssert withRarFile() {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    File target = new File(this.actual.getBaseDir(), "target");
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".rar";
    File rarFile = new File(target, artifact);
    if (!rarFile.isFile() && !rarFile.canRead()) {
      failWithMessage("The ear file <%s> does not exist or can not be read.", rarFile.getAbsolutePath());
    }
    return new ArchiveAssert(rarFile, this.actual.getModel(), this.myself);
  }

  public MavenProjectResultAssert contains(List<String> files) {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    File target = new File(this.actual.getBaseDir(), "target");
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".ear";
    File earFile = new File(target, artifact);

    try (JarFile jarFile = new JarFile(earFile)) {
      if (!files.stream()
          .allMatch(fileEntry -> jarFile.stream().anyMatch(jarEntry -> fileEntry.equals(jarEntry.getName())))) {
        failWithMessage("The ear file <%s> does not contain all given elements.", files);
      }
    } catch (IOException e) {
      failWithMessage("IOException happened. <%s> file:<%s>", e.getMessage(), earFile.getAbsolutePath());
    }
    return myself;
  }

  /**
   * A module can have a `target` directory or but in contradiction to a an aggregator project which does not have a
   * `target` directory. So it shouldn't be checked.
   *
   * @param moduleName The name of the module.
   * @return ..
   */
  public MavenProjectResultAssert hasModule(String moduleName) {
    isNotNull();

    File moduleNameFile = new File(this.actual.getBaseDir(), moduleName);

    if (!moduleNameFile.exists() || !moduleNameFile.isHidden() && !moduleNameFile.isDirectory()) {
      failWithMessage("expected having a module <%s> which does not exist", moduleName);
    }
    return myself;
  }

  public MavenProjectResultAssert withModule(String moduleName) {
    isNotNull();

    File moduleNameFile = new File(this.actual.getBaseDir(), moduleName);

    if (!moduleNameFile.exists() || !moduleNameFile.isHidden() && !moduleNameFile.isDirectory()) {
      failWithMessage("expected having a module <%s> which does not exist", moduleName);
    }
    Model model = ProjectHelper.readProject(moduleNameFile);
    MavenProjectResult mavenProjectResult = new MavenProjectResult(moduleNameFile, model);
    return new MavenProjectResultAssert(mavenProjectResult);
  }

}
