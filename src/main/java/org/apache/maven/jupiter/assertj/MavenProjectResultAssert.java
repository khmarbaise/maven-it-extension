package org.apache.maven.jupiter.assertj;

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
import java.util.jar.JarFile;
import org.apache.maven.jupiter.extension.maven.MavenProjectResult;
import org.apache.maven.model.Model;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

/**
 * @author Karl Heinz Marbaise
 */
public class MavenProjectResultAssert extends AbstractAssert<MavenProjectResultAssert, MavenProjectResult> {

  protected MavenProjectResultAssert(MavenProjectResult actual) {
    super(actual, MavenProjectResultAssert.class);
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
    if (!target.isDirectory() && !target.exists()) {
      failWithMessage("The target directory of <%s> does not exist.", actual.getBaseDir().getAbsolutePath());
    }
    return myself;
  }

  public MavenProjectResultAssert has(String directory) {
    isNotNull();
    File target = new File(this.actual.getBaseDir(), directory);
    if (!target.isDirectory() && !target.exists()) {
      failWithMessage("The given directory <%s> of <%s> does not exist.", directory,
          actual.getBaseDir().getAbsolutePath());
    }
    return myself;
  }

  public MavenProjectResultAssert withEarFile() {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    File target = new File(this.actual.getBaseDir(), "target");
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".ear";
    File earFile = new File(target, artifact);
    if (!earFile.isFile() && !earFile.canRead()) {
      failWithMessage("The ear file <%s> does not exist or can not be read.", earFile.getAbsolutePath());
    }
    return myself;
  }

  public MavenProjectResultAssert withJarFile() {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    File target = new File(this.actual.getBaseDir(), "target");
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".jar";
    File earFile = new File(target, artifact);
    if (!earFile.isFile() && !earFile.canRead()) {
      failWithMessage("The ear file <%s> does not exist or can not be read.", earFile.getAbsolutePath());
    }
    return myself;
  }

  public MavenProjectResultAssert withWarFile() {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    File target = new File(this.actual.getBaseDir(), "target");
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".war";
    File earFile = new File(target, artifact);
    if (!earFile.isFile() && !earFile.canRead()) {
      failWithMessage("The ear file <%s> does not exist or can not be read.", earFile.getAbsolutePath());
    }
    return myself;
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

  public MavenProjectResultAssert containsOnlyOnce(String... files) {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    File target = new File(this.actual.getBaseDir(), "target");
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".ear";
    File earFile = new File(target, artifact);

    try (JarFile jarFile = new JarFile(earFile)) {
      Assertions.assertThat(jarFile.stream())
          .describedAs("Checking ear file names.")
          .extracting(jarEntry -> jarEntry.getName())
          .containsOnlyOnce(files);
    } catch (IOException e) {
      failWithMessage("IOException happened. <%s> file:<%s>", e.getMessage(), earFile.getAbsolutePath());
    }
    return myself;
  }

  public MavenProjectResultAssert doesNotContain(String... excludeItems) {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    File target = new File(this.actual.getBaseDir(), "target");
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".ear";
    File earFile = new File(target, artifact);

    try (JarFile jarFile = new JarFile(earFile)) {
      Assertions.assertThat(jarFile.stream()).extracting(jarEntry -> jarEntry.getName()).doesNotContain(excludeItems);
    } catch (IOException e) {
      failWithMessage("IOException happened. <%s> file:<%s>", e.getMessage(), earFile.getAbsolutePath());
    }
    return myself;
  }
}
