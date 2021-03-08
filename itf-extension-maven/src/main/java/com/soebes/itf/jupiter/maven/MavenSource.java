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

import org.apiguardian.api.API;

import java.nio.file.Path;
import java.util.Optional;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * This class will represent the source directory structure information:
 * <pre>
 * src/test/java/../
 *    +--- FirstIT.java
 *           +--- test_case_one      <--- baseDirectory
 * src/test/resources-its/.../
 *    +--- FirstIT/
 *            +--- .predefined-repo  <--- repository (optional)
 *            +--- test_case_one     <--- projectDirectory
 *                    +--- src/...
 *                    +--- pom.xml
 * </pre>
 *
 * @author Karl Heinz Marbaise
 */
@API(status = EXPERIMENTAL, since = "0.10.0")
public final class MavenSource {

  private final Path baseDirectory;
  private final Path cacheDirectory;
  private final Path projectDirectory;
  private final Optional<Path> repository;

  public MavenSource(Path baseDirectory, Path cacheDirectory, Path projectDirectory, Optional<Path> repository) {
    this.baseDirectory = baseDirectory;
    this.cacheDirectory = cacheDirectory;
    this.projectDirectory = projectDirectory;
    this.repository = repository;
  }

  public Path baseDirectory() {
    return baseDirectory;
  }

  public Path cacheDirectory() {
    return cacheDirectory;
  }

  public Path projectDirectory() {
    return projectDirectory;
  }

  public Optional<Path> repository() {
    return repository;
  }
}
