package com.soebes.itf.examples;

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

import com.soebes.itf.extension.assertj.MavenExecutionResultAssert;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenProjectResult;

import java.nio.file.Paths;

import static org.assertj.core.api.Assertions.assertThat;

@MavenJupiterExtension
class MavenProjectResultIT {

  @MavenTest
  void basic(MavenExecutionResult result) {
    MavenExecutionResultAssert.assertThat(result).isSuccessful();

    MavenProjectResult mpr = result.getMavenProjectResult();
    assertThat(mpr.getTargetBaseDirectory()).endsWith(Paths.get("basic"));
    assertThat(mpr.getTargetProjectDirectory()).endsWith(Paths.get("basic", "project"));
    assertThat(mpr.getTargetCacheDirectory()).endsWith(Paths.get("basic", ".m2", "repository"));
  }


}
