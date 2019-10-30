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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;
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

  List<String> convert(String[] inputList) {
    List<String> result = new ArrayList<>();
    for (String s : inputList) {
      String[] split = s.split("/");
      String tmp = split[0] + "/";
      result.add(tmp);
      for (int i = 1; i < split.length; i++) {
        System.out.println("i = " + i + " '" + split[i] + "'");
        tmp += split[i];
        result.add(tmp);
      }
    }
    return result;
  }

  @Test
  void name() {
    String[] files = {"META-INF/application.xml", "APP-INF/classes/foo.properties"};
    String[] expectedResult = {"META-INF/", "META-INF/application.xml", "APP-INF/", "APP-INF/classes/",
        "APP-INF/classes/foo.properties"};

    Stream.of(files).forEach(s -> System.out.println("s = " + s));

    List<String> actual = convert(files);
    assertThat(actual).containsExactly(expectedResult);
  }

  @Test
  void check_for_list() {
    String[] expectedResult = {"META-INF/", "META-INF/application.xml", "APP-INF/", "APP-INF/classes/",
        "APP-INF/classes/foo.properties"};

    assertThat(expectedResult).allSatisfy(s -> s.startsWith("APP-INF"))
        .contains("APP-INF/", "APP-INF/classes/", "APP-INF/classes/foo.properties");
  }
}
