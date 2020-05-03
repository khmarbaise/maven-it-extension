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

import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenProject;
import com.soebes.itf.jupiter.extension.MavenTest;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.TestMethodOrder;

@MavenJupiterExtension
class MavenProjectIT {

  @MavenTest
  void packaging_includes(MavenExecutionResult result) {
  }

  @Nested
  @TestMethodOrder(OrderAnnotation.class)
  @MavenProject("test_project")
  class NestedExample {

    @MavenTest(goals = "clean")
    @Order(10)
    void basic(MavenExecutionResult result) {
      System.out.println("(basic) result = " + result);
    }

    @MavenTest(goals = "install")
    @Order(20)
    void packaging_includes(MavenExecutionResult result) {
      System.out.println("(packaging_includes) result = " + result);
    }

  }


}
