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
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.junit.jupiter.api.BeforeEach;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;
import static java.util.stream.Collectors.toList;

@MavenJupiterExtension
class ITWithBeforeEachMavenIT {

  @BeforeEach
  //TODO: Enhanced MavenProjectResult with `target`
  //TODO: testMethodProjectFolder should be made part of MavenProjectResult as well! => simplifies the following code
  void beforeEach(MavenProjectResult project) throws IOException {
    //Each time the beforeEach will be executed the extension will delete the project and
    //recreate from scratch. This will be checked in the following statement.
    assertThat(new File(project.getTargetProjectDirectory(), "target")).doesNotExist();

    File testMethodProjectFolder = new File(this.getClass().getResource("/").getFile(), "com/soebes/itf/examples/ITWithBeforeEachMavenIT/the_first_test_case");
    List<String> expectedElements = createElements(testMethodProjectFolder);

    List<String> actualElements = createElements(project.getTargetProjectDirectory());

    assertThat(actualElements).containsExactlyInAnyOrderElementsOf(expectedElements);

  }

  private List<String> createElements(File xdirectory) {
    Collection<File> expectedCollectedFiles = FileUtils.listFilesAndDirs(xdirectory, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

    List<String> expectedElements = expectedCollectedFiles.stream().map(p -> p.toString().replace(xdirectory.getAbsolutePath(), "")).collect(toList());
    return expectedElements;
  }

  //FIXME: @RepeatedTest(value = 2) Does not work currently but it should work!
  @MavenTest
  void the_first_test_case(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

}
