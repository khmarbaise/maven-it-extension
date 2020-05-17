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
 * @author Karl Heinz Marbaise
 */
class DirectoryResolverResult {

  private final File mavenItTestCaseBaseDirectory;

  private final File mavenBaseDirectory;

  private final File targetDirectory;

  private final File integrationTestCaseDirectory;

  private final File projectDirectory;

  private final File mavenItsBaseDirectory;

  private final File sourceMavenProject;

  private final File cacheDirectory;

  private final File componentUnderTestDirectory;

  private final Optional<File> predefinedRepository;

  private final Optional<File> mrmRepository;

  DirectoryResolverResult(ExtensionContext context) {
    StorageHelper sh = new StorageHelper(context);

    this.mavenItTestCaseBaseDirectory = sh.get(Storage.MAVEN_IT_TESTCASE_BASEDIRECTORY, File.class);
    this.mavenBaseDirectory = sh.get(Storage.MAVEN_IT_BASEDIRECTORY, File.class);
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
    this.mavenItsBaseDirectory = new File(DirectoryHelper.getTargetDir(), "test-classes");
    this.componentUnderTestDirectory = new File(this.getTargetDirectory(), "itf-repo"); // Hard Coded!!

    Class<?> testClass = context.getTestClass().orElseThrow(() -> new IllegalStateException("Test class not found."));
    String toFullyQualifiedPath = DirectoryHelper.toFullyQualifiedPath(testClass);
    if (mavenProject.isPresent()) {
      MavenProject mavenProjectAnnotation = mavenProject.get().getAnnotation(MavenProject.class);
      this.sourceMavenProject = new File(this.mavenItsBaseDirectory, toFullyQualifiedPath + "/" + mavenProjectAnnotation.value());
    } else {
      this.sourceMavenProject = new File(this.mavenItsBaseDirectory, toFullyQualifiedPath + "/" + methodName.getName());
    }

    Optional<Class<?>> optionalMavenRepository = AnnotationHelper.findMavenRepositoryAnnotation(context);
    if (optionalMavenRepository.isPresent()) {
      MavenRepository mavenRepository = optionalMavenRepository.get().getAnnotation(MavenRepository.class);
      String repositoryPath = DirectoryHelper.toFullyQualifiedPath(optionalMavenRepository.get());
      File cacheDirectoryBase = new File(this.mavenBaseDirectory, repositoryPath);
      this.cacheDirectory = new File(cacheDirectoryBase, mavenRepository.value());
    } else {
      //FIXME: Hard coded default. Should we get the default from the Annotation?
      this.cacheDirectory = new File(this.integrationTestCaseDirectory, ".m2/repository");
    }

    Optional<Class<?>> optionalMavenPredefinedRepository = AnnotationHelper.findMavenPredefinedRepositoryAnnotation(context);
    if (optionalMavenPredefinedRepository.isPresent()) {
      MavenPredefinedRepository mavenRepository = optionalMavenPredefinedRepository.get().getAnnotation(MavenPredefinedRepository.class);
      String repositoryPath = DirectoryHelper.toFullyQualifiedPath(optionalMavenPredefinedRepository.get());
      File cacheDirectoryBase = new File(this.mavenItsBaseDirectory, repositoryPath);
      this.predefinedRepository = Optional.of(new File(cacheDirectoryBase, mavenRepository.value()));
    } else {
      //FIXME: Hard coded default. Should we get the default from the Annotation?
      this.predefinedRepository = Optional.empty();
    }
    Optional<Class<?>> optionalMavenMockRepositoryRepository = AnnotationHelper.findMavenMockRepositoryManager(context);
    if (optionalMavenMockRepositoryRepository.isPresent()) {
      MavenMockRepositoryManager mockRepositoryManager = optionalMavenMockRepositoryRepository.get().getAnnotation(MavenMockRepositoryManager.class);
      String repositoryPath = DirectoryHelper.toFullyQualifiedPath(optionalMavenMockRepositoryRepository.get());
      File cacheDirectoryBase = new File(this.mavenItsBaseDirectory, repositoryPath);
      this.mrmRepository = Optional.of(new File(cacheDirectoryBase, mockRepositoryManager.value()));
    } else {
      this.mrmRepository = Optional.empty();
    }

  }

  final File getComponentUnderTestDirectory() {
    return componentUnderTestDirectory;
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

  final File getMavenItsBaseDirectory() {
    return mavenItsBaseDirectory;
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

  final File getMavenBaseDirectory() {
    return mavenBaseDirectory;
  }

  final File getTargetDirectory() {
    return targetDirectory;
  }

  final Optional<File> getMrmRepository() {
    return mrmRepository;
  }

  class DirectoryExtensionContextResolver {

    private File mavenItBaseDirectory;

    private File mavenBaseDirectory;

    private File targetDirectory;

    DirectoryResolverResult resolve(ExtensionContext context) {
      StorageHelper sh = new StorageHelper(context);
      this.mavenItBaseDirectory = sh.get(Storage.MAVEN_IT_TESTCASE_BASEDIRECTORY, File.class);
      this.mavenBaseDirectory = sh.get(Storage.MAVEN_IT_BASEDIRECTORY, File.class);
      this.targetDirectory = sh.get(Storage.TARGET_DIRECTORY, File.class);

      return new DirectoryResolverResult(context);
    }

    public File getMavenItBaseDirectory() {
      return mavenItBaseDirectory;
    }

    public File getMavenBaseDirectory() {
      return mavenBaseDirectory;
    }

    public File getTargetDirectory() {
      return targetDirectory;
    }
  }

}
