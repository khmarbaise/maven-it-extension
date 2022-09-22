package com.soebes.itf.examples.mps;

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

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenProjectSources;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

/**
 * Example integration test to demonstrate the usage
 * of {@code @MavenProjectSources(sources = "com/soebes/itf/examples/mps/xyz")}.
 * The given project is defined in the given directory instead of being
 * extracted from the directory based on the package name of the current
 * integration test class and its method name.
 *
 * @author Karl Heinz Marbaise
 */
@MavenJupiterExtension
@MavenProjectSources(sources = "com/soebes/itf/examples/mps/xyz")
class MavenProjectSourcesLocationOneIT {

  @MavenTest
  void project_001(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

  @MavenTest
  void project_002(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

}
