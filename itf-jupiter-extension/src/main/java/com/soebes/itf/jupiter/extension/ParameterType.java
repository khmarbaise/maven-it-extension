package com.soebes.itf.jupiter.extension;

import com.soebes.itf.jupiter.maven.MavenCacheResult;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenExecutor;
import com.soebes.itf.jupiter.maven.MavenLog;
import com.soebes.itf.jupiter.maven.MavenProjectResult;

/**
 * @author Karl Heinz Marbaise
 */
enum ParameterType {
  ExecutionResult(MavenExecutionResult.class),
  LogResult(MavenLog.class),
  CacheResult(MavenCacheResult.class),
  ProjectResult(MavenProjectResult.class),
  Executor(MavenExecutor.class);

  private Class<?> klass;

  ParameterType(Class<?> klass) {
    this.klass = klass;
  }

  Class<?> getKlass() {
    return klass;
  }
}
