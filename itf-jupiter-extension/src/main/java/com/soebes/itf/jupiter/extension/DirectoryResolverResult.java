package com.soebes.itf.jupiter.extension;

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

import org.junit.jupiter.api.extension.ExtensionContext;

import java.lang.reflect.Method;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

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
 *   target/                                   <-- targetDirectory
 *      +-- itf-repo/                          <-- targetItfRepoDirectory
 *      +-- test-classes/                      <-- targetTestClassesDirectory
 *            +--- FirstIT/
 *                    +--- test_case_one       <-- sourceMavenProject
 *
 *      +-- maven-it/                          <-- targetMavenItDirectory
 *           +-- FirstIT/                      <-- mavenItTestCaseBaseDirectory
 *                 +--- test_case_one/         <-- integrationTestCaseDirectory
 *                       +--- .m2/repository   <-- cacheDirectory
 *                       +--- project          <-- projectDirectory
 *                               +--- src/
 *                               +--- pom.xml
 * </pre>
 *
 * @author Karl Heinz Marbaise
 */
class DirectoryResolverResult {

  private final Path mavenItTestCaseBaseDirectory;

  private final Path targetMavenItDirectory;

  private final Path targetDirectory;

  private final Path integrationTestCaseDirectory;

  private final Path projectDirectory;

  private final Path targetTestClassesDirectory;

  private final Path sourceMavenProject;

  private final Path cacheDirectory;

  private final Path targetItfRepoDirectory;

  private final Optional<Path> predefinedRepository;

  DirectoryResolverResult(ExtensionContext context) {
    StorageHelper sh = new StorageHelper(context);

    this.mavenItTestCaseBaseDirectory = sh.get(Storage.MAVEN_IT_TESTCASE_BASEDIRECTORY, Path.class);
    this.targetMavenItDirectory = sh.get(Storage.TARGET_MAVEN_IT_DIRECTORY, Path.class);
    this.targetDirectory = sh.get(Storage.TARGET_DIRECTORY, Path.class);

    Method methodName = context.getTestMethod().orElseThrow(() -> new IllegalStateException("No method given"));

    Optional<Class<?>> mavenProject = AnnotationHelper.findMavenProjectAnnotation(context);
    if (mavenProject.isPresent()) {
      MavenProject mavenProjectAnnotation = mavenProject.get().getAnnotation(MavenProject.class);
      this.integrationTestCaseDirectory = this.getMavenItTestCaseBaseDirectory().resolve( mavenProjectAnnotation.value());
    } else {
      this.integrationTestCaseDirectory = this.getMavenItTestCaseBaseDirectory().resolve( methodName.getName() );
    }

    this.projectDirectory = integrationTestCaseDirectory.resolve("project");
    this.targetTestClassesDirectory = DirectoryHelper.getTargetDir().resolve("test-classes");
    this.targetItfRepoDirectory = this.getTargetDirectory().resolve("itf-repo"); // Hard Coded!!

    Class<?> testClass = context.getTestClass().orElseThrow(() -> new IllegalStateException("Test class not found."));
    String toFullyQualifiedPath = DirectoryHelper.toFullyQualifiedPath(testClass);

    Path intermediate = this.targetTestClassesDirectory.resolve(toFullyQualifiedPath);
    if (mavenProject.isPresent()) {
      MavenProject mavenProjectAnnotation = mavenProject.get().getAnnotation(MavenProject.class);
      this.sourceMavenProject = intermediate.resolve(mavenProjectAnnotation.value());
    } else {
      this.sourceMavenProject = AnnotationHelper.findMavenProjectSourcesAnnotation(context)
          .map(s -> targetTestClassesDirectory.resolve(s.sources()))
          .orElse(intermediate.resolve(methodName.getName()));
    }

    Optional<Class<?>> optionalMavenRepository = AnnotationHelper.findMavenRepositoryAnnotation(context);
    if (optionalMavenRepository.isPresent()) {
      MavenRepository mavenRepository = optionalMavenRepository.get().getAnnotation(MavenRepository.class);
      String repositoryPath = DirectoryHelper.toFullyQualifiedPath(optionalMavenRepository.get());
      Path cacheDirectoryBase = this.targetMavenItDirectory.resolve(repositoryPath);
      this.cacheDirectory = cacheDirectoryBase.resolve(mavenRepository.value());
    } else {
      //FIXME: Hard coded default. Should we get the default from the Annotation?
      this.cacheDirectory = this.integrationTestCaseDirectory.resolve(".m2/repository");
    }

    Optional<Class<?>> optionalMavenPredefinedRepository = AnnotationHelper.findMavenPredefinedRepositoryAnnotation(context);
    if (optionalMavenPredefinedRepository.isPresent()) {
      MavenPredefinedRepository mavenRepository = optionalMavenPredefinedRepository.get().getAnnotation(MavenPredefinedRepository.class);
      String repositoryPath = DirectoryHelper.toFullyQualifiedPath(optionalMavenPredefinedRepository.get());
      Path cacheDirectoryBase = this.targetTestClassesDirectory.resolve(repositoryPath);
      this.predefinedRepository = Optional.of(cacheDirectoryBase.resolve(mavenRepository.value()));
    } else {
      //FIXME: Hard coded default. Should we get the default from the Annotation?
      this.predefinedRepository = Optional.empty();
    }

  }

  Path getTargetItfRepoDirectory() {
    return targetItfRepoDirectory;
  }

  Optional<Path> getPredefinedRepository() {
    return predefinedRepository;
  }

  Path getCacheDirectory() {
    return cacheDirectory;
  }

  Path getSourceMavenProject() {
    return sourceMavenProject;
  }

  Path getProjectDirectory() {
    return projectDirectory;
  }

  Path getIntegrationTestCaseDirectory() {
    return integrationTestCaseDirectory;
  }

  Path getMavenItTestCaseBaseDirectory() {
    return mavenItTestCaseBaseDirectory;
  }

  Path getTargetDirectory() {
    return targetDirectory;
  }

}
