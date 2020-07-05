package com.soebes.itf.extension.assertj;

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

import com.soebes.itf.jupiter.maven.MavenLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

/**
 * @author Karl Heinz Marbaise
 */
class MavenLogAssertTest {

  private MavenLogAssert mavenLogAssert;

  @BeforeEach
   void beforeEach() throws URISyntaxException {
    URI stdoutURI = this.getClass().getResource("/mvn-stdout.log").toURI();
    URI stderrURI = this.getClass().getResource("/mvn-stderr.log").toURI();
    MavenLog mavenLog = new MavenLog(Paths.get(stdoutURI), Paths.get(stderrURI));
    this.mavenLogAssert = new MavenLogAssert(mavenLog);
  }

  @Test
  void warn_should_give_only_warning_lines_without_prefix_back() {
    mavenLogAssert.warn().containsExactly("Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!");
  }

  @Test
  void info_should_give_only_warning_lines_without_prefix_back() {
    mavenLogAssert.info().containsSequence("", "--- maven-ear-plugin:3.0.1:ear (default-ear) @ test ---");
  }

  @Test
  void debug_should_give_only_warning_lines_without_prefix_back() {
    mavenLogAssert.debug().containsSequence(
        "Configuring mojo org.apache.maven.plugins:maven-ear-plugin:3.0.1:ear from plugin realm ClassRealm[plugin>org.apache.maven.plugins:maven-ear-plugin:3.0.1, parent: sun.misc.Launcher$AppClassLoader@70dea4e]",
        "Configuring mojo 'org.apache.maven.plugins:maven-ear-plugin:3.0.1:ear' with basic configurator -->",
        "  (f) earSourceDirectory = /Users/khmarbaise/ws-git-soebes/maven-it-extension/itf-examples/target/maven-it/com/soebes/itf/examples/LogoutputIT/basic/project/src/main/application"
    );
  }

  @Test
  void plain_should_give_back_all_lines() {
    mavenLogAssert.plain().containsSequence(
        "[INFO] ",
        "[INFO] --- maven-ear-plugin:3.0.1:ear (default-ear) @ test ---",
        "[DEBUG] Configuring mojo org.apache.maven.plugins:maven-ear-plugin:3.0.1:ear from plugin realm ClassRealm[plugin>org.apache.maven.plugins:maven-ear-plugin:3.0.1, parent: sun.misc.Launcher$AppClassLoader@70dea4e]"
    );
  }

  @Test
  void plain_should_give_back_all_lines_second() {
    mavenLogAssert.plain().containsSequence(
        "[ERROR] Failure during execution.",
        "[DEBUG] adding entry META-INF/maven/org.apache.maven.its.ear.basic/test/pom.properties",
        "[INFO] ------------------------------------------------------------------------",
        "[INFO] BUILD SUCCESS",
        "[INFO] ------------------------------------------------------------------------"
    );
  }

  @Test
  void error_should_give_only_error_lines_without_prefix_back() {
    mavenLogAssert.error().containsExactly(
        "Failure during execution."
    );
  }

}