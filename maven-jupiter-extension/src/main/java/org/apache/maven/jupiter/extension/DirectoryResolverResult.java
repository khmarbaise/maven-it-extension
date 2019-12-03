package org.apache.maven.jupiter.extension;

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

import java.io.File;
import java.lang.reflect.Method;
import java.util.Optional;
import org.apache.maven.jupiter.extension.utils.DirectoryHelper;
import org.junit.jupiter.api.extension.ExtensionContext;

class DirectoryResolverResult {

  private final File mavenItBaseDirectory;

  private final File mavenBaseDirectory;

  private final File targetDirectory;

  private final File integrationTestCaseDirectory;

  private final File projectDirectory;

  private final File mavenItsBaseDirectory;

  private final File sourceMavenProject;

  private final File cacheDirectory;

  private final File componentUnderTestDirectory;

  DirectoryResolverResult(ExtensionContext context) {
    StorageHelper sh = new StorageHelper(context);

    this.mavenItBaseDirectory = sh.get(Storage.BASE_IT_DIRECTORY, File.class);
    this.mavenBaseDirectory = sh.get(Storage.BASE_DIRECTORY, File.class);
    this.targetDirectory = sh.get(Storage.TARGET_DIRECTORY, File.class);

    Method methodName = context.getTestMethod().orElseThrow(() -> new IllegalStateException("No method given"));
    this.integrationTestCaseDirectory = new File(this.mavenItBaseDirectory, methodName.getName());

    this.projectDirectory = new File(integrationTestCaseDirectory, "project");
    this.mavenItsBaseDirectory = new File(DirectoryHelper.getTargetDir(), "test-classes");
    this.componentUnderTestDirectory = new File(this.getTargetDirectory(), "invoker-repo"); // Hard Coded!!

    Class<?> testClass = context.getTestClass().orElseThrow(() -> new IllegalStateException("Test class not found."));
    String toFullyQualifiedPath = DirectoryHelper.toFullyQualifiedPath(testClass);
    this.sourceMavenProject = new File(this.mavenItsBaseDirectory, toFullyQualifiedPath + "/" + methodName.getName());

    Optional<Class<?>> optionalMavenRepository = AnnotationHelper.findMavenRepositoryAnnotation(context);
    if (optionalMavenRepository.isPresent()) {
      MavenRepository mavenRepository = optionalMavenRepository.get().getAnnotation(MavenRepository.class);
      String repositoryPath = DirectoryHelper.toFullyQualifiedPath(optionalMavenRepository.get());
      File cacheDirectoryBase = new File(this.mavenBaseDirectory, repositoryPath);
      this.cacheDirectory = new File(cacheDirectoryBase, mavenRepository.value());
    } else {
      //Hard coded default. Should we get the default from the Annotation?
      this.cacheDirectory = new File(this.integrationTestCaseDirectory, ".m2/repository");
    }

  }

  File getComponentUnderTestDirectory() {
    return componentUnderTestDirectory;
  }

  final File getCacheDirectory() {
    return cacheDirectory;
  }

  File getSourceMavenProject() {
    return sourceMavenProject;
  }

  File getMavenItsBaseDirectory() {
    return mavenItsBaseDirectory;
  }

  File getProjectDirectory() {
    return projectDirectory;
  }

  File getIntegrationTestCaseDirectory() {
    return integrationTestCaseDirectory;
  }

  File getMavenItBaseDirectory() {
    return mavenItBaseDirectory;
  }

  File getMavenBaseDirectory() {
    return mavenBaseDirectory;
  }

  File getTargetDirectory() {
    return targetDirectory;
  }

  class DirectoryResolver {

    DirectoryResolverResult resolve() {
      return new DirectoryResolverResult(null);
    }
  }

  class DirectoryExtensionContextResolver {

    private File mavenItBaseDirectory;

    private File mavenBaseDirectory;

    private File targetDirectory;

    DirectoryResolverResult resolve(ExtensionContext context) {
      StorageHelper sh = new StorageHelper(context);
      this.mavenItBaseDirectory = sh.get(Storage.BASE_IT_DIRECTORY, File.class);
      this.mavenBaseDirectory = sh.get(Storage.BASE_DIRECTORY, File.class);
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
