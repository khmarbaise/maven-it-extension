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
import java.util.stream.Stream;

/**
 * @author Karl Heinz Marbaise
 */
public class ApplicationExecutor {

  private final File loggingDirectory;

  private final File workingDirectory;

  private final Path applicationExecutable;

  private final List<String> jvmArguments;

  private final String prefix;

  private final String javaHome;

  public ApplicationExecutor(String javaHome, File loggingDirectory, File workingDirectory, Path applicationExecutable,
                             List<String> jvmArguments, String prefix) {
    this.javaHome = javaHome;
    this.loggingDirectory = loggingDirectory;
    this.workingDirectory = workingDirectory;
    this.applicationExecutable = applicationExecutable;
    this.jvmArguments = jvmArguments;
    this.prefix = prefix;
  }

  public ApplicationExecutor(File workingDirectory, File loggingDirectory, Path applicationExecutable,
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
    Path argumentsLog = Paths.get(loggingDirectory.getAbsolutePath(), this.prefix + "-arguments.log");
    Files.deleteIfExists(argumentsLog);
    try {
      Files.write(argumentsLog, applicationArguments,
          StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to write argument log file", e);
    }

    Path stdErrOut = Paths.get(loggingDirectory.getAbsolutePath(), this.prefix + "-stderr.log");
    Path stdOutOut = Paths.get(loggingDirectory.getAbsolutePath(), this.prefix + "-stdout.log");
    Files.deleteIfExists(stdErrOut);
    Files.deleteIfExists(stdOutOut);
    ProcessBuilder pb = new ProcessBuilder(applicationArguments);
    pb.redirectError(stdErrOut.toFile());
    pb.redirectOutput(stdOutOut.toFile());
    pb.directory(workingDirectory);
    return pb.start();
  }

  public int startAndWaitUntilEnded(List<String> args) throws IOException, InterruptedException {
    Process start = start(args);
    return start.waitFor();
  }

  public Path getStdout() {
    return Paths.get(loggingDirectory.toString(), this.prefix + "-stdout.log");
  }

  Stream<String> createLogStream() {
    InputStream resourceAsStream = this.getClass().getResourceAsStream("/mvn-stdout.log");
    return new BufferedReader(new InputStreamReader(resourceAsStream, Charset.defaultCharset())).lines();
  }

  public Path getStdErr() {
    return Paths.get(loggingDirectory.toString(), this.prefix + "-stderr.log");
  }

}
