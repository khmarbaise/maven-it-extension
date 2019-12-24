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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.JarFile;
import org.apache.maven.model.Model;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.Assertions;

public class ArchiveAssert extends AbstractAssert<ArchiveAssert, File> {

  private final Model model;

  private final List<String> includes;

  private final MavenProjectResultAssert parent;

  ArchiveAssert(File earFile, Model model, MavenProjectResultAssert parent) {
    super(earFile, ArchiveAssert.class);
    this.model = model;
    this.includes = new ArrayList<>();
    this.parent = parent;
    //TODO: currently ignore maven files and MANIFEST.MF
    ignoreMavenFiles();
    ignoreManifest();
  }

  /**
   * This will ignore the files within an archive
   * <ul>
   *   <li>META-INF/maven/&lt;groupId&gt;/&lt;artifactId&gt;/pom.xml</li>
   *   <li>META-INF/maven/&lt;groupId&gt;/&lt;artifactId&gt;/pom.properties</li>
   * </ul>
   *
   *
   * @return {@link ArchiveAssert}
   */
  public ArchiveAssert ignoreMavenFiles() {
    this.includes.addAll(
        Arrays.asList("META-INF/maven/" + this.model.getGroupId() + "/" + this.model.getArtifactId() + "pom.xml",
            "META-INF/maven/" + this.model.getGroupId() + "/" + this.model.getArtifactId() + "pom.properties"));
    return myself;
  }

  public ArchiveAssert ignoreManifest() {
    this.includes.addAll(Arrays.asList("META-INF/MANIFEST.MF"));
    return myself;
  }

  public ArchiveAssert doesNotContain(String... files) {
    try (JarFile jarFile = new JarFile(this.actual)) {
      List<String> includes = Arrays.asList(files);
      Assertions.assertThat(jarFile.stream())
          .describedAs("Checking ear file names.")
          .extracting(jarEntry -> jarEntry.getName())
          .doesNotContain(includes.toArray(new String[] {}));
    } catch (IOException e) {
      failWithMessage("IOException happened. <%s> file:<%s>", e.getMessage());
    }
    return myself;
  }

  public ArchiveAssert containsOnlyOnce(List<String> files) {
    return containsOnlyOnce(files.toArray(new String[] {}));
  }

  public ArchiveAssert containsOnlyOnce(String... files) {
    try (JarFile jarFile = new JarFile(this.actual)) {
      Assertions.assertThat(jarFile.stream())
          .describedAs("Checking ear file names.")
          .extracting(jarEntry -> jarEntry.getName())
          .containsOnlyOnce(files);
    } catch (IOException e) {
      failWithMessage("IOException happened. <%s> file:<%s>", e.getMessage());
    }
    return myself;
  }
  public ArchiveAssert containsOnly(String... files) {
    try (JarFile jarFile = new JarFile(this.actual)) {
      Assertions.assertThat(jarFile.stream())
          .describedAs("Checking ear file names.")
          .extracting(jarEntry -> jarEntry.getName())
          .containsOnly(files);
    } catch (IOException e) {
      failWithMessage("IOException happened. <%s> file:<%s>", e.getMessage());
    }
    return myself;
  }

  public MavenProjectResultAssert and() {
    return this.parent;
  }
}
