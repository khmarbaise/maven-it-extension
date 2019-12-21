package org.apache.maven.jupiter.extension;

import java.io.File;
import org.apache.maven.jupiter.extension.maven.MavenCacheResult;
import org.apache.maven.jupiter.extension.maven.MavenExecutionResult;
import org.apache.maven.jupiter.extension.maven.MavenLog;
import org.apache.maven.jupiter.extension.maven.MavenProjectResult;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.ExtensionContext.Store;

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

  void safe(File baseDirectory, File mavenItBaseDirectory, File targetDirectory) {
    StorageHelper sh = new StorageHelper(context);
    sh.put(Storage.BASE_DIRECTORY, baseDirectory);
    sh.put(Storage.BASE_IT_DIRECTORY, mavenItBaseDirectory);
    sh.put(Storage.TARGET_DIRECTORY, targetDirectory);

  }
  void safe(MavenExecutionResult result, MavenLog log, MavenCacheResult mavenCacheResult, MavenProjectResult mavenProjectResult) {
    put(ParameterType.ExecutionResult + context.getUniqueId(), result);
    put(ParameterType.LogResult + context.getUniqueId(), log);
    put(ParameterType.CacheResult + context.getUniqueId(), mavenCacheResult);
    put(ParameterType.ProjectResult + context.getUniqueId(), mavenProjectResult);

  }
}