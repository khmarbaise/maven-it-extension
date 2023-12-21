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

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static com.soebes.itf.jupiter.extension.MavenVersionComparator.of;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Karl Heinz Marbaise
 */
class MavenVersionParserTest {

  static Stream<Arguments> givenVersion() {
    return Stream.of(
        arguments("0.0.1", of(0, 0, 1)),
        arguments("1.0.1", of(1, 0, 1)),
        arguments("1.2.1", of(1, 2, 1)),
        arguments("1.3.0", of(1, 3, 0)),
        arguments("2.1.3", of(2, 1, 3)),
        arguments("3.6.1-alpha-1", of(3, 6, 1, "alpha", 1)),
        arguments("3.6.1-delta-1", of(3, 6, 1, "delta", 1)),
        arguments("3.6.1-alpha-15", of(3, 6, 1, "alpha", 15)));
  }

  @ParameterizedTest
  @MethodSource
  void givenVersion(String givenMavenVersion, MavenVersionComparator expectedVersion) {
    var givenParsedVersion = MavenVersionParser.parseVersion(givenMavenVersion);
    assertThat(givenParsedVersion).isEqualTo(expectedVersion);
  }

}
