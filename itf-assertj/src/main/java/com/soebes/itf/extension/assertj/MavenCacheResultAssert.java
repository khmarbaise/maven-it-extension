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
import org.apiguardian.api.API;
import org.assertj.core.api.AbstractAssert;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * @author Karl Heinz Marbaise
 */
@API(status = EXPERIMENTAL, since = "0.8.0")
public class MavenCacheResultAssert extends AbstractAssert<MavenCacheResultAssert, MavenCacheResult> {

  MavenCacheResultAssert(MavenCacheResult actual) {
    super(actual, MavenCacheResultAssert.class);
  }

  /**
   * An entry point for MavenCacheResult to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(result).isSuccessful();</code>
   *
   * @param actual the MavenLog we want to make assertions on.
   * @return a new {@link MavenCacheResultAssert}
   */
  public static MavenCacheResultAssert assertThat(MavenCacheResult actual) {
    return new MavenCacheResultAssert(actual);
  }

  /**
   * @deprecated use {@link #isEqualTo} instead
   *
   * @throws UnsupportedOperationException if this method is called.
   * @implNote java:S1133: Suppressing "Do not forget to remove this deprecated code someday." message.
   */
  @Override
  @Deprecated
  @SuppressWarnings("java:S1133")
  public boolean equals(Object obj) {
    throw new UnsupportedOperationException("'equals' is not supported...maybe you intended to call 'isEqualTo'");
  }

  /**
   * Always returns 1.
   *
   * @return 1.
   */
  @Override
  public int hashCode() {
    return 1;
  }
}
