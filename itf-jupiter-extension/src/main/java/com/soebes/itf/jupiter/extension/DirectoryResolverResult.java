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

import java.io.File;
import java.lang.reflect.Method;
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

  private final File mavenItTestCaseBaseDirectory;

  private final File targetMavenItDirectory;

  private final File targetDirectory;

  private final File integrationTestCaseDirectory;

  private final File projectDirectory;

  private final File targetTestClassesDirectory;

  private final File sourceMavenProject;

  private final File cacheDirectory;

  private final File targetItfRepoDirectory;

  private final Optional<File> predefinedRepository;

  DirectoryResolverResult(ExtensionContext context) {
    StorageHelper sh = new StorageHelper(context);

    this.mavenItTestCaseBaseDirectory = sh.get(Storage.MAVEN_IT_TESTCASE_BASEDIRECTORY, File.class);
    this.targetMavenItDirectory = sh.get(Storage.TARGET_MAVEN_IT_DIRECTORY, File.class);
    this.targetDirectory = sh.get(Storage.TARGET_DIRECTORY, File.class);

    Method methodName = context.getTestMethod().orElseThrow(() -> new IllegalStateException("No method given"));

    Optional<Class<?>> mavenProject = AnnotationHelper.findMavenProjectAnnotation(context);
    if (mavenProject.isPresent()) {
      MavenProject mavenProjectAnnotation = mavenProject.get().getAnnotation(MavenProject.class);
      this.integrationTestCaseDirectory = new File(this.getMavenItTestCaseBaseDirectory(),
          mavenProjectAnnotation.value());
    } else {
      this.integrationTestCaseDirectory = new File(this.getMavenItTestCaseBaseDirectory(), methodName.getName());
    }

    this.projectDirectory = new File(integrationTestCaseDirectory, "project");
    this.targetTestClassesDirectory = new File(DirectoryHelper.getTargetDir(), "test-classes");
    this.targetItfRepoDirectory = new File(this.getTargetDirectory(), "itf-repo"); // Hard Coded!!

    Class<?> testClass = context.getTestClass().orElseThrow(() -> new IllegalStateException("Test class not found."));
    String toFullyQualifiedPath = DirectoryHelper.toFullyQualifiedPath(testClass);


    File intermediate = new File(this.targetTestClassesDirectory, toFullyQualifiedPath);
    if (mavenProject.isPresent()) {
      MavenProject mavenProjectAnnotation = mavenProject.get().getAnnotation(MavenProject.class);
      this.sourceMavenProject = new File(intermediate, mavenProjectAnnotation.value());
    } else {
      this.sourceMavenProject = new File(intermediate, methodName.getName());
    }

    Optional<Class<?>> optionalMavenRepository = AnnotationHelper.findMavenRepositoryAnnotation(context);
    if (optionalMavenRepository.isPresent()) {
      MavenRepository mavenRepository = optionalMavenRepository.get().getAnnotation(MavenRepository.class);
      String repositoryPath = DirectoryHelper.toFullyQualifiedPath(optionalMavenRepository.get());
      File cacheDirectoryBase = new File(this.targetMavenItDirectory, repositoryPath);
      this.cacheDirectory = new File(cacheDirectoryBase, mavenRepository.value());
    } else {
      //FIXME: Hard coded default. Should we get the default from the Annotation?
      this.cacheDirectory = new File(this.integrationTestCaseDirectory, ".m2/repository");
    }

    Optional<Class<?>> optionalMavenPredefinedRepository = AnnotationHelper.findMavenPredefinedRepositoryAnnotation(context);
    if (optionalMavenPredefinedRepository.isPresent()) {
      MavenPredefinedRepository mavenRepository = optionalMavenPredefinedRepository.get().getAnnotation(MavenPredefinedRepository.class);
      String repositoryPath = DirectoryHelper.toFullyQualifiedPath(optionalMavenPredefinedRepository.get());
      File cacheDirectoryBase = new File(this.targetTestClassesDirectory, repositoryPath);
      this.predefinedRepository = Optional.of(new File(cacheDirectoryBase, mavenRepository.value()));
    } else {
      //FIXME: Hard coded default. Should we get the default from the Annotation?
      this.predefinedRepository = Optional.empty();
    }

  }

  final File getTargetItfRepoDirectory() {
    return targetItfRepoDirectory;
  }

  final Optional<File> getPredefinedRepository() {
    return predefinedRepository;
  }

  final File getCacheDirectory() {
    return cacheDirectory;
  }

  final File getSourceMavenProject() {
    return sourceMavenProject;
  }

  final File getProjectDirectory() {
    return projectDirectory;
  }

  final File getIntegrationTestCaseDirectory() {
    return integrationTestCaseDirectory;
  }

  final File getMavenItTestCaseBaseDirectory() {
    return mavenItTestCaseBaseDirectory;
  }

  final File getTargetDirectory() {
    return targetDirectory;
  }

}
