package org.apache.maven.jupiter.maven;

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

import java.util.StringJoiner;

/**
 * @author Karl Heinz Marbaise
 */
public class MavenExecutionResult {

  private final ExecutionResult result;

  private final int returnCode;

  private final MavenLog mavenLog;

  private final MavenProjectResult mavenProjectResult;

  private final MavenCacheResult mavenCacheResult;

  public MavenExecutionResult(ExecutionResult result, int returnCode, MavenLog mavenLog,
      MavenProjectResult mavenProjectResult, MavenCacheResult mavenCacheResult) {
    this.result = result;
    this.returnCode = returnCode;
    this.mavenLog = mavenLog;
    this.mavenProjectResult = mavenProjectResult;
    this.mavenCacheResult = mavenCacheResult;
  }

  public int getReturnCode() {
    return returnCode;
  }

  public boolean isSuccesful() {
    return ExecutionResult.Successful.equals(this.result);
  }

  public boolean isFailure() {
    return ExecutionResult.Failure.equals(this.result);
  }

  public boolean isError() {
    return ExecutionResult.Error.equals(this.result);
  }

  public ExecutionResult getResult() {
    return this.result;
  }

  public MavenLog getMavenLog() {
    return mavenLog;
  }

  public MavenCacheResult getMavenCacheResult() {
    return mavenCacheResult;
  }

  public MavenProjectResult getMavenProjectResult() {
    return mavenProjectResult;
  }

  public enum ExecutionResult {
    Successful,
    Failure,
    Error
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", MavenExecutionResult.class.getSimpleName() + "[", "]").add("result=" + result)
        .add("returnCode=" + returnCode)
        .add("mavenLog=" + mavenLog)
        .add("mavenProjectResult=" + mavenProjectResult)
        .add("mavenCacheResult=" + mavenCacheResult)
        .toString();
  }
}
