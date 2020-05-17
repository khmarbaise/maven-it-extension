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
import com.soebes.itf.jupiter.mrm.FileSystemServer;
import com.soebes.itf.jupiter.mrm.MRMServer;
import com.soebes.itf.jupiter.mrm.RepoContainer;
import me.alexpanov.net.FreePortFinder;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.condition.OS;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import static com.soebes.itf.jupiter.extension.AnnotationHelper.getActiveProfiles;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.getCommandLineOptions;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.getCommandLineSystemProperties;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.getGoals;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.hasActiveProfiles;
import static com.soebes.itf.jupiter.extension.AnnotationHelper.isDebug;
import static com.soebes.itf.jupiter.extension.StorageHelper.NAMESPACE_MAVEN_IT;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;

/**
 * @author Karl Heinz Marbaise
 */
public class MavenITExtension implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback, ParameterResolver, BeforeTestExecutionCallback,
    InvocationInterceptor {

  private static final String MRM_SERVER = "MRM_SERVER";
  private static final String GENERATED_SETTINGS = "GENERATED-SETTINGS";

  @Override
  public void beforeAll(ExtensionContext context) throws Exception {
    Class<?> testClass = context.getTestClass()
        .orElseThrow(() -> new ExtensionConfigurationException("MavenITExtension is only supported for classes."));

    boolean mockRepositoryManagerAnnotation = testClass.isAnnotationPresent(MavenMockRepositoryManager.class);
    if (mockRepositoryManagerAnnotation) {
      //FIXME: Code duplication.
      File mavenItsBaseDirectory = new File(DirectoryHelper.getTargetDir(), "test-classes");
      Optional<Class<?>> optionalMavenMockRepositoryRepository = AnnotationHelper.findMavenMockRepositoryManager(context);

      if (optionalMavenMockRepositoryRepository.isPresent()) {
        MavenMockRepositoryManager mockRepositoryManager = optionalMavenMockRepositoryRepository.get().getAnnotation(MavenMockRepositoryManager.class);
        String repositoryPath = DirectoryHelper.toFullyQualifiedPath(optionalMavenMockRepositoryRepository.get());
        File cacheDirectoryBase = new File(mavenItsBaseDirectory, repositoryPath);
        File mrmRepository = new File(cacheDirectoryBase, mockRepositoryManager.value());
        if (!mrmRepository.isDirectory() && !mrmRepository.exists()) {
          throw new IllegalStateException("The " + mrmRepository.getAbsolutePath() + " is either no directory or does not exist.");
        }
        int freeLocalPort = FreePortFinder.findFreeLocalPort();

        //From the failsafe-plugin configuration:
        String localRepository = System.getProperty("localRepository");
        if (localRepository == null || localRepository.isEmpty()) {
//          throw new IllegalStateException("can read system property 'localRepository'");
          //FIXME: Hard CODED TESTING
          localRepository = "/Users/khmarbaise/.m2/repository";
        }

        File localM2repo = new File(localRepository);
        RepoContainer repoContainer = new RepoContainer()
            .add(localM2repo)
            .add(mrmRepository);

        MRMServer mrm = new MRMServer(repoContainer, freeLocalPort);

        FileSystemServer fileSystemServer = mrm.create();

        Store store = context.getStore(NAMESPACE_MAVEN_IT);
        store.put(MRM_SERVER + context.getUniqueId(), mrm);

        //Check if we are the correct location.
        CreateSettingsFromTemplate createSettingsFromTemplate = new CreateSettingsFromTemplate(URI.create(fileSystemServer.getUrl()));
        List<String> settingsLines = createSettingsFromTemplate.create();

        Path pathSettingsXml = Paths.get(cacheDirectoryBase.getAbsolutePath(), "generated-settings.xml");
        try {
          Files.write(pathSettingsXml, settingsLines,
              StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
          e.printStackTrace();
        }
        store.put(GENERATED_SETTINGS + context.getUniqueId(), pathSettingsXml.toFile());

        mrm.start();
      }

    }
  }

  @Override
  public void afterAll(ExtensionContext context) throws Exception {
    Class<?> testClass = context.getTestClass()
        .orElseThrow(() -> new ExtensionConfigurationException("MavenITExtension is only supported for classes."));
    if (testClass.isAnnotationPresent(MavenMockRepositoryManager.class)) {
      Store store = context.getStore(NAMESPACE_MAVEN_IT);
      MRMServer mrmServer = store.get(MRM_SERVER + context.getUniqueId(), MRMServer.class);

      mrmServer.shutdown();
    }
  }


  @Override
  public void beforeEach(ExtensionContext context) {
    Class<?> testClass = context.getTestClass()
        .orElseThrow(() -> new ExtensionConfigurationException("MavenITExtension is only supported for classes."));

    //FIXME: Need to reconsider the maven-it directory?
    File mavenItBaseDirectory = new File(DirectoryHelper.getTargetDir(), "maven-it");
    String toFullyQualifiedPath = DirectoryHelper.toFullyQualifiedPath(testClass);

    File mavenItTestCaseBaseDirectory = new File(mavenItBaseDirectory, toFullyQualifiedPath);
    //TODO: What happens if the directory has been created by a previous run?
    // should we delete that structure here? Maybe we should make this configurable.
    mavenItTestCaseBaseDirectory.mkdirs();

    new StorageHelper(context).save(mavenItBaseDirectory, mavenItTestCaseBaseDirectory, DirectoryHelper.getTargetDir());
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
      throws IOException, InterruptedException, XmlPullParserException {

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
    File integrationTestCaseDirectory = directoryResolverResult.getIntegrationTestCaseDirectory();
    integrationTestCaseDirectory.mkdirs();

    if (mavenProject.isPresent()) {
      if (!directoryResolverResult.getProjectDirectory().exists()) {
        directoryResolverResult.getProjectDirectory().mkdirs();
        directoryResolverResult.getCacheDirectory().mkdirs();

        FileUtils.copyDirectory(directoryResolverResult.getSourceMavenProject(),
            directoryResolverResult.getProjectDirectory());
        FileUtils.copyDirectory(directoryResolverResult.getComponentUnderTestDirectory(),
            directoryResolverResult.getCacheDirectory());
      }
    } else {
      FileUtils.deleteQuietly(directoryResolverResult.getProjectDirectory());
      directoryResolverResult.getProjectDirectory().mkdirs();
      directoryResolverResult.getCacheDirectory().mkdirs();

      FileUtils.copyDirectory(directoryResolverResult.getSourceMavenProject(),
          directoryResolverResult.getProjectDirectory());
      FileUtils.copyDirectory(directoryResolverResult.getComponentUnderTestDirectory(),
          directoryResolverResult.getCacheDirectory());
    }

    //Copy ".predefined-repo" into ".m2/repository"
    Optional<File> predefinedRepository = directoryResolverResult.getPredefinedRepository();
    if (predefinedRepository.isPresent()) {
      FileUtils.copyDirectory(predefinedRepository.get(),
          directoryResolverResult.getCacheDirectory());
    } else {
      boolean annotationPresent = methodName.isAnnotationPresent(MavenPredefinedRepository.class);
      if (annotationPresent) {
        MavenPredefinedRepository annotation = methodName.getAnnotation(MavenPredefinedRepository.class);
        File predefinedRepoFile = new File(directoryResolverResult.getSourceMavenProject(), annotation.value());
        FileUtils.copyDirectory(predefinedRepoFile,
            directoryResolverResult.getCacheDirectory());
      }
    }

    Optional<Path> mvnLocation = new MavenLocator(FileSystems.getDefault(), getSystemPATH(), OS.WINDOWS.isCurrentOs()).findMvn();
    if (!mvnLocation.isPresent()) {
      throw new IllegalStateException("We could not find the maven executable `mvn` somewhere");
    }

    ApplicationExecutor mavenExecutor = new ApplicationExecutor(directoryResolverResult.getProjectDirectory(),
        integrationTestCaseDirectory, mvnLocation.get(), Collections.emptyList(), prefix);

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
    MavenJupiterExtension mavenJupiterExtensionAnnotation = mavenIT.getAnnotation(MavenJupiterExtension.class);

    //FIXME: Need to introduce better directory names
    //Refactor out the following lines
    File mavenBaseDirectory = new File(directoryResolverResult.getTargetDirectory(), "..");
    File pomFile = new File(mavenBaseDirectory, "pom.xml");

    ModelReader modelReader = new ModelReader(ProjectHelper.readProject(pomFile));
    Map<String, String> keyValues = new HashMap<>();
    //The following three elements we are reading from the original pom file.
    keyValues.put("project.groupId", modelReader.getGroupId());
    keyValues.put("project.artifactId", modelReader.getArtifactId());
    keyValues.put("project.version", modelReader.getVersion());

    List<String> resultingGoals = Stream.of(GoalPriority.goals(mavenJupiterExtensionAnnotation.goals(), getGoals(methodName))).collect(toList());
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

    Model model = ProjectHelper.readProject(new File(directoryResolverResult.getProjectDirectory(), "pom.xml"));
    MavenProjectResult mavenProjectResult = new MavenProjectResult(directoryResolverResult.getProjectDirectory(),
        model);

    MavenExecutionResult result = new MavenExecutionResult(executionResult, processCompletableFuture, log,
        mavenProjectResult, mavenCacheResult);

    new StorageHelper(context).save(result, log, mavenCacheResult, mavenProjectResult);
  }

}
