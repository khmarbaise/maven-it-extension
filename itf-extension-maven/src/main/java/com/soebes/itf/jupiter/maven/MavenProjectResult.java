package com.soebes.itf.jupiter.maven;

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

import org.apache.maven.model.Model;
import org.apiguardian.api.API;

import java.io.File;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * @author Karl Heinz Marbaise
 */
@API(status = EXPERIMENTAL, since = "0.1.0")
public class MavenProjectResult {

  @Deprecated
  private final File baseDir;

  private final Model model;
  /**
   * This is the {@code project} directory of the test case.
   */
  @API(status = EXPERIMENTAL, since = "0.10.0")
  private final File targetProjectDirectory;

  /**
   * This is the {@code base} directory of the test case.
   */
  @API(status = EXPERIMENTAL, since = "0.10.0")
  private final File targetBaseDirectory;

  /**
   * This is the {@code cache} directory of the test case.
   */
  @API(status = EXPERIMENTAL, since = "0.10.0")
  private final File targetCacheDirectory;

  /**
   * The source structure looks usually like this:
   * <pre>
   *   src/test/java/../
   *      +--- FirstIT.java
   *             +--- test_case_one
   *   src/test/resources-its/.../
   *      +--- FirstIT/
   *              +--- test_case_one
   *                      +--- src/...
   *                      +--- pom.xml
   * </pre>
   * during the execution of the integration tests the following
   * will be generated:
   * <pre>
   *   target/maven-it/
   *      .../FirstIT
   *           +--- test_case_one            <-- targetBaseDirectory
   *                   +--- .m2/             <-- targetCacheDirectory
   *                   +--- project          <-- targetProjectDirectory
   *                           +--- src/
   *                           +--- pom.xml  <-- model
   *                   +--- mvn-stdout.log
   *                   +--- mvn-stderr.log
   * </pre>
   *
   * @param targetBaseDirectory This represents the root directory of test case in {@code target} directory.
   * @param targetProjectDirectory This represents the root directory in the {@code target} structure.
   * @param targetCacheDirectory This represents the root directory in the {@code cache} structure.
   * @param model The model read from {@code pom.xml} file in the {@code target} structure.
   * @implNote Currently we only read the single {@code pom.xml} file. An existing multi module
   * structure with it's {@code pom.xml} files will not being read.
   */
  public MavenProjectResult(File targetBaseDirectory, File targetProjectDirectory, File targetCacheDirectory, Model model) {
    this.targetBaseDirectory = targetBaseDirectory;
    this.targetProjectDirectory = targetProjectDirectory;
    this.targetCacheDirectory = targetCacheDirectory;
    this.model = model;
    //The following assignments only exist to initialize the deprecated marked field. Will be removed
    //with Release 0.11.0
    this.baseDir = targetBaseDirectory;
  }

  /**
   * @return The base directory of the test case within {@code target} directory.
   */
  @API(status = EXPERIMENTAL, since = "0.10.0")
  public File getTargetBaseDirectory() {
    return targetBaseDirectory;
  }

  /**
   * @return The {@code project} directory of the test case.
   */
  @API(status = EXPERIMENTAL, since = "0.10.0")
  public File getTargetProjectDirectory() {
    return targetProjectDirectory;
  }

  /**
   * @return The {@code cache} directory of the test case.
   */
  @API(status = EXPERIMENTAL, since = "0.10.0")
  public File getTargetCacheDirectory() {
    return targetCacheDirectory;
  }

  @Deprecated
  public File getBaseDir() {
    return baseDir;
  }

  public Model getModel() {
    return model;
  }
}
