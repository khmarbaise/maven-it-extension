package com.soebes.itf.extension.assertj;

import com.soebes.itf.jupiter.maven.MavenCacheResult;
import com.soebes.itf.jupiter.maven.MavenExecutionResult;
import com.soebes.itf.jupiter.maven.MavenLog;
import com.soebes.itf.jupiter.maven.MavenProjectResult;

/**
 * Entry point to all Maven specific assertions needed in integration testing.
 *
 * @author Karl Heinz Marbaise
 */
public class MavenITAssertions {

  public static MavenExecutionResultAssert assertThat(MavenExecutionResult actual) {
    return new MavenExecutionResultAssert(actual);
  }

  public static MavenProjectResultAssert assertThat(MavenProjectResult actual) {
    return new MavenProjectResultAssert(actual);
  }

  public static MavenLogAssert assertThat(MavenLog actual) {
    return new MavenLogAssert(actual);
  }

  public static MavenCacheResultAssert assertThat(MavenCacheResult actual) {
    return new MavenCacheResultAssert(actual);
  }
}
