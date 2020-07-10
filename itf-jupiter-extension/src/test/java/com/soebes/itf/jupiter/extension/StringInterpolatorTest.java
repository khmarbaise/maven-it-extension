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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

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

    StringInterpolator interpolator = new StringInterpolator(mapping);

    String givenString = "the answer to ${key}, universe and everything.";

    String interpolated = interpolator.interpolate(givenString);
    assertThat(interpolated).isEqualTo("the answer to life, universe and everything.");
  }

  @Test
  void interpolate_should_interpolate_two_placeholders() {
    Map<String, String> mapping = new HashMap<>();
    mapping.put("key", "life");
    mapping.put("value", "everything");

    StringInterpolator interpolator = new StringInterpolator(mapping);

    String givenString = "the answer to ${key}, universe and ${value}.";

    String interpolated = interpolator.interpolate(givenString);
    assertThat(interpolated).isEqualTo("the answer to life, universe and everything.");
  }

  @Test
  void interpolate_should_return_the_original_if_there_is_an_unknown_key() {
    Map<String, String> mapping = new HashMap<>();
    mapping.put("key", "of the key");
    mapping.put("value", "wrongValue");

    StringInterpolator interpolator = new StringInterpolator(mapping);

    String givenString = "The name of the key will replace the ${wrongKey}.";

    assertThat(interpolator.interpolate(givenString)).isEqualTo(givenString);
  }

  @Test
  void interpolate_an_empty_string() {
    Map<String, String> mapping = Collections.singletonMap("key", "TheValue");

    String interpolated = new StringInterpolator(mapping).interpolate("");
    assertThat(interpolated).isEmpty();
  }

  @Nested
  class NullChecks {

    @Test
    void constructor_fails_with_null_pointer_exception_while_using_null_as_parameter() {
      assertThatNullPointerException().isThrownBy(() -> new StringInterpolator(null))
          .withMessage("mapping is not allowed to be null.");
    }

    @Test
    void interplate_fails_with_null_pointer_exception_while_using_null_as_parameter() {
      StringInterpolator si = new StringInterpolator(Collections.emptyMap());
      assertThatNullPointerException().isThrownBy(() -> si.interpolate(null))
          .withMessage("stringToBeInterpolated is not allowed to be null.");
    }

  }

}
