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
import java.util.Optional;
import org.apache.maven.jupiter.extension.maven.MavenProjectResult;
import org.apache.maven.jupiter.extension.maven.ProjectHelper;
import org.assertj.core.api.AbstractAssert;

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
    if (!target.isDirectory() || !target.exists() || !target.isHidden()) {
      failWithMessage("The target directory of <%s> does not exist.", actual.getBaseDir().getAbsolutePath());
    }
    return myself;
  }

  public MavenProjectResultAssert has(String directory) {
    isNotNull();
    File target = new File(this.actual.getBaseDir(), directory);
    if (!target.isDirectory() && !target.exists() && !target.isHidden()) {
      failWithMessage("The given directory <%s> of <%s> does not exist.", directory,
          actual.getBaseDir().getAbsolutePath());
    }
    return myself;
  }

  /**
   * @param moduleName The name of the module.
   * @return ..
   */
  public MavenModuleResultAssert hasModule(String moduleName) {
    isNotNull();

    File moduleNameFile = new File(this.actual.getBaseDir(), moduleName);

    if (!moduleNameFile.exists() || !moduleNameFile.isHidden() && !moduleNameFile.isDirectory()) {
      failWithMessage("expected having a module <%s> which does not exist", moduleName);
    }

    MavenProjectResult mavenProjectResult = new MavenProjectResult(moduleNameFile,
        ProjectHelper.readProject(moduleNameFile));
    return new MavenModuleResultAssert(mavenProjectResult);
  }

}
