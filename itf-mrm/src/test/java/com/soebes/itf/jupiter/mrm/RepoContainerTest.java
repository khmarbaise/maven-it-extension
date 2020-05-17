package com.soebes.itf.jupiter.mrm;

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

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Karl Heinz Marbaise
 */
class RepoContainerTest {

  @Test
  void get_repositories_should_return_empty() {
    assertThat(new RepoContainer().getRepositories()).isEmpty();
  }

  @Test
  void get_repositories_should_return_single_entry() {
    File singleFile = new File(".");
    RepoContainer repoContainer = new RepoContainer().add(singleFile);
    assertThat(repoContainer.getRepositories()).containsExactly(singleFile);
  }
}