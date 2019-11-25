package org.apache.maven.jupiter.extension;

import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.maven.jupiter.extension.AnnotationHelper.getActiveProfiles;
import static org.apache.maven.jupiter.extension.AnnotationHelper.getGoals;
import static org.apache.maven.jupiter.extension.AnnotationHelper.hasActiveProfiles;
import static org.apache.maven.jupiter.extension.AnnotationHelper.isDebug;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;

public class ExecutorMaven {

  private final DirectoryResolverResult directoryResolverResult;

  private final ExtensionContext context;

  public ExecutorMaven(DirectoryResolverResult directoryResolverResult, ExtensionContext context) {
    this.directoryResolverResult = directoryResolverResult;
    this.context = context;
  }

  public void execute() {
    String mavenHome = System.getProperty("maven.home");
    if (mavenHome == null || mavenHome.isEmpty()) {
      //FIXME: currently not set; using hard coded path? Need to reconsider how to set it?
      mavenHome = "/Users/khmarbaise/tools/maven";
    }
    //FIXME: Very likely we need to tweak for Windows environment? see maven-invoker how to find Maven executable?
    String mvnLocation = mavenHome + "/bin/mvn";

    //FIXME: Removed hard coded parts.
    ApplicationExecutor mavenExecutor = new ApplicationExecutor(directoryResolverResult.getProjectDirectory(),
        directoryResolverResult.getIntegrationTestCaseDirectory(), new File(mvnLocation), Collections.emptyList(), "mvn");

    //FIXME: Need to think about the default options given for a IT.
    //Process start = mavenExecutor.start(Arrays.asList("--no-transfer-progress", "-V", "clean", "verify"));

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
  }
}
