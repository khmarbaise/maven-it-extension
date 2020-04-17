package com.soebes.itf.maven.plugin;

import com.soebes.itf.extension.assertj.MavenITAssertions;
import com.soebes.itf.jupiter.extension.MavenIT;
import com.soebes.itf.jupiter.extension.MavenOptions;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.DisplayName;

import static org.assertj.core.api.Assertions.assertThat;

@MavenIT
class BasicIT {

	@MavenTest(options = {MavenOptions.NO_TRANSFER_PROGRESS})
	@DisplayName("Running a basic test which makes sure the groupId/artifact of the plugin are ok.")
	void groupid_artifactid_should_be_ok(MavenExecutionResult result) {
		MavenITAssertions.assertThat(result).isSuccessful();
	}

	/**
	 * The test will check that the jar of the test project will be installed into the
	 * {@code invoker-repo} and the appropriate {@codoe pom.xml} file.
	 *
	 * The given test project does not have any dependencies nor any code.
	 *
	 * We are using {@code pre-integration-test} cause we want to test only the
	 * plugin itself.
	 *
	 * @param result {@link MavenExecutionResult}
	 */
	@MavenTest(options = {MavenOptions.NO_TRANSFER_PROGRESS}, goals = {"pre-integration-test"})
	@DisplayName("Running within an execution block in pom file with given goal but no explicit life cycle binding.")
	void install_should_not_fail(MavenExecutionResult result) {
		MavenITAssertions.assertThat(result).isSuccessful().project();

		//FIXME: The following should be made easier See https://github.com/khmarbaise/maven-it-extension/issues/39
		File target = new File(result.getMavenProjectResult().getBaseDir(), "target");
		File invokerRepo = new File(target, "invoker-repo");

		File install_should_not_fail = new File(invokerRepo, "com/soebes/itf/maven/plugin/its/install_should_not_fail");

		assertThat(install_should_not_fail).isDirectory().satisfies(isnf -> {
			assertThat(new File(isnf, "maven-metadata-local.xml")).isNotEmpty();
			assertThat(new File(isnf, "1.0")).isDirectory().satisfies(v10 -> {
				assertThat(v10).satisfies(file -> {
					assertThat(new File(file, "install_should_not_fail-1.0.jar")).isNotEmpty();
					assertThat(new File(file, "install_should_not_fail-1.0.pom")).isNotEmpty();
					assertThat(new File(file, "_remote.repositories")).isNotEmpty();
				});
			});
		});

	}

	/**
	 * The test will check that the jar of the test project and the single dependency
	 * will be installed into the {@code invoker-repo} and the appropriate {@codoe pom.xml} file.
	 *
	 * The given test project has only a single dependency but no code at all.
	 * The single dependency is <a href="https://search.maven.org/artifact/org.apiguardian/apiguardian-api/1.1.0/jar">apiguardian-api 1.1.0</a>
	 * which does not has a dependency on it own.
	 *
	 * We are using {@code pre-integration-test} cause we want to test only the
	 * plugin itself.
	 *
	 * @param result {@link MavenExecutionResult}
	 */
	@MavenTest(options = {MavenOptions.NO_TRANSFER_PROGRESS}, goals = {"pre-integration-test"})
	@DisplayName("Install with a single dependency.")
	void install_with_a_single_dependency(MavenExecutionResult result) {
		MavenITAssertions.assertThat(result).isSuccessful().project();

		File target = new File(result.getMavenProjectResult().getBaseDir(), "target");
		File invokerRepo = new File(target, "invoker-repo");

		File install_should_not_fail = new File(invokerRepo, "com/soebes/itf/maven/plugin/its/install_with_a_single_dependency");
		assertThat(install_should_not_fail).isDirectory().satisfies(isnf -> {
			assertThat(new File(isnf, "maven-metadata-local.xml")).isNotEmpty();
			assertThat(new File(isnf, "1.0")).isDirectory().satisfies(v10 -> {
				assertThat(v10).satisfies(file -> {
					assertThat(new File(file, "install_with_a_single_dependency-1.0.jar")).isNotEmpty();
					assertThat(new File(file, "install_with_a_single_dependency-1.0.pom")).isNotEmpty();
					assertThat(new File(file, "_remote.repositories")).isNotEmpty();
				});
			});
		});

		File apiguardian = new File(invokerRepo, "org/apiguardian/apiguardian-api");
		assertThat(apiguardian).isDirectory().satisfies(isnf -> {
			assertThat(new File(isnf, "maven-metadata-local.xml")).isNotEmpty();
			assertThat(new File(isnf, "1.1.0")).isDirectory().satisfies(v10 -> {
				assertThat(v10).satisfies(file -> {
					assertThat(new File(file, "apiguardian-api-1.1.0.jar")).isNotEmpty();
					assertThat(new File(file, "apiguardian-api-1.1.0.pom")).isNotEmpty();
				});
			});
		});
	}

	/**
	 * The test will check that the jar of the test project and the single dependency
	 * will be installed into the {@code invoker-repo} and the appropriate {@codoe pom.xml} file
	 * including their transitive dependencies.
	 *
	 * The given test project has a single dependency but no code at all.
	 * The single dependency is <a href="https://search.maven.org/artifact/junit/junit/4.13/jar">junit</a>
	 * which has a single dependency <a href="https://search.maven.org/artifact/org.hamcrest/hamcrest-core/1.3/jar">hamcrest-core</a>
	 * (scope:compile). Furthermore the hamcrest-core has a
	 * <a href="https://search.maven.org/artifact/org.hamcrest/hamcrest-parent/1.3/pom">hamcrest-parent</a>
	 * which needs to be installed as well.
	 *
	 * We are using {@code pre-integration-test} cause we want to test only the
	 * plugin itself.
	 *
	 * @param result {@link MavenExecutionResult}
	 */
	@MavenTest(options = {MavenOptions.NO_TRANSFER_PROGRESS}, goals = {"pre-integration-test"})
	@DisplayName("Install with a single dependency and one transitive dependency")
	void install_with_dep_and_transitive_dep(MavenExecutionResult result) {
		MavenITAssertions.assertThat(result).isSuccessful().project();

		File target = new File(result.getMavenProjectResult().getBaseDir(), "target");
		File invokerRepo = new File(target, "invoker-repo");

		File install_should_not_fail = new File(invokerRepo, "com/soebes/itf/maven/plugin/its/install_with_dep_and_transitive_dep");
		assertThat(install_should_not_fail).isDirectory().satisfies(isnf -> {
			assertThat(new File(isnf, "1.0")).isDirectory().satisfies(v10 -> {
				assertThat(v10).satisfies(file -> {
					assertThat(new File(file, "install_with_dep_and_transitive_dep-1.0.jar")).isNotEmpty();
					assertThat(new File(file, "install_with_dep_and_transitive_dep-1.0.pom")).isNotEmpty();
				});
			});
		});

		File junit = new File(invokerRepo, "junit/junit");
		assertThat(junit).isDirectory().satisfies(isnf -> {
			assertThat(new File(isnf, "4.13")).isDirectory().satisfies(v4_13 -> {
				assertThat(v4_13).satisfies(file -> {
					assertThat(new File(file, "junit-4.13.jar")).isNotEmpty();
					assertThat(new File(file, "junit-4.13.pom")).isNotEmpty();
				});
			});
		});

		File hamcrestCore = new File(invokerRepo, "org/hamcrest/hamcrest-core");
		assertThat(hamcrestCore).isDirectory().satisfies(isnf -> {
			assertThat(new File(isnf, "1.3")).isDirectory().satisfies(v13 -> {
				assertThat(v13).satisfies(file -> {
					assertThat(new File(file, "hamcrest-core-1.3.jar")).isNotEmpty();
					assertThat(new File(file, "hamcrest-core-1.3.pom")).isNotEmpty();
				});
			});
		});

		File hamcrestParent = new File(invokerRepo, "org/hamcrest/hamcrest-parent");
		assertThat(hamcrestParent).isDirectory().satisfies(isnf -> {
			assertThat(new File(isnf, "1.3")).isDirectory().satisfies(v10 -> {
				assertThat(v10).satisfies(file -> {
					assertThat(new File(file, "hamcrest-parent-1.3.pom")).isNotEmpty();
				});
			});
		});
	}

}
