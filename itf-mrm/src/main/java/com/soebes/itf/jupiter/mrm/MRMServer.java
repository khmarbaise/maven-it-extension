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

import org.codehaus.mojo.mrm.api.FileSystem;
import org.codehaus.mojo.mrm.impl.CompositeFileSystem;

import java.net.URI;
import java.util.UUID;

/**
 * Mock Repository Manager.
 *
 * @author Karl Heinz Marbaise
 */
public class MRMServer {


  private final RepoContainer repoContainer;
  private FileSystemServer mrm;
  private final int port;

  public MRMServer(RepoContainer repoContainer, int port) {
    this.repoContainer = repoContainer;
    this.port = port;
  }

  protected String getSettingsServletPath() {
    return null;
  }

  private FileSystemServer createFileSystemServer(FileSystem artifactStore) {
    //FIXME: Might need to change
    return new FileSystemServer(UUID.randomUUID().toString(),
        //Port should be done globally to prevent collisions.
        this.port, artifactStore, getSettingsServletPath());
  }

  //FIXME: Reconsider returning here?
  public FileSystemServer create() {
    Storage storage = new Storage(this.repoContainer);

    CompositeFileSystem into = storage.into();
    this.mrm = createFileSystemServer(into);
    return mrm;
  }

  public void start() throws Exception {
    this.mrm.ensureStarted();
  }
  public void shutdown() throws InterruptedException {
    this.mrm.finish();
  }

  public URI getURI() {
    return URI.create(this.mrm.getUrl());
  }
}
