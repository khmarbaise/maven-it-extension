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

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.soebes.itf.jupiter.extension.Preconditions.requireGreaterZero;
import static com.soebes.itf.jupiter.extension.Preconditions.requireNotEmpty;
import static com.soebes.itf.jupiter.extension.Preconditions.requireNotNull;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

/**
 * @author Karl Heinz Marbaise
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PreconditionsTest {

  @Nested
  class RequireNotNull {
    @Test
    void should_raise_illegal_argument_exception_while_giving_null() {
      assertThatIllegalArgumentException()
          .isThrownBy(() -> requireNotNull(null, "Failuremessage"))
          .withMessage("Failuremessage");

    }

    @Test
    void should_not_raise_a_failure_while_giving_non_null() {
      assertThatCode(() -> requireNotNull(Void.class, "Failuremessage"))
          .doesNotThrowAnyException();
    }
  }

  @Nested
  class RequireGreaterZero {
    @Test
    void should_raise_illegal_argument_exception_while_given_zero_size_array() {
      assertThatIllegalArgumentException()
          .isThrownBy(() -> requireGreaterZero(new String[]{}, "Failuremessage"))
          .withMessage("Failuremessage");

    }

    @Test
    void should_not_raise_a_failure_while_giving_one_size_array() {
      assertThatCode(() -> requireNotNull(new String[]{"1"}, "Failuremessage"))
          .doesNotThrowAnyException();
    }
  }

  @Nested
  class RequireNotEmpty {
    @Test
    void should_raise_illegal_argument_exception_while_given_empty_string() {
      assertThatIllegalArgumentException()
          .isThrownBy(() -> requireNotEmpty("", "Failuremessage"))
          .withMessage("Failuremessage");

    }

    @Test
    void should_not_raise_a_failure_while_giving_a_non_empty_string() {
      assertThatCode(() -> requireNotNull("1", "Failuremessage"))
          .doesNotThrowAnyException();
    }
  }
}