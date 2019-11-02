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
import org.apache.maven.jupiter.extension.maven.MavenProjectResult;
import org.apache.maven.model.Model;
import org.assertj.core.api.AbstractAssert;

public class MavenModuleResultAssert extends AbstractAssert<MavenModuleResultAssert, MavenProjectResult> {

  protected MavenModuleResultAssert(MavenProjectResult actual) {
    super(actual, MavenModuleResultAssert.class);
  }

  public MavenModuleResultAssert hasTarget() {
    isNotNull();
    File target = new File(this.actual.getBaseDir(), "target");

    if (!(target.exists() && target.isDirectory() && !target.isHidden())) {
      failWithMessage("The target directory of <%s> does not exist.", actual.getBaseDir().getAbsolutePath());
    }
    return myself;
  }

  public ArchiveAssert withEarFile() {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    File target = new File(this.actual.getBaseDir(), "target");
    String artifact = model.getArtifactId() + "-" + model.getVersion() + ".ear";
    File earFile = new File(target, artifact);
    if (!earFile.isFile() && !earFile.canRead() && !earFile.isHidden()) {
      failWithMessage("The ear file <%s> does not exist or can not be read.", earFile.getAbsolutePath());
    }

    return new ArchiveAssert(earFile, this.actual.getModel(), this.myself);
  }

  public ArchiveAssert withWarFile() {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    File target = new File(this.actual.getBaseDir(), "target");
    String warArchive = model.getArtifactId() + "-" + model.getVersion() + ".war";
    File warFile = new File(target, warArchive);
    if (!warFile.isFile() && !warFile.canRead() && !warFile.isHidden()) {
      failWithMessage("The war file <%s> does not exist or can not be read.", warFile.getAbsolutePath());
    }

    return new ArchiveAssert(warFile, this.actual.getModel(), this.myself);
  }
  public ArchiveAssert withJarFile() {
    isNotNull();
    hasTarget();

    Model model = this.actual.getModel();
    File target = new File(this.actual.getBaseDir(), "target");
    String jarArchive = model.getArtifactId() + "-" + model.getVersion() + ".jar";
    File jarFile = new File(target, jarArchive);
    if (!jarFile.isFile() && !jarFile.canRead() && !jarFile.isHidden()) {
      failWithMessage("The jar file <%s> does not exist or can not be read.", jarFile.getAbsolutePath());
    }

    return new ArchiveAssert(jarFile, this.actual.getModel(), this.myself);
  }

}
