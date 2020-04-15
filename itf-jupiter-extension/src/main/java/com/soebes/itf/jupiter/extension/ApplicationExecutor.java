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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Karl Heinz Marbaise
 */
public class ApplicationExecutor {

  private final File loggingDirectory;

  private final File workingDirectory;

  private final File applicationExecutable;

  private final List<String> jvmArguments;

  private final String prefix;

  private final String javaHome;

  public ApplicationExecutor(String javaHome, File loggingDirectory, File workingDirectory, File applicationExecutable,
      List<String> jvmArguments, String prefix) {
    this.javaHome = javaHome;
    this.loggingDirectory = loggingDirectory;
    this.workingDirectory = workingDirectory;
    this.applicationExecutable = applicationExecutable;
    this.jvmArguments = jvmArguments;
    this.prefix = prefix;
  }

  public ApplicationExecutor(File workingDirectory, File loggingDirectory, File applicationExecutable,
      List<String> jvmArguments, String prefix) {
    //TODO: This should be made configurable
    this(System.getProperty("java.home"), loggingDirectory, workingDirectory, applicationExecutable, jvmArguments,
        prefix);
  }

  public Process start(List<String> startArguments) throws IOException {

    List<String> applicationArguments = new ArrayList<>();
    //TODO: Can make that better?
    applicationArguments.addAll(Collections.singletonList(applicationExecutable.toString()));
    applicationArguments.addAll(startArguments);

    //TODO: Can make that better?
    try {
      Files.write(Paths.get(loggingDirectory.getAbsolutePath(), this.prefix + "-arguments.log"), applicationArguments,
          StandardOpenOption.CREATE);
    } catch (IOException e) {
      e.printStackTrace();
    }

    ProcessBuilder pb = new ProcessBuilder(applicationArguments);
    Map<String, String> environment = pb.environment();
    pb.redirectError(new File(loggingDirectory, this.prefix + "-stderr.out"));
    pb.redirectOutput(new File(loggingDirectory, this.prefix + "-stdout.out"));
    pb.directory(workingDirectory);
    return pb.start();
  }

  public int startAndWaitUntilEnded(List<String> args) throws IOException, InterruptedException {
    Process start = start(args);
    return start.waitFor();
  }

  public Path getStdout() {
    return Paths.get(loggingDirectory.toString(), this.prefix + "-stdout.out");
  }

  Stream<String> createLogStream() {
    InputStream resourceAsStream = this.getClass().getResourceAsStream("/mvn-stdout.out");
    return new BufferedReader(new InputStreamReader(resourceAsStream, Charset.defaultCharset())).lines();
  }

  public Path getStdErr() {
    return Paths.get(loggingDirectory.toString(), this.prefix + "-stderr.out");
  }

  private File doesApplicationExist() {
    File application = this.applicationExecutable;
    if (!application.exists()) {
      throw new IllegalStateException("The " + applicationExecutable + " does not exist.");
    }
    return application;
  }

}
