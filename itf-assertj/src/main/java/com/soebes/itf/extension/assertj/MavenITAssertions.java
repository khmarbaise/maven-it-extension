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
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenLog;
import com.soebes.itf.jupiter.maven.MavenProjectResult;

/**
 * Entry point to all Maven specific assertions needed in integration testing.
 *
 * @author Karl Heinz Marbaise
 */
public class MavenITAssertions {

  private MavenITAssertions() {
    // prevent instantiation.
  }

  public static MavenExecutionResultAssert assertThat(MavenExecutionResult actual) {
    return new MavenExecutionResultAssert(actual);
  }

  public static MavenProjectResultAssert assertThat(MavenProjectResult actual) {
    return new MavenProjectResultAssert(actual);
  }

  public static MavenLogAssert assertThat(MavenLog actual) {
    return new MavenLogAssert(actual);
  }

  public static MavenCacheResultAssert assertThat(MavenCacheResult actual) {
    return new MavenCacheResultAssert(actual);
  }
}
