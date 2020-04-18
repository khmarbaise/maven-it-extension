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
import org.assertj.core.api.AbstractAssert;

/**
 * @author Karl Heinz Marbaise
 */
public class MavenCacheResultAssert extends AbstractAssert<MavenCacheResultAssert, MavenCacheResult> {

  protected MavenCacheResultAssert(MavenCacheResult actual) {
    super(actual, MavenCacheResultAssert.class);
  }

  /**
   * An entry point for MavenCacheResult to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(result).isSuccessful();</code>
   *
   * @param actual the MavenLog we want to make assertions on.
   * @return a new </code>{@link MavenCacheResultAssert}</code>
   */
  public static MavenCacheResultAssert assertThat(MavenCacheResult actual) {
    return new MavenCacheResultAssert(actual);
  }

}
