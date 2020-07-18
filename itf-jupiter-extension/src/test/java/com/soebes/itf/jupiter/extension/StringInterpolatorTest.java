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

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.soebes.itf.jupiter.extension.StringInterpolator.interpolate;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Unit test for {@link StringInterpolator}.
 *
 * @author Karl Heinz Marbaise
 */
class StringInterpolatorTest {

  @Test
  void interpolate_should_replace_a_single_placeholder() {
    Map<String, String> mapping = Collections.singletonMap("key", "life");

    List<String> of = Arrays.asList("the answer to ${key}, universe and everything.");

    assertThat(of.stream().map(interpolate(mapping))).containsExactly("the answer to life, universe and everything.");
  }

  @Test
  void interpolate_should_interpolate_two_placeholders() {
    Map<String, String> mapping = new HashMap<>();
    mapping.put("key", "life");
    mapping.put("value", "everything");

    List<String> givenList = Arrays.asList("the answer to ${key}, universe and ${value}.");

    assertThat(givenList.stream().map(interpolate(mapping))).containsExactly("the answer to life, universe and everything.");
  }

  @Test
  void interpolate_should_return_the_original_if_there_is_an_unknown_key() {
    Map<String, String> mapping = new HashMap<>();
    mapping.put("key", "givenList the key");
    mapping.put("value", "wrongValue");

    List<String> givenList = Arrays.asList("The name givenList the key will replace the ${wrongKey}.");

    assertThat(givenList.stream().map(interpolate(mapping))).containsExactly(givenList.toArray(new String[]{}));
  }

  @Test
  void interpolate_an_empty_string() {
    Map<String, String> mapping = Collections.singletonMap("key", "TheValue");

    List<String> givenList = Arrays.asList("");
    assertThat(givenList.stream().map(interpolate(mapping))).containsExactly("");
  }

  @Nested
  class NullChecks {

    @Test
    void constructor_fails_with_null_pointer_exception_while_using_null_as_parameter() {
      assertThatNullPointerException().isThrownBy(() -> interpolate(null))
          .withMessage("mapping is not allowed to be null.");
    }

  }

}
