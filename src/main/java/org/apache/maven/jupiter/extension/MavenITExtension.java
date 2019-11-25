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

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.maven.jupiter.extension.AnnotationHelper.getActiveProfiles;
import static org.apache.maven.jupiter.extension.AnnotationHelper.getGoals;
import static org.apache.maven.jupiter.extension.AnnotationHelper.hasActiveProfiles;
import static org.apache.maven.jupiter.extension.AnnotationHelper.isDebug;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.apache.commons.io.FileUtils;
import org.apache.maven.jupiter.extension.maven.MavenCacheResult;
import org.apache.maven.jupiter.extension.maven.MavenExecutionResult;
import org.apache.maven.jupiter.extension.maven.MavenExecutionResult.ExecutionResult;
import org.apache.maven.jupiter.extension.maven.MavenExecutor;
import org.apache.maven.jupiter.extension.maven.MavenLog;
import org.apache.maven.jupiter.extension.maven.MavenProjectResult;
import org.apache.maven.jupiter.extension.maven.ProjectHelper;
import org.apache.maven.jupiter.utils.DirectoryHelper;
import org.apache.maven.model.Model;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.InvocationInterceptor;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.ReflectiveInvocationContext;

/**
 * @author Karl Heinz Marbaise
 */
public class MavenITExtension implements BeforeEachCallback, ParameterResolver, BeforeTestExecutionCallback,
    InvocationInterceptor {

  @Override
  public void beforeEach(ExtensionContext context) {
    System.out.println("! MavenITExtension: beforeEach()");
    Class<?> testClass = context.getTestClass()
        .orElseThrow(() -> new ExtensionConfigurationException("MavenITExtension is only supported for classes."));

    System.out.println(
        "! MavenITExtension: beforeEach() context.getTestMethod() = " + context.getTestMethod().get().getName());
    //FIXME: Need to reconsider the maven-it directory?
    File baseDirectory = new File(DirectoryHelper.getTargetDir(), "maven-it");
    String toFullyQualifiedPath = DirectoryHelper.toFullyQualifiedPath(testClass);

    File mavenItBaseDirectory = new File(baseDirectory, toFullyQualifiedPath);
    mavenItBaseDirectory.mkdirs();

    Store store = context.getStore(MavenITNameSpace.NAMESPACE_MAVEN_IT);
    store.put(Result.BaseDirectory, baseDirectory);
    store.put(Result.BaseITDirectory, mavenItBaseDirectory);
    store.put(MavenITNameSpace.TARGET_DIRECTORY, DirectoryHelper.getTargetDir());
  }

  private static final List<Class<?>> VALID_PARAMETER_TYPES = Arrays.asList(MavenExecutionResult.class, MavenLog.class,
      MavenCacheResult.class, MavenProjectResult.class, MavenExecutor.class);

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    Executable declaringExecutable = parameterContext.getParameter().getDeclaringExecutable();
    Parameter parameter = parameterContext.getParameter();
    System.out.println("parameterContext.getParameter() = " + parameter.getName());
    System.out.println("declaringExecutable.getParameters() = " + declaringExecutable.getParameterCount());
    //Java9+
    // List.of(...)

    return VALID_PARAMETER_TYPES.contains(parameterContext.getParameter().getType());
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {

    System.out.println("!*** resolveParameter *** !");
    System.out.println("! Testmethode: " + extensionContext.getTestMethod().get().getName());
    System.out.println("! extensionContext.getElement() = " + extensionContext.getElement());

    Store nameSpace = extensionContext.getStore(MavenITNameSpace.NAMESPACE_MAVEN_IT);

    Result result = Stream.of(Result.values())
        .filter(s -> s.getKlass() == parameterContext.getParameter().getType())
        .findFirst()
        .orElseGet(() -> Result.BaseITDirectory);

    if (parameterContext.getParameter().getType().equals(MavenExecutor.class)) {
      System.out.println("! Parameter type MavenExecutor");
      return new MavenExecutor("ExecutorName");
    } else {
      return nameSpace.get(result + extensionContext.getUniqueId(), result.getKlass());
    }
  }

  @Override
  public void interceptBeforeEachMethod(Invocation<Void> invocation,
      ReflectiveInvocationContext<Method> invocationContext, ExtensionContext extensionContext) throws Throwable {
    System.out.println("+-------------------------------------------------------------------+");
    System.out.println("! invocationContext = method: " + invocationContext.getExecutable().getName());
    if (extensionContext.getTestMethod().isPresent()) {
      System.out.println("! invocation = " + extensionContext.getTestMethod().get().getName());
    }
    invocation.proceed();
    System.out.println("^-------------------------------------------------------------------^");
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) throws IOException, InterruptedException {
    System.out.println("! MavenITExtension: beforeTestExecution: context.getTestMethod() = " + context.getTestMethod()
        .get()
        .getName());

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

    ExecutorMaven executorMaven = new ExecutorMaven(directoryResolverResult, context);
    executorMaven.execute();
    String mavenHome = System.getProperty("maven.home");
    if (mavenHome == null || mavenHome.isEmpty()) {
      //FIXME: currently not set; using hard coded path? Need to reconsider how to set it?
      mavenHome = "/Users/khmarbaise/tools/maven";
    }
    //FIXME: Very likely we need to tweak for Windows environment? see maven-invoker how to find Maven executable?
    String mvnLocation = mavenHome + "/bin/mvn";

    //FIXME: Removed hard coded parts.
    ApplicationExecutor mavenExecutor = new ApplicationExecutor(directoryResolverResult.getProjectDirectory(),
        integrationTestCaseDirectory, new File(mvnLocation), Collections.emptyList(), "mvn");

    //Process start = mavenExecutor.start(Arrays.asList("--no-transfer-progress", "-V", "clean", "verify"));
    //FIXME: Need to think about the default options given for a IT.

    List<String> executionArguments = new ArrayList<>();
    List<String> defaultArguments = Arrays.asList(
        "-Dmaven.repo.local=" + directoryResolverResult.getCacheDirectory().toString(), "--batch-mode", "-V");
    executionArguments.addAll(defaultArguments);

    Method methodName = context.getTestMethod().orElseThrow(() -> new IllegalStateException("No method given"));
    if (hasActiveProfiles(methodName)) {
      String collect = Stream.of(getActiveProfiles(methodName)).collect(joining(",", "-P", ""));
      executionArguments.add(collect);
    }

    if (isDebug(methodName)) {
      executionArguments.add("-X");
    }

    Class<?> mavenIT = AnnotationHelper.findMavenITAnnotation(context).orElseThrow(IllegalStateException::new);
    MavenIT mavenITAnnotation = mavenIT.getAnnotation(MavenIT.class);
    String[] resultingGoals = GoalPriority.goals(mavenITAnnotation.goals(), getGoals(methodName));
    executionArguments.addAll(Stream.of(resultingGoals).collect(toList()));

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

    Store nameSpace = context.getStore(MavenITNameSpace.NAMESPACE_MAVEN_IT);
    nameSpace.put(Result.ExecutionResult + context.getUniqueId(), result);
    nameSpace.put(Result.LogResult + context.getUniqueId(), log);
    nameSpace.put(Result.CacheResult + context.getUniqueId(), mavenCacheResult);
    nameSpace.put(Result.ProjectResult + context.getUniqueId(), mavenProjectResult);
  }

}
