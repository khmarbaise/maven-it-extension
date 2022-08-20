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

import com.soebes.itf.jupiter.maven.MavenCacheResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

/**
 * @author Karl Heinz Marbaise
 */
@ExtendWith(MockitoExtension.class)
class MavenCacheResultAssertTest {

  @Mock
  private MavenCacheResult mavenCacheResult;

  @InjectMocks
  private MavenCacheResultAssert mavenCacheResultAssert;

  @Test
  @SuppressWarnings("deprecation")
  void equals_should_throw_unsupported_operation_exception() {
    assertThatExceptionOfType(UnsupportedOperationException.class)
        .isThrownBy(() -> mavenCacheResultAssert.equals(null))
        .withMessage("'equals' is not supported...maybe you intended to call 'isEqualTo'");
  }

  @Test
  void hashcode_should_return_minus_one() {
    assertThat(mavenCacheResultAssert.hashCode()).isEqualTo(1);
  }

}