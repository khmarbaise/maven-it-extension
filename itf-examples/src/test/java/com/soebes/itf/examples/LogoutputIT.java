package com.soebes.itf.examples;

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

import com.soebes.itf.jupiter.extension.MavenDebug;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenLog;
import org.junit.jupiter.api.condition.EnabledOnOs;
import org.junit.jupiter.api.condition.OS;

import java.io.IOException;
import java.nio.file.Files;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;

/**
 * This integration tests shows some examples how to analyse the log output during a build.
 *
 * @author Karl Heinz Marbaise
 */
@MavenJupiterExtension
@MavenDebug
class LogoutputIT {

  @MavenTest
  @EnabledOnOs({OS.MAC, OS.LINUX})
  void basic(MavenExecutionResult result, MavenLog mavenLog)
      throws IOException {
    assertThat(result).isSuccessful();

    // Will read the stdout log file and removes the prefix "[WARNING] "
    // assertThat(result).out().warn().containsExactly("Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!");
    // tag::warning[]
    assertThat(result)
        .out()
        .warn()
        .containsExactly("Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!");
    // end::warning[]

    // Will read the stdout log file and removes the prefix "[INFO] "
    // tag::stdout[]
    assertThat(result)
        .out()
        .info()
        .contains("Building Maven Integration Test :: it0033 1.0");
    // end::stdout[]

    // This will combine the check for a successful build with the check for the log file to contain
    // appropriate things.
    assertThat(result).isSuccessful().out().info().contains("Building Maven Integration Test :: it0033 1.0");

    // Will read the stdout log file and removes the prefix "[DEBUG] " so you need to check only the remaining part of the lines.
    assertThat(result).out().debug().contains("Populating class realm maven.api");

    // You can access the output (stdout) of the maven build directly and do things yourself.
    // tag::selfmade[]
    assertThat(Files.lines(mavenLog.getStdout()))
        .filteredOn(s1 -> s1.startsWith("[WARNING]"))
        .first()
        .isEqualTo("[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!");

    // You can access the output (stderr) of the maven build directly and do things yourself.
    assertThat(Files.lines(mavenLog.getStderr()))
        .isEmpty();
    // end::selfmade[]

    // Will read the stderr log file and check the given things.
    // tag::error[]
    assertThat(result).err().plain().isEmpty();
    // end::error[]

  }

  @MavenTest
  @EnabledOnOs(OS.WINDOWS)
  void basic_windows(MavenExecutionResult result, MavenLog mavenLog)
      throws IOException {
    assertThat(result).isSuccessful();

    // Will read the stdout logfile and removes the prefix "[WARNING] "
    assertThat(result).out().warn().containsExactly("Using platform encoding (Cp1252 actually) to copy filtered resources, i.e. build is platform dependent!");

    // Will read the stdout logfile and removes the prefix "[INFO] "
    assertThat(result).out().info().contains("Building Maven Integration Test :: it0033 1.0");

    // Will read the stdout logfile and removes the prefix "[DEBUG] " so you need to check only the remaining part of the lines.
    assertThat(result).out().debug().contains("Populating class realm maven.api");

    // You can access the output (stdout) of the maven build directly and do things yourself.
    assertThat(Files.lines(mavenLog.getStdout()))
        .filteredOn(s -> s.startsWith("[WARNING]")).containsExactly("[WARNING] Using platform encoding (Cp1252 actually) to copy filtered resources, i.e. build is platform dependent!");

    // Will read the stderr log file and check the given things.
    assertThat(result).err().plain().isEmpty();
  }

}
