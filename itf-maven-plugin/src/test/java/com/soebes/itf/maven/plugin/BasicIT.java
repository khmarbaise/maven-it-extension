package com.soebes.itf.maven.plugin;

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

import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenOption;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.DisplayName;

import java.nio.file.Path;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;
import static com.soebes.itf.jupiter.extension.MavenCLIOptions.DEBUG;
import static com.soebes.itf.jupiter.extension.MavenCLIOptions.NO_TRANSFER_PROGRESS;

@MavenJupiterExtension
class BasicIT {

  @MavenTest
  @MavenOption(NO_TRANSFER_PROGRESS)
  @MavenOption(DEBUG)
  @DisplayName("Running a basic test which makes sure the groupId/artifact of the plugin are ok.")
  void groupid_artifactid_should_be_ok(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

  /**
   * The test will check that the jar of the test project will be installed into the
   * {@code itf-repo} and the appropriate {@code pom.xml} file.
   * <p>
   * The given test project does not have any dependencies nor any code.
   * <p>
   * We are using {@code pre-integration-test} because we want to test only the
   * plugin itself.
   *
   * @param result {@link MavenExecutionResult}
   */
  @MavenTest
  @MavenGoal("pre-integration-test")
  @DisplayName("Running within an execution block in pom file with given goal but no explicit life cycle binding.")
  void install_should_not_fail(MavenExecutionResult result) {
    assertThat(result).isSuccessful().project();

    //FIXME: The following should be made easier See https://github.com/khmarbaise/maven-it-extension/issues/39
    Path target = result.getMavenProjectResult().getTargetProjectDirectory().resolve( "target");
    Path itfRepo = target.resolve( "itf-repo");

    Path install_should_not_fail = itfRepo.resolve( "com/soebes/itf/maven/plugin/its/install_should_not_fail");

    assertThat(install_should_not_fail).isDirectory().satisfies(isnf -> {
      assertThat(isnf.resolve( "maven-metadata-local.xml")).isNotEmptyFile();
      assertThat(isnf.resolve("1.0")).isDirectory().satisfies(v10 -> {
        assertThat(v10).satisfies(file -> {
          assertThat(file.resolve( "install_should_not_fail-1.0.jar")).isNotEmptyFile();
          assertThat(file.resolve( "install_should_not_fail-1.0.pom")).isNotEmptyFile();
          assertThat(file.resolve( "_remote.repositories")).isNotEmptyFile();
        });
      });
    });

  }

  /**
   * The test will check that the jar of the test project and the single dependency
   * will be installed into the {@code itf-repo} and the appropriate {@code pom.xml} file.
   * <p>
   * The given test project has only a single dependency but no code at all.
   * The single dependency is <a href="https://search.maven.org/artifact/org.apiguardian/apiguardian-api/1.1.0/jar">apiguardian-api 1.1.0</a>
   * which does not have a dependency on it own.
   * <p>
   * We are using {@code pre-integration-test} because we want to test only the
   * plugin itself.
   *
   * @param result {@link MavenExecutionResult}
   */
  @MavenTest
  @MavenOption(NO_TRANSFER_PROGRESS)
  @MavenGoal("pre-integration-test")
  @DisplayName("Install with a single dependency.")
  void install_with_a_single_dependency(MavenExecutionResult result) {
    assertThat(result).isSuccessful().project();

    Path target = result.getMavenProjectResult().getTargetProjectDirectory().resolve( "target");
    Path itfRepo = target.resolve( "itf-repo");

    Path install_should_not_fail = itfRepo.resolve( "com/soebes/itf/maven/plugin/its/install_with_a_single_dependency");
    assertThat(install_should_not_fail).isDirectory().satisfies(isnf -> {
      assertThat(isnf.resolve( "maven-metadata-local.xml")).isNotEmptyFile();
      assertThat(isnf.resolve( "1.0")).isDirectory().satisfies(v10 -> {
        assertThat(v10).satisfies(file -> {
          assertThat(file.resolve( "install_with_a_single_dependency-1.0.jar")).isNotEmptyFile();
          assertThat(file.resolve( "install_with_a_single_dependency-1.0.pom")).isNotEmptyFile();
          assertThat(file.resolve( "_remote.repositories")).isNotEmptyFile();
        });
      });
    });

    Path apiguardian = itfRepo.resolve( "org/apiguardian/apiguardian-api");
    assertThat(apiguardian).isDirectory().satisfies(isnf -> {
      assertThat(isnf.resolve( "maven-metadata-local.xml")).isNotEmptyFile();
      assertThat(isnf.resolve( "1.1.0")).isDirectory().satisfies(v10 -> {
        assertThat(v10).satisfies(file -> {
          assertThat(file.resolve( "apiguardian-api-1.1.0.jar")).isNotEmptyFile();
          assertThat(file.resolve( "apiguardian-api-1.1.0.pom")).isNotEmptyFile();
        });
      });
    });
  }

  /**
   * The test will check that the jar of the test project and the single dependency
   * will be installed into the {@code itf-repo} and the appropriate {@code pom.xml} file
   * including their transitive dependencies.
   * <p>
   * The given test project has a single dependency but no code at all.
   * The single dependency is <a href="https://search.maven.org/artifact/junit/junit/4.13.2/jar">junit</a>
   * which has a single dependency <a href="https://search.maven.org/artifact/org.hamcrest/hamcrest-core/1.3/jar">hamcrest-core</a>
   * (scope:compile). Furthermore the hamcrest-core has a
   * <a href="https://search.maven.org/artifact/org.hamcrest/hamcrest-parent/1.3/pom">hamcrest-parent</a>
   * which needs to be installed as well.
   * <p>
   * We are using {@code pre-integration-test} because we want to test only the
   * plugin itself.
   *
   * @param result {@link MavenExecutionResult}
   */
  @MavenTest
  @DisplayName("Install with a single dependency and one transitive dependency")
  @MavenOption(NO_TRANSFER_PROGRESS)
  @MavenGoal("pre-integration-test")
  void install_with_dep_and_transitive_dep(MavenExecutionResult result) {
    assertThat(result).isSuccessful().project();

    Path target = result.getMavenProjectResult().getTargetProjectDirectory().resolve("target");
    Path itfRepo = target.resolve( "itf-repo");

    Path install_should_not_fail = itfRepo.resolve( "com/soebes/itf/maven/plugin/its/install_with_dep_and_transitive_dep");
    assertThat(install_should_not_fail).isDirectory().satisfies(isnf -> {
      assertThat(isnf.resolve( "1.0")).isDirectory().satisfies(v10 -> {
        assertThat(v10).satisfies(file -> {
          assertThat(file.resolve( "install_with_dep_and_transitive_dep-1.0.jar")).isNotEmptyFile();
          assertThat(file.resolve( "install_with_dep_and_transitive_dep-1.0.pom")).isNotEmptyFile();
        });
      });
    });

    Path junit = itfRepo.resolve( "junit/junit");
    assertThat(junit).isDirectory().satisfies(isnf -> {
      assertThat(isnf.resolve( "4.13.2")).isDirectory().satisfies(v4_13 -> {
        assertThat(v4_13).satisfies(file -> {
          assertThat(file.resolve( "junit-4.13.2.jar")).isNotEmptyFile();
          assertThat(file.resolve( "junit-4.13.2.pom")).isNotEmptyFile();
        });
      });
    });

    Path hamcrestCore = itfRepo.resolve( "org/hamcrest/hamcrest-core");
    assertThat(hamcrestCore).isDirectory().satisfies(isnf -> {
      assertThat(isnf.resolve( "1.3")).isDirectory().satisfies(v13 -> {
        assertThat(v13).satisfies(file -> {
          assertThat(file.resolve("hamcrest-core-1.3.jar")).isNotEmptyFile();
          assertThat(file.resolve("hamcrest-core-1.3.pom")).isNotEmptyFile();
        });
      });
    });

    Path hamcrestParent = itfRepo.resolve( "org/hamcrest/hamcrest-parent");
    assertThat(hamcrestParent).isDirectory().satisfies(isnf -> {
      assertThat(isnf.resolve( "1.3")).isDirectory().satisfies(v10 -> {
        assertThat(v10).satisfies(file -> {
          assertThat(file.resolve( "hamcrest-parent-1.3.pom")).isNotEmptyFile();
        });
      });
    });
  }

}
