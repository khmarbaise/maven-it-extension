package com.soebes.itf.extension.assertj;

import org.assertj.core.api.AbstractAssert;

import com.soebes.itf.jupiter.maven.MavenCacheResult;

/**
 * @author Karl Heinz Marbaise
 */
public class MavenCacheResultAssert extends AbstractAssert<MavenCacheResultAssert, MavenCacheResult> {

  protected MavenCacheResultAssert(MavenCacheResult actual) {
    super(actual, MavenCacheResultAssert.class);
  }

  /**
   * An entry point for MavenCacheResult to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(result).isSuccessful();</code>
   *
   * @param actual the MavenLog we want to make assertions on.
   * @return a new </code>{@link MavenCacheResultAssert}</code>
   */
  public static MavenCacheResultAssert assertThat(MavenCacheResult actual) {
    return new MavenCacheResultAssert(actual);
  }

}
