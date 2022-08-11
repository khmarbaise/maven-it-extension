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
import java.nio.file.Path;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * @implNote The return types of the methods: {@link #getTargetProjectDirectory()}, {@link #getTargetBaseDirectory()}
 * and {@link #getTargetCacheDirectory()} has been changed from {@link File} into {@link Path} in release 0.12.0.
 *
 * @author Karl Heinz Marbaise
 */
@API(status = EXPERIMENTAL, since = "0.12.0")
public class MavenProjectResult {

  private final Model model;
  /**
   * This is the {@code project} directory of the test case.
   */
  private final Path targetProjectDirectory;

  /**
   * This is the {@code base} directory of the test case.
   */
  private final Path targetBaseDirectory;

  /**
   * This is the {@code cache} directory of the test case.
   */
  private final Path targetCacheDirectory;

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
  public MavenProjectResult(Path targetBaseDirectory, Path targetProjectDirectory, Path targetCacheDirectory, Model model) {
    this.targetBaseDirectory = targetBaseDirectory;
    this.targetProjectDirectory = targetProjectDirectory;
    this.targetCacheDirectory = targetCacheDirectory;
    this.model = model;
  }

  /**
   * @since 0.12.0 The return type has been changed from {@link File} into {@link Path}
   * @return The base directory of the test case within {@code target} directory.
   */
  @API(status = EXPERIMENTAL, since = "0.12.0")
  public Path getTargetBaseDirectory() {
    return targetBaseDirectory;
  }

  /**
   * @since 0.12.0 The return type has been changed from {@link File} into {@link Path}
   * @return The {@code project} directory of the test case.
   */
  @API(status = EXPERIMENTAL, since = "0.12.0")
  public Path getTargetProjectDirectory() {
    return targetProjectDirectory;
  }

  /**
   * @since 0.12.0 The return type has been changed from {@link File} into {@link Path}
   * @return The {@code cache} directory of the test case.
   */
  @API(status = EXPERIMENTAL, since = "0.10.0")
  public Path getTargetCacheDirectory() {
    return targetCacheDirectory;
  }

  public Model getModel() {
    return model;
  }
}
