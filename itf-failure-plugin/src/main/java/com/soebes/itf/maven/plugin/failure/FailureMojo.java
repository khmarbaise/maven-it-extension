package com.soebes.itf.maven.plugin.failure;

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

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

/**
 * The intention of this mojo is to fail the build in relationship with
 * integration tests.
 *
 * @author @author <a href="mailto:khmarbaise@apache.org">Karl Heinz Marbaise</a>
 */
@Mojo(name = "failure", defaultPhase = LifecyclePhase.NONE,
    requiresDependencyResolution = ResolutionScope.NONE, threadSafe = true)
public class FailureMojo extends AbstractMojo {

  @Parameter(defaultValue = "false")
  private boolean executionException;

  @Parameter(defaultValue = "false")
  private boolean failureException;

  @Parameter(defaultValue = "Exception")
  private String exception;

  public void execute() throws MojoExecutionException, MojoFailureException {
    if (executionException) {
      throw new MojoExecutionException(exception);
    }
    if (failureException) {
      throw new MojoFailureException(exception);
    }

    getLog().warn("Neither executionException nor failureException has been set.");
  }

}
