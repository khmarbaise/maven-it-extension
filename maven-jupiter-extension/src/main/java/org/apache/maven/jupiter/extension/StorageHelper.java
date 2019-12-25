package org.apache.maven.jupiter.extension;

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

import java.io.File;
import org.apache.maven.jupiter.maven.MavenCacheResult;
import org.apache.maven.jupiter.maven.MavenExecutionResult;
import org.apache.maven.jupiter.maven.MavenLog;
import org.apache.maven.jupiter.maven.MavenProjectResult;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

/**
 * @author Karl Heinz Marbaise
 */
final class StorageHelper {

  private static final Namespace NAMESPACE_MAVEN_IT = Namespace.create(MavenITExtension.class);

  private final Store nameSpace;

  private final ExtensionContext context;

  StorageHelper(ExtensionContext context) {
    this.nameSpace = context.getStore(NAMESPACE_MAVEN_IT);
    this.context = context;
  }

  <V> V get(Storage storage, Class<V> requiredType) {
    return nameSpace.get(storage, requiredType);
  }

  <V> V get(Object key, Class<V> requiredType) {
    return nameSpace.get(key, requiredType);
  }

  void put(Object key, Object value) {
    nameSpace.put(key, value);
  }

  void save(File baseDirectory, File mavenItBaseDirectory, File targetDirectory) {
    StorageHelper sh = new StorageHelper(context);
    sh.put(Storage.BASE_DIRECTORY, baseDirectory);
    sh.put(Storage.BASE_IT_DIRECTORY, mavenItBaseDirectory);
    sh.put(Storage.TARGET_DIRECTORY, targetDirectory);
  }

  void save(MavenExecutionResult result, MavenLog log, MavenCacheResult mavenCacheResult,
      MavenProjectResult mavenProjectResult) {
    put(ParameterType.ExecutionResult + context.getUniqueId(), result);
    put(ParameterType.LogResult + context.getUniqueId(), log);
    put(ParameterType.CacheResult + context.getUniqueId(), mavenCacheResult);
    put(ParameterType.ProjectResult + context.getUniqueId(), mavenProjectResult);
  }
}