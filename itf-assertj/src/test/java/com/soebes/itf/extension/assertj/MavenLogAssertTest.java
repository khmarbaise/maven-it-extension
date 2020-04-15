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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

@Disabled("Currently not working.")
class MavenLogAssertTest {

  Stream<String> createLogStream() {
    InputStream resourceAsStream = this.getClass().getResourceAsStream("/mvn-stdout.out");
    return new BufferedReader(new InputStreamReader(resourceAsStream, Charset.defaultCharset())).lines();
  }

  @Test
  void first_test() {
//    return Paths.get(loggingDirectory.toString(), this.prefix + "-stdout.out");

    //MavenLogAssert mavenLogAssert = new MavenLogAssert();

    List<String> infoList = createLogStream().filter(p -> p.startsWith("[INFO]"))
        .map(s -> s.substring(7))
        .collect(Collectors.toList());

    List<String> debugList = createLogStream().filter(p -> p.startsWith("[DEBUG]"))
        .map(s -> s.substring(7))
        .collect(Collectors.toList());
    List<String> errorList = createLogStream().filter(p -> p.startsWith("[ERROR]"))
        .map(s -> s.substring(7))
        .collect(Collectors.toList());
    List<String> warningList = createLogStream().filter(p -> p.startsWith("[WARNING]"))
        .map(s -> s.substring(9))
        .collect(Collectors.toList());

    List<String> collect = infoList.stream()
        .filter(s -> !s.startsWith("Downloading from "))
        .filter(s -> !s.startsWith("Downloaded from "))
        .filter(s -> !s.isEmpty())
        .collect(Collectors.toList());
    //infoList.forEach(s -> System.out.println(s));
  }
}