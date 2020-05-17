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

import org.apache.maven.model.Model;

/**
 * This will read information from the {@link Model} which can be accessed
 * by other parts of the integration tests.
 *
 * @author Karl Heinz Marbaise
 */
class PomModelReader {

  private final Model model;

  PomModelReader(Model model) {
    this.model = model;
  }

  public String getGroupId() {
    if (this.model.getGroupId() != null) {
      return this.model.getGroupId();
    }
    //FIXME: this.model.getParent() == null what should be the consequence?
    return this.model.getParent().getGroupId();
  }

  public String getVersion() {
    if (this.model.getVersion() != null) {
      return this.model.getVersion();
    }
    //FIXME: this.model.getParent() == null what should be the consequence?
    return this.model.getParent().getVersion();
  }

  public String getArtifactId() {
    return this.model.getArtifactId();
  }
}
