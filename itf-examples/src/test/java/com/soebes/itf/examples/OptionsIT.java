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

import com.soebes.itf.jupiter.extension.MavenGoal;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenOption;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.extension.SystemProperty;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;

import static com.soebes.itf.extension.assertj.MavenITAssertions.assertThat;
import static com.soebes.itf.jupiter.extension.MavenCLIOptions.DEBUG;
import static com.soebes.itf.jupiter.extension.MavenCLIOptions.LOG_FILE;

@MavenJupiterExtension
@MavenGoal("verify")
class OptionsIT {

  // mvn -X --log-file test.log
  // The log file is located within the project directory.
  @MavenTest
  @MavenOption(DEBUG)
  @MavenOption(value = LOG_FILE, parameter = "test.log")
  void first_integration_test(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

  // Using two system properties -Daccent=true -Dgolem=three
  @MavenTest
  @SystemProperty(value = "accent", content = "bean")
  @SystemProperty(value = "golem", content = "three")
  @SystemProperty("skipTests")
//  @SystemProperties(value =
//      @SystemProperty(value = "first", content = "one"),
//      @SystemProperty(value = "second", content = "two"),
//      @SystemProperty(value = "third", content =   "trhee")
//  })
  @SystemProperty("skipTests")
  @MavenGoal("install")
  void second_integration_test(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

}