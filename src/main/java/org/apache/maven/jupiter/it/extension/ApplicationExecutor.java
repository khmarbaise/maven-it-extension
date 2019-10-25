package org.apache.maven.jupiter.it.extension;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    this(System.getProperty("java.home"), loggingDirectory, workingDirectory, applicationExecutable, jvmArguments,
        prefix);
  }

  public Process start(List<String> startArguments) throws IOException {

    //    String javaBin = this.javaHome + "/bin/java";

    List<String> applicationArguments = new ArrayList<>();
    applicationArguments.addAll(Collections.singletonList(applicationExecutable.toString()));
    //    applicationArguments.addAll(jvmArguments);
    //    applicationArguments.addAll(Arrays.asList("-jar", application.getAbsolutePath()));
    applicationArguments.addAll(startArguments);

    applicationArguments.forEach(s -> System.out.println("arguments = " + s));

    ProcessBuilder pb = new ProcessBuilder(applicationArguments);
    Map<String, String> environment = pb.environment();
    environment.entrySet()
        .stream()
        .forEach(stringStringEntry -> System.out.println(
            "environment k:" + stringStringEntry.getKey() + " v:" + stringStringEntry.getValue()));
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
