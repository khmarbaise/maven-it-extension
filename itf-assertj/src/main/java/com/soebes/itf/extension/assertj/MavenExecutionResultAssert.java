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

import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import org.apiguardian.api.API;
import org.assertj.core.api.AbstractAssert;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * @author Karl Heinz Marbaise
 */
@API(status = EXPERIMENTAL, since = "0.8.0")
public class MavenExecutionResultAssert extends AbstractAssert<MavenExecutionResultAssert, MavenExecutionResult> {

  protected MavenExecutionResultAssert(MavenExecutionResult actual) {
    super(actual, MavenExecutionResultAssert.class);
  }

  /**
   * An entry point for MavenExecutionResultAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(result).isSuccessful();</code>
   *
   * @param actual the MavenExecutionResult we want to make assertions on.
   * @return a new {@link MavenExecutionResultAssert}
   */
  public static MavenExecutionResultAssert assertThat(MavenExecutionResult actual) {
    return new MavenExecutionResultAssert(actual);
  }

  /**
   * This will give you access to the {@code stdout} of the Maven build.
   *
   * @return Stdout
   */
  public LogAssert out() {
    isNotNull();
    return new LogAssert(new LogClass(this.actual.getMavenLog().getStdout()));
  }

  /**
   * This will give you access to the {@code stderr} of the Maven build.
   * @return Std Err
   */
  public LogAssert err() {
    isNotNull();
    return new LogAssert(new LogClass(this.actual.getMavenLog().getStderr()));
  }

  public MavenProjectResultAssert project() {
    isNotNull();
    return new MavenProjectResultAssert(this.actual.getMavenProjectResult());
  }

  public MavenCacheResultAssert cache() {
    isNotNull();
    return new MavenCacheResultAssert(this.actual.getMavenCacheResult());
  }

  /**
   * @return {@link MavenExecutionResultAssert} for method chaining.
   * @throws AssertionError if the actual value is {@code null}.
   * @throws AssertionError if the actual value is not {@code true}.
   */
  public MavenExecutionResultAssert isSuccessful() {
    isNotNull();
    if (!this.actual.isSuccesful()) {
      failWithMessage("The build was not successful but was <%s> with returnCode:<%s>", actual.getResult(),
          actual.getReturnCode());
    }
    return myself;
  }

  /**
   * @return {@link MavenExecutionResultAssert} for method chaining.
   * @throws AssertionError if the actual value is {@code null}.
   * @throws AssertionError if the actual value is not {@code true}.
   */
  public MavenExecutionResultAssert isFailure() {
    isNotNull();
    if (!this.actual.isFailure()) {
      failWithMessage("The build should be not successful but was <%s> with returnCode:<%s>", actual.getResult(),
          actual.getReturnCode());
    }
    return myself;
  }
}
