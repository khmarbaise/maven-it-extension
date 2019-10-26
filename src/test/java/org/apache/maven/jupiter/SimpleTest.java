package org.apache.maven.jupiter;

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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Test;

class SimpleTest {

  @Test
  void firstTest() {
    List<String> earFiles = Arrays.asList("A", "B", "C", "D");
    List<String> x = Arrays.asList("B", "C");

    boolean b = x.stream().allMatch(s -> earFiles.stream().anyMatch(earFile -> earFile.equals(s)));
    System.out.println("b = " + b);

    assertThat(earFiles).doesNotContain("X").containsOnlyOnce("B", "C");
  }
}
