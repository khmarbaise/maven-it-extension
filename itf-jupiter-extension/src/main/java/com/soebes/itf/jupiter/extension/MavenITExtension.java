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

import com.soebes.itf.jupiter.maven.MavenCacheResult;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenExecutionResult.ExecutionResult;
import com.soebes.itf.jupiter.maven.MavenLog;
import com.soebes.itf.jupiter.maven.MavenProjectResult;
import com.soebes.itf.jupiter.maven.ProjectHelper;
import org.apache.maven.model.Model;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.io.IOException;
import java.lang.reflect.Method;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.soebes.itf.jupiter.extension.AnnotationHelper.findMavenProjectLocationAnnotation;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.goals;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.hasGoals;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.hasOptions;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.hasProfiles;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.hasSystemProperties;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.options;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.profiles;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.systemProperties;
import static com.soebes.itf.jupiter.extension.MavenProjectSources.ResourceUsage.*;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * @author Karl Heinz Marbaise
 */
class MavenITExtension implements BeforeEachCallback, ParameterResolver, BeforeTestExecutionCallback,
    InvocationInterceptor {

  /**
   * The command line options which are given is no annotation at all is defined.
   */
  private static final List<String> DEFAULT_COMMAND_LINE_OPTIONS = Arrays.asList(MavenCLIOptions.BATCH_MODE,
      MavenCLIOptions.SHOW_VERSION, MavenCLIOptions.ERRORS);

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    Class<?> testClass = context.getTestClass()
        .orElseThrow(() -> new ExtensionConfigurationException("MavenITExtension is only supported for classes."));

    boolean resourcesIts = AnnotationHelper.findMavenProjectSourcesAnnotation(context)
        .map(s -> s.getAnnotation(MavenProjectSources.class).resourcesUsage().equals(DEFAULT))
        .orElse(false);


    //FIXME: Need to reconsider the maven-it directory?
    Path targetTestClassesDirectory = DirectoryHelper.getTargetDir().resolve("maven-it");
    String toFullyQualifiedPath = DirectoryHelper.toFullyQualifiedPath(testClass);

    Path mavenItTestCaseBaseDirectory = targetTestClassesDirectory.resolve(toFullyQualifiedPath);
    //TODO: What happens if the directory has been created by a previous run?
    // should we delete that structure here? Maybe we should make this configurable.
    Files.createDirectories(mavenItTestCaseBaseDirectory);

    StorageHelper storageHelper = new StorageHelper(context);
    storageHelper.save(targetTestClassesDirectory, mavenItTestCaseBaseDirectory, DirectoryHelper.getTargetDir());

    Optional<Class<?>> mavenProject = AnnotationHelper.findMavenProjectAnnotation(context);

    DirectoryResolverResult directoryResolverResult = new DirectoryResolverResult(context);
    Path integrationTestCaseDirectory = directoryResolverResult.getIntegrationTestCaseDirectory();

    // FIXME: Model should be done right.
    MavenProjectResult mavenProjectResult = new MavenProjectResult(directoryResolverResult.getTargetDirectory(), directoryResolverResult.getProjectDirectory(), directoryResolverResult.getIntegrationTestCaseDirectory(), new Model());
    storageHelper.put(ParameterType.ProjectResult + context.getUniqueId(), mavenProjectResult);

    Files.createDirectories(integrationTestCaseDirectory);

    //TODO: Reconsider deleting the local cache .m2/repository with each run yes/no
    // Define default behaviour => Remove it.
    // Make it configurable. How? Think about?
    if (mavenProject.isPresent()) {
      if (!Files.exists(directoryResolverResult.getProjectDirectory())) {
        Files.createDirectories(directoryResolverResult.getProjectDirectory());
        Files.createDirectories(directoryResolverResult.getCacheDirectory());

        PathUtils.copyDirectoryRecursively(directoryResolverResult.getSourceMavenProject(),
            directoryResolverResult.getProjectDirectory());

        PathUtils.copyDirectoryRecursively(directoryResolverResult.getTargetItfRepoDirectory(),
            directoryResolverResult.getCacheDirectory());
      }
    } else {
      PathUtils.deleteRecursively(directoryResolverResult.getProjectDirectory());
      Files.createDirectories(directoryResolverResult.getProjectDirectory());
      Files.createDirectories(directoryResolverResult.getCacheDirectory());

      if (!resourcesIts) {
        return;
      }

      PathUtils.copyDirectoryRecursively(directoryResolverResult.getSourceMavenProject(),
          directoryResolverResult.getProjectDirectory());
      PathUtils.copyDirectoryRecursively(directoryResolverResult.getTargetItfRepoDirectory(),
          directoryResolverResult.getCacheDirectory());
    }

  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
    return Stream.of(ParameterType.values())
        .anyMatch(parameterType -> parameterType.getKlass() == parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {

    StorageHelper sh = new StorageHelper(extensionContext);

    ParameterType parameterType = Stream.of(ParameterType.values())
        .filter(s -> s.getKlass() == parameterContext.getParameter().getType())
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("Unknown parameter type"));

    return sh.get(parameterType + extensionContext.getUniqueId(), parameterType.getKlass());
  }

  @Override
  public void interceptBeforeEachMethod(Invocation<Void> invocation,
                                        ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
    invocation.proceed();
  }


  /**
   * @return The content of the {@code PATH} environment variable.
   * @implNote The usage of System.getenv("PATH") triggers the java:S5304. In this case we don't transfer sensitive information.
   * In this case we suppress that SonarQube warning.
   */
  @SuppressWarnings("java:S5304")
  private Optional<String> getSystemPATH() {
    return Optional.ofNullable(System.getenv("PATH"));
  }

  @Override
  public void beforeTestExecution(ExtensionContext context)
      throws IOException, InterruptedException {

    Method methodName = context.getTestMethod().orElseThrow(() -> new IllegalStateException("No method given"));

    String prefix = "mvn";
    Optional<Class<?>> mavenProject = AnnotationHelper.findMavenProjectAnnotation(context);
    //TODO: In cases where we have MavenProject it might be better to have
    // different directories (which would be more concise with the other assumptions) with directory idea instead
    // of prefixed files.
    if (mavenProject.isPresent()) {
      prefix = methodName.getName() + "-mvn";
    }

    DirectoryResolverResult directoryResolverResult = new DirectoryResolverResult(context);
    Path integrationTestCaseDirectory = directoryResolverResult.getIntegrationTestCaseDirectory();
    Files.createDirectories(integrationTestCaseDirectory);

    //Copy ".predefined-repo" into ".m2/repository"
    Optional<Path> predefinedRepository = directoryResolverResult.getPredefinedRepository();
    if (predefinedRepository.isPresent()) {
      PathUtils.copyDirectoryRecursively(predefinedRepository.get(),
          directoryResolverResult.getCacheDirectory());
    } else {
      boolean annotationPresent = methodName.isAnnotationPresent(MavenPredefinedRepository.class);
      if (annotationPresent) {
        MavenPredefinedRepository annotation = methodName.getAnnotation(MavenPredefinedRepository.class);
        Path predefinedRepoPath = directoryResolverResult.getSourceMavenProject().resolve(annotation.value());
        PathUtils.copyDirectoryRecursively(predefinedRepoPath,
            directoryResolverResult.getCacheDirectory());
      }
    }

    Optional<Path> mvnLocation = new MavenLocator(FileSystems.getDefault(), getSystemPATH(), OS.WINDOWS.isCurrentOs()).findMvn();
    if (!mvnLocation.isPresent()) {
      throw new IllegalStateException("We could not find the maven executable `mvn` somewhere");
    }

    Path projectWorkingDirectory = directoryResolverResult.getProjectDirectory();
    Optional<MavenProjectLocation> mavenProjectLocationAnnotation = findMavenProjectLocationAnnotation(context);
    if (mavenProjectLocationAnnotation.isPresent()) {
      String mavenProjectLocation = mavenProjectLocationAnnotation.get().value();
      if (mavenProjectLocation.isEmpty()) {
        throw new IllegalStateException("You have to define a location in your MavenProjectLocation annotation");
      }
      projectWorkingDirectory = directoryResolverResult.getProjectDirectory().resolve(mavenProjectLocation);
    }

    ApplicationExecutor mavenExecutor = new ApplicationExecutor(projectWorkingDirectory,
        integrationTestCaseDirectory, mvnLocation.get(), Collections.emptyList(), prefix);

    List<String> executionArguments = new ArrayList<>();

    //TODO: Reconsider about the default options which are being defined here? Documented? users guide?
    List<String> defaultArguments = Arrays.asList(
        "-Dmaven.repo.local=" + directoryResolverResult.getCacheDirectory().toString());
    executionArguments.addAll(defaultArguments);

    if (hasProfiles(context)) {
      String collect = profiles(context).stream().collect(joining(",", "-P", ""));
      executionArguments.add(collect);
    }

    if (hasSystemProperties(context)) {
      List<String> collect = systemProperties(context)
          .stream()
          .map(s -> s.content().isEmpty() ? "-D" + s.value() : "-D" + s.value() + "=" + s.content())
          .collect(toList());
      executionArguments.addAll(collect);
    }

    if (hasOptions(context)) {
      executionArguments.addAll(options(context));
    } else {
      // If no option is defined at all the following are the defaults.
      executionArguments.addAll(DEFAULT_COMMAND_LINE_OPTIONS);
    }


    if (hasGoals(context)) {
      List<String> resultingGoals = goals(context);
      Map<String, String> keyValues = pomEntries(directoryResolverResult);

      List<String> filteredGoals = new PropertiesFilter(keyValues, resultingGoals).filter();
      executionArguments.addAll(filteredGoals);
    } else {
      //TODO: This is the default goal which will be executed if no `@MavenGoal` at all annotation is defined.
      executionArguments.add("package");
    }


    Process start = mavenExecutor.start(executionArguments);

    int processCompletableFuture = start.waitFor();

    ExecutionResult executionResult = ExecutionResult.Successful;
    if (processCompletableFuture != 0) {
      executionResult = ExecutionResult.Failure;
    }

    MavenLog log = new MavenLog(mavenExecutor.getStdout(), mavenExecutor.getStdErr());
    MavenCacheResult mavenCacheResult = new MavenCacheResult(directoryResolverResult.getCacheDirectory());

    Model model = ProjectHelper.readProject(projectWorkingDirectory.resolve("pom.xml"));

    MavenProjectResult mavenProjectResult = new MavenProjectResult(directoryResolverResult.getIntegrationTestCaseDirectory(),
        directoryResolverResult.getProjectDirectory(), directoryResolverResult.getCacheDirectory(), model);

    MavenExecutionResult result = new MavenExecutionResult(executionResult, processCompletableFuture, log,
        mavenProjectResult, mavenCacheResult);

    new StorageHelper(context).save(result, log, mavenCacheResult, mavenProjectResult);
  }

  private Map<String, String> pomEntries(DirectoryResolverResult directoryResolverResult) {
    //FIXME: Need to introduce better directory names
    Path mavenBaseDirectory = directoryResolverResult.getTargetDirectory().getParent();
    Path pomFile = mavenBaseDirectory.resolve("pom.xml");

    ModelReader modelReader = new ModelReader(ProjectHelper.readProject(pomFile));
    Map<String, String> keyValues = new HashMap<>();
    //The following three elements are read from the original pom file.
    keyValues.put("project.groupId", modelReader.getGroupId());
    keyValues.put("project.artifactId", modelReader.getArtifactId());
    keyValues.put("project.version", modelReader.getVersion());
    return keyValues;
  }

}
