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
import org.codehaus.mojo.mrm.impl.digest.AutoDigestFileSystem;
import org.codehaus.mojo.mrm.impl.maven.ArtifactStoreFileSystem;
import org.codehaus.mojo.mrm.impl.maven.DiskArtifactStore;

import java.io.File;
import java.util.function.Function;

/**
 * @author Karl Heinz Marbaise
 */
public class Storage {

  private static final Function<File, DiskArtifactStore> toDiskArtifactStore = DiskArtifactStore::new;
  private static final Function<DiskArtifactStore, ArtifactStoreFileSystem> toArtifactStoreFileSystem = ArtifactStoreFileSystem::new;
  private static final Function<ArtifactStoreFileSystem, AutoDigestFileSystem> toAutoDigestFileSystem = AutoDigestFileSystem::new;
  private static final Function<FileSystem[], CompositeFileSystem> toCompositeFileSystem = CompositeFileSystem::new;

  private final RepoContainer repoContainer;

  public Storage(RepoContainer repoContainer) {
    this.repoContainer = repoContainer;
  }

  public CompositeFileSystem into() {
    FileSystem[] fileSystems = repoContainer.getRepositories()
        .stream()
        .map(toDiskArtifactStore.andThen(toArtifactStoreFileSystem).andThen(toAutoDigestFileSystem))
        .toArray(FileSystem[]::new);
    return toCompositeFileSystem.apply(fileSystems);
  }

}
