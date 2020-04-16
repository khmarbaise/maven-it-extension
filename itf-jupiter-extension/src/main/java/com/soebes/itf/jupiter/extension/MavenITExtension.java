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
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;
import java.util.stream.Stream;

import static com.soebes.itf.jupiter.extension.AnnotationHelper.getActiveProfiles;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.getCommandLineOptions;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.getCommandLineSystemProperties;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.getGoals;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.hasActiveProfiles;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.isDebug;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * @author Karl Heinz Marbaise
 */
public class MavenITExtension implements BeforeEachCallback, ParameterResolver, BeforeTestExecutionCallback,
    InvocationInterceptor{

  private static final Logger LOGGER = Logger.getLogger(MavenITExtension.class.getName());

  @Override
  public void beforeEach(ExtensionContext context) {
    Class<?> testClass = context.getTestClass()
        .orElseThrow(() -> new ExtensionConfigurationException("MavenITExtension is only supported for classes."));

    //FIXME: Need to reconsider the maven-it directory?
    File mavenItBaseDirectory = new File(DirectoryHelper.getTargetDir(), "maven-it");
    String toFullyQualifiedPath = DirectoryHelper.toFullyQualifiedPath(testClass);

    File mavenItTestCaseBaseDirectory = new File(mavenItBaseDirectory, toFullyQualifiedPath);
    //TODO: What happends if the directory has been created by a previous run?
    // should we delete that structure here? Maybe we should make this configurable.
    mavenItTestCaseBaseDirectory.mkdirs();

    new StorageHelper(context).save(mavenItBaseDirectory, mavenItTestCaseBaseDirectory, DirectoryHelper.getTargetDir());
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    return Stream.of(ParameterType.values())
        .anyMatch(parameterType -> parameterType.getKlass() == parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {

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

  @Override
  public void beforeTestExecution(ExtensionContext context)
      throws IOException, InterruptedException, XmlPullParserException {

    DirectoryResolverResult directoryResolverResult = new DirectoryResolverResult(context);

    File integrationTestCaseDirectory = directoryResolverResult.getIntegrationTestCaseDirectory();
    integrationTestCaseDirectory.mkdirs();
    directoryResolverResult.getProjectDirectory().mkdirs();
    directoryResolverResult.getCacheDirectory().mkdirs();

    //FIXME: Copy artifacts from maven-invoker-plugin:install location into each cache; Currently HARD CODED!!
    FileUtils.copyDirectory(directoryResolverResult.getComponentUnderTestDirectory(),
        directoryResolverResult.getCacheDirectory());
    FileUtils.copyDirectory(directoryResolverResult.getSourceMavenProject(),
        directoryResolverResult.getProjectDirectory());

    String mavenHome = System.getProperty("maven.home");
    if (mavenHome == null || mavenHome.isEmpty()) {
      //FIXME: currently not set; using hard coded path? Need to reconsider how to set it?
      mavenHome = "/Users/khmarbaise/tools/maven";
    }
    String mvnLocation = mavenHome + "/bin/mvn";
    if (OS.WINDOWS.isCurrentOs()) {
      mvnLocation += ".cmd";
    }


    String prefix = "mvn";
    Method methodName = context.getTestMethod().orElseThrow(() -> new IllegalStateException("No method given"));
    Optional<Class<?>> mavenProject = AnnotationHelper.findMavenProjectAnnotation(context);
    if (mavenProject.isPresent()) {
      prefix = methodName.getName() + "-mvn";
    }

    //FIXME: Removed hard coded parts.
    ApplicationExecutor mavenExecutor = new ApplicationExecutor(directoryResolverResult.getProjectDirectory(),
        integrationTestCaseDirectory, new File(mvnLocation), Collections.emptyList(), prefix);

    List<String> executionArguments = new ArrayList<>();


    List<String> defaultArguments = Arrays.asList(
        "-Dmaven.repo.local=" + directoryResolverResult.getCacheDirectory().toString(), MavenOptions.BATCH_MODE, MavenOptions.SHOW_VERSION);
    executionArguments.addAll(defaultArguments);

    if (hasActiveProfiles(methodName)) {
      String collect = Stream.of(getActiveProfiles(methodName))
          .collect(joining(",", MavenOptions.ACTIVATE_PROFILES, ""));
      executionArguments.add(collect);
    }


    if (isDebug(methodName)) {
      executionArguments.add(MavenOptions.DEBUG);
    }

    executionArguments.addAll(Stream.of(getCommandLineSystemProperties(methodName))
        .map(arg -> "-D" + arg).collect(toList()));
    executionArguments.addAll(Stream.of(getCommandLineOptions(methodName)).collect(toList()));

    Class<?> mavenIT = AnnotationHelper.findMavenITAnnotation(context).orElseThrow(IllegalStateException::new);
    MavenIT mavenITAnnotation = mavenIT.getAnnotation(MavenIT.class);
    //FIXME: We need to filter placeholder here?

    //FIXME: Need to introduce better directory names
    //Refactor out the following lines
    File mavenBaseDirectory = new File(directoryResolverResult.getTargetDirectory(), "..");
    File pomFile = new File(mavenBaseDirectory, "pom.xml");
    PomReader pomReader = new PomReader(new FileInputStream(pomFile));
    ModelReader modelReader = new ModelReader(pomReader.getModel());
    Map<String, String> keyValues = new HashMap<>();
    //The following three elements we are reading from the original pom file.
    keyValues.put("project.groupId", modelReader.getGroupId());
    keyValues.put("project.artifactId", modelReader.getArtifactId());
    keyValues.put("project.version", modelReader.getVersion());

    List<String> resultingGoals = Stream.of(GoalPriority.goals(mavenITAnnotation.goals(), getGoals(methodName))).collect(toList());
    PropertiesFilter propertiesFilter = new PropertiesFilter(keyValues, resultingGoals);

    //TODO: We should consider to make replacements also in systemProperties annotation possible.

    List<String> filteredGoals = propertiesFilter.filter();
    executionArguments.addAll(filteredGoals);

    Process start = mavenExecutor.start(executionArguments);

    int processCompletableFuture = start.waitFor();

    ExecutionResult executionResult = ExecutionResult.Successful;
    if (processCompletableFuture != 0) {
      executionResult = ExecutionResult.Failure;
    }

    MavenLog log = new MavenLog(mavenExecutor.getStdout(), mavenExecutor.getStdErr());
    MavenCacheResult mavenCacheResult = new MavenCacheResult(directoryResolverResult.getCacheDirectory().toPath());

    Model model = ProjectHelper.readProject(directoryResolverResult.getProjectDirectory());
    MavenProjectResult mavenProjectResult = new MavenProjectResult(directoryResolverResult.getProjectDirectory(),
        model);

    MavenExecutionResult result = new MavenExecutionResult(executionResult, processCompletableFuture, log,
        mavenProjectResult, mavenCacheResult);

    new StorageHelper(context).save(result, log, mavenCacheResult, mavenProjectResult);
  }

}
