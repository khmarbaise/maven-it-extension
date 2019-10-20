package org.apache.maven.jupiter.it.extension;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.io.FileUtils;
import org.apache.maven.jupiter.it.extension.MavenExecutionResult.ExecutionResult;
import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterTestExecutionCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.BeforeTestExecutionCallback;
import org.junit.jupiter.api.extension.ExtensionConfigurationException;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolutionException;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.junit.platform.commons.util.AnnotationUtils;

public class MavenITExtension implements BeforeEachCallback, BeforeAllCallback, TestInstancePostProcessor,
    ParameterResolver, BeforeTestExecutionCallback, AfterTestExecutionCallback, AfterAllCallback {

  private static final String BASE_DIRECTORY = "BASE_DIRECTORY";

  private static final String EXECUTION_RESULT = "EXECUTION_RESULT";

  private static final Namespace NAMESPACE_MAVEN_IT = Namespace.create(MavenITExtension.class);

  private Optional<MavenIT> findMavenIt(ExtensionContext context) {
    Optional<ExtensionContext> current = Optional.of(context);
    while (current.isPresent()) {
      Optional<MavenIT> endToEndTest = AnnotationUtils.findAnnotation(current.get().getRequiredTestClass(),
          MavenIT.class);
      if (endToEndTest.isPresent()) {
        return endToEndTest;
      }
      current = current.get().getParent();
    }
    return Optional.empty();
  }

  @Override
  public void beforeAll(ExtensionContext context) {
    System.out.println("MavenITExtension.beforeAll root:" + context.getUniqueId());
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception {
    System.out.println("MavenITExtension.beforeEach " + context.getUniqueId());
    Class<?> testClass = context.getTestClass()
        .orElseThrow(() -> new ExtensionConfigurationException("MavenITExtension is only supported for classes."));

    MavenIT mavenITAnnotation = findMavenIt(context).orElseThrow(
        () -> new IllegalStateException("Annotation is not present."));

    File baseDirectory = new File(DirectoryHelper.getTargetDir(), "maven-it");
    //File classBaseDirectory = new File(baseDirectory, testClass.getSimpleName());
    File mavenItBaseDirectory = new File(baseDirectory, DirectoryHelper.path(context.getUniqueId()).toString());
    mavenItBaseDirectory.mkdirs();

    Store store = context.getStore(NAMESPACE_MAVEN_IT);
    store.put(BASE_DIRECTORY + context.getUniqueId(), mavenItBaseDirectory);
  }

  @Override
  public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
    System.out.println("MavenITExtension.postProcessTestInstance " + context.getUniqueId());
  }

  @Override
  public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    System.out.println("MavenITExtension.supportsParameter");
    List<Class<?>> availableTypes = Arrays.asList(MavenExecutionResult.class);
    System.out.println(" --> Checking for " + availableTypes);
    System.out.println(
        "     parameterContext.getParameter().getName() = " + parameterContext.getParameter().getParameterizedType());
    return availableTypes.contains(parameterContext.getParameter().getParameterizedType());
  }

  @Override
  public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext)
      throws ParameterResolutionException {
    System.out.println(" --> MavenITExtension.resolveParameter");

    Store nameSpace = extensionContext.getStore(NAMESPACE_MAVEN_IT);
    return nameSpace.get(EXECUTION_RESULT + extensionContext.getUniqueId(), MavenExecutionResult.class);
  }

  private boolean isDebug(Method method) {
    if (!method.isAnnotationPresent(MavenTest.class)) {
      throw new IllegalStateException("MavenTest Annotation nicht an der Method");
    }
    MavenTest mavenTestAnnotation = method.getAnnotation(MavenTest.class);

    return mavenTestAnnotation.debug();
  }

  @Override
  public void beforeTestExecution(ExtensionContext context) throws IOException, InterruptedException {
    System.out.println("MavenITExtension.beforeTestExecution");
    AnnotatedElement annotatedElement = context.getElement()
        .orElseThrow(() -> new IllegalStateException("STDImport Annotation not found."));

    Store nameSpace = context.getStore(NAMESPACE_MAVEN_IT);
    File mavenItBaseDirectory = nameSpace.get(BASE_DIRECTORY + context.getUniqueId(), File.class);

    Method methodName = context.getTestMethod().orElseThrow(() -> new IllegalStateException("No method given"));

    File integrationTestCaseDirectory = new File(mavenItBaseDirectory, methodName.getName());

    integrationTestCaseDirectory.mkdirs();

    File cacheDirectory = new File(integrationTestCaseDirectory, ".m2/repository");
    cacheDirectory.mkdirs();

    File projectDirectory = new File(integrationTestCaseDirectory, "project");
    projectDirectory.mkdirs();

    //FIXME: Removed hard coded parts.
    Class<?> testClass = context.getTestClass().orElseThrow(() -> new IllegalStateException("Test class not found."));
    File copyMavenPluginProject = new File(DirectoryHelper.getTargetDir(),
        "test-classes/maven-its/" + testClass.getSimpleName() + "/" + methodName.getName());
    FileUtils.copyDirectory(copyMavenPluginProject, projectDirectory);

    //FIXME: Removed hard coded parts.
    ApplicationExecutor mavenExecutor = new ApplicationExecutor(projectDirectory, integrationTestCaseDirectory,
        new File("/Users/khmarbaise/tools/maven/bin/mvn"), Collections.emptyList(), "mvn");

    //Process start = mavenExecutor.start(Arrays.asList("--no-transfer-progress", "-V", "clean", "verify"));
    //FIXME: Need to think about the default options given for a IT.

    List<String> executionArguments = new ArrayList<>();
    List<String> defaultArguments = Arrays.asList("-Dmaven.repo.local=" + cacheDirectory.toString(), "--batch-mode",
        "-V", "clean", "verify", "-Prun-its");
    executionArguments.addAll(defaultArguments);
    if (isDebug(methodName)) {
      executionArguments.add("-X");
    }
    Process start = mavenExecutor.start(executionArguments);

    int processCompletableFuture = start.waitFor();

    ExecutionResult executionResult = ExecutionResult.Successful;
    if (processCompletableFuture != 0) {
      executionResult = ExecutionResult.Failure;
    }
    MavenExecutionResult result = new MavenExecutionResult(executionResult, processCompletableFuture);
    nameSpace.put(EXECUTION_RESULT + context.getUniqueId(), result);
  }

  @Override
  public void afterTestExecution(ExtensionContext context) {
    System.out.println("MavenITExtension.afterTestExecution");
  }

  @Override
  public void afterAll(ExtensionContext context) {
    System.out.println("MavenITExtension.afterAll root:" + context.getUniqueId());
  }
}
