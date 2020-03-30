package org.apache.maven.it;

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

import static org.apache.maven.assertj.MavenITAssertions.assertThat;
import static org.apache.maven.jupiter.extension.MavenOptions.DEBUG;
import static org.apache.maven.jupiter.extension.MavenOptions.LOG_FILE;

import org.apache.maven.jupiter.extension.MavenIT;
import org.apache.maven.jupiter.extension.MavenTest;
import org.apache.maven.jupiter.maven.MavenExecutionResult;

@MavenIT
class OptionsIT {

  // mvn -X --log-file test.log
  // The log file is located within the project directory.
  @MavenTest(options = {DEBUG, LOG_FILE, "test.log"})
  void first_integration_test(MavenExecutionResult result) {
    assertThat(result).isSuccessful();
  }

}