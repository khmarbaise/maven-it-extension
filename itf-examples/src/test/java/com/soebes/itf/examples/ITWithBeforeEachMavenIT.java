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

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenProjectResult;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

@MavenJupiterExtension
class ITWithBeforeEachMavenIT {

  @BeforeEach
    //TODO: Enhanced MavenProjectResult with `target`
    //TODO: testMethodProjectFolder should be made part of MavenProjectResult as well! => simplifies the following code
  void beforeEach(MavenProjectResult project) throws IOException {
    //Each time the beforeEach will be executed the extension will delete the project and
    //recreate from scratch. This will be checked in the following statement.
    assertThat(project.getTargetProjectDirectory().resolve("target")).doesNotExist();

    String resource = this.getClass().getResource("/").getFile();
    Path testMethodProjectFolder = Paths.get(resource, "com/soebes/itf/examples/ITWithBeforeEachMavenIT/the_first_test_case");

    List<Path> expectedElements = readDirectoryElements(testMethodProjectFolder);

    List<Path> expectedResult = expectedElements.stream().map(testMethodProjectFolder::relativize).collect(Collectors.toList());

    List<Path> actualElements = readDirectoryElements(project.getTargetProjectDirectory());

    List<Path> actualResult = actualElements.stream().map(s -> project.getTargetProjectDirectory().relativize(s)).collect(Collectors.toList());

    assertThat(actualResult).containsExactlyInAnyOrderElementsOf(expectedResult);

  }

  private List<Path> readDirectoryElements(Path xdirectory) throws IOException {
    try (Stream<Path> walk = Files.walk(xdirectory)) {
      return walk.filter(s -> Files.isRegularFile(s) || Files.isDirectory(s))
          .collect(Collectors.toList());
    }
  }

  //FIXME: @RepeatedTest(value = 2) Does not work currently but it should work!
  @MavenTest
  void the_first_test_case(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

}
