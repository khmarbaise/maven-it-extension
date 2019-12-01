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
        directoryResolverResult.getIntegrationTestCaseDirectory(), new File(mvnLocation), Collections.emptyList(),
        "mvn");

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
