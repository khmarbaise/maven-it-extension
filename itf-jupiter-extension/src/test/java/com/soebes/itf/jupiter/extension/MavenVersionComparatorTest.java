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
import static com.soebes.itf.jupiter.extension.MavenVersionComparatorTest.Compare.EQUALS;
import static com.soebes.itf.jupiter.extension.MavenVersionComparatorTest.Compare.GREATER_THAN;
import static com.soebes.itf.jupiter.extension.MavenVersionComparatorTest.Compare.LESS_THAN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;

/**
 * @author Karl Heinz Marbaise
 */
class MavenVersionComparatorTest {

  static Stream<Arguments> compareOrderOfGivenVersions() {
    return Stream.of(
        arguments(of(1, 2, 0), of(1, 2, 1), LESS_THAN),
        arguments(of(1, 3, 0), of(1, 4, 0), LESS_THAN),
        arguments(of(2, 1, 3), of(2, 0, 3), GREATER_THAN),
        arguments(of(3, 6, 1, "alpha", 1), of(3, 6, 1, "alpha", 2), LESS_THAN),
        arguments(of(3, 6, 1, "alpha", 1), of(3, 6, 1, "alpha", 1), EQUALS),
        arguments(of(3, 6, 1, "alpha", 15), of(3, 6, 1, "alpha", 1), GREATER_THAN),
        arguments(of(3, 6, 1, "rc", 1), of(3, 6, 1, "alpha", 1), GREATER_THAN),
        arguments(of(3, 6, 1, "gamma", 1), of(3, 6, 1, "alpha", 1), GREATER_THAN),
        arguments(of(3, 6, 1, "gamma", 1), of(3, 6, 1), LESS_THAN),
        arguments(of(3, 6, 1), of(3, 6, 1, "delta", 1), GREATER_THAN),
        arguments(of(1, 0, 0, "alpha", 1), of(1, 0, 0, "alpha", 2), LESS_THAN),
        arguments(of(1, 0, 0, "beta", 1), of(1, 0, 0, "alpha", 2), GREATER_THAN),
        arguments(of(1, 0, 0, "rc", 1), of(1, 0, 0, "beta", 2), GREATER_THAN),
        arguments(of(1, 0, 0), of(1, 0, 0, "rc", 1), GREATER_THAN),
        arguments(of(1, 0), of(1, 0, 0), EQUALS),
        arguments(of(1, 0), of(1, 0, 0, "alpha", 2), EQUALS),
        arguments(of(1, 0), of(1, 0, 15, "alpha", 2), EQUALS),
        arguments(of(1, 0), of(1, 0, 1), EQUALS),
        arguments(of(1, 0), of(1, 0, 5), EQUALS),
        arguments(of(1, 0), of(1, 0, 20), EQUALS),
        arguments(of(1, 1), of(1, 0, 0), GREATER_THAN),
        arguments(of(1, 6), of(1, 0, 0), GREATER_THAN),
        arguments(of(1), of(1, 0, 0), EQUALS),
        arguments(of(1), of(1, 5, 0), EQUALS),
        arguments(of(1), of(1, 5, 0, "rc", 1), EQUALS),
        arguments(of(1), of(1, 200, 1), EQUALS),
        arguments(of(2), of(1, 200, 1), GREATER_THAN),
        arguments(of(), of(1), EQUALS),
        arguments(of(), of(1, 200), EQUALS),
        arguments(of(), of(1, 200, 1), EQUALS),
        arguments(of(), of(1, 200, 1, "rc", 1), EQUALS));
  }

  @ParameterizedTest(name = "[{index}]: {0} {2} {1}")
  @MethodSource
  void compareOrderOfGivenVersions(MavenVersionComparator mavenVersionComparator1, MavenVersionComparator mavenVersionComparator2, Compare expectedComparable) {
    var compare = mavenVersionComparator1.compareTo(mavenVersionComparator2);
    assertThat(compare).isEqualTo(expectedComparable.getCompareResult());
  }

  enum Compare {
    LESS_THAN(-1),
    GREATER_THAN(1),
    EQUALS(0);

    private final int compareResult;

    Compare(int compareResult) {
      this.compareResult = compareResult;
    }

    public int getCompareResult() {
      return compareResult;
    }

    @Override
    public String toString() {
      return switch (this) {
        case LESS_THAN -> "<";
        case GREATER_THAN -> ">";
        case EQUALS -> "=";
      };
    }
  }

}
