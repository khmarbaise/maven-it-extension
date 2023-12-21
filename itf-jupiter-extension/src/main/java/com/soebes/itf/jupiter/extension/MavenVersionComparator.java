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

import org.apiguardian.api.API;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * @author Karl Heinz Marbaise
 */
@API(status = INTERNAL, since = "0.13.0")
public final class MavenVersionComparator implements Comparable<MavenVersionComparator> {
  private static final int NOT_DEFINED = -1;
  private static final String NOT_DEFINE_QUALIFIER = "";
  private static final String NOT_DEFINED_STRING = "ND";
  private final int major;
  private final int minor;
  private final int patch;
  private final String qualifier;
  private final int qualifierNumber;

  private MavenVersionComparator(int major, int minor, int patch, String qualifier, int qualifierNumber) {
    this.major = major;
    this.minor = minor;
    this.patch = patch;
    this.qualifier = Objects.requireNonNull(qualifier, "qualifier is not allowed to be null.");
    this.qualifierNumber = qualifierNumber;
  }

  /**
   * Define a {@link MavenVersionComparator} which identifies any version to be equal to it.
   * @return {@link MavenVersionComparator}.
   */
  public static MavenVersionComparator of() {
    return MavenVersionComparator.of(NOT_DEFINED);
  }

  /**
   * Define a {@link MavenVersionComparator} which identifies only the major version.
   * Example: {@code 3}. That means in consequence that the following version will
   * be seen as equal:
   * <ul>
   *   <li>3 == 3.6.0 </li>
   *   <li>3 == 3.1.1 </li>
   *   <li>3 == 3.1.1-alpha-1 </li>
   * </ul>
   * That means you are only interested in the major version.
   *
   * @param major The major version.
   * @return {@link MavenVersionComparator}.
   */
  public static MavenVersionComparator of(int major) {
    return MavenVersionComparator.of(major, NOT_DEFINED);
  }

  /**
   * Define a {@link MavenVersionComparator} which identifies only the major and minor version.
   * Example: {@code 3.6}. That means in consequence that the following version will
   * be seen as equal:
   * <ul>
   *   <li>3.6 == 3.6.0 </li>
   *   <li>3.6 == 3.6.15 </li>
   *   <li>3.6 == 3.6.15-alpha-2 </li>
   * </ul>
   * That means you are only interested in the major and minor version.
   * @param major The major version.
   * @param minor The minor version.
   * @return {@link MavenVersionComparator}.
   */
  public static MavenVersionComparator of(int major, int minor) {
    return MavenVersionComparator.of(major, minor, NOT_DEFINED);
  }

  /**
   * Define a {@link MavenVersionComparator} which identifies only the major, minor and patch version.
   * Example: {@code 3.6.1}.
   * @param major The major version.
   * @param minor The minor version.
   * @param patch The patch version.
   * @return {@link MavenVersionComparator}.
   */
  public static MavenVersionComparator of(int major, int minor, int patch) {
    return MavenVersionComparator.of(major, minor, patch, NOT_DEFINE_QUALIFIER, NOT_DEFINED);
  }

  /**
   * Define a {@link MavenVersionComparator} which identifies only the major, minor, patch, qualifier and qualifierNumber.
   * Example: {@code 3.6.1-alpha-1}.
   *
   * @param major The major version.
   * @param minor The minor version.
   * @param patch The patch version.
   * @param qualifier The qualifier version.
   * @param qualifierNumber The qualifierNumber version.
   * @return {@link MavenVersionComparator}.
   */
  public static MavenVersionComparator of(int major, int minor, int patch, String qualifier, int qualifierNumber) {
    return new MavenVersionComparator(major, minor, patch, qualifier, qualifierNumber);
  }

  public int major() {
    return major;
  }

  public int minor() {
    return minor;
  }

  public int patch() {
    return patch;
  }

  public String qualifier() {
    return qualifier;
  }

  public int qualifierNumber() {
    return qualifierNumber;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    MavenVersionComparator that = (MavenVersionComparator) obj;
    return this.major == that.major &&
           this.minor == that.minor &&
           this.patch == that.patch &&
           Objects.equals(this.qualifier, that.qualifier) &&
           this.qualifierNumber == that.qualifierNumber;
  }

  @Override
  public int hashCode() {
    return Objects.hash(major, minor, patch, qualifier, qualifierNumber);
  }

  @Override
  public String toString() {
    String q = this.qualifier.isEmpty() ? "" : "-" + this.qualifier + "-" + qualifierNumber;
    String majorStr = this.major == NOT_DEFINED ? NOT_DEFINED_STRING : Integer.toString(this.major);
    String minorStr = this.minor == NOT_DEFINED ? NOT_DEFINED_STRING : Integer.toString(this.minor);
    String patchStrg = this.patch == NOT_DEFINED ? NOT_DEFINED_STRING : Integer.toString(this.patch);
    return "{" + majorStr + "." + minorStr + "." + patchStrg + q + "}";
  }

  /**
   * This defines the order for qualifiers:
   * <ol>
   *   <li>alpha</li>
   *   <li>beta</li>
   *   <li>rc</li>
   * </ol>
   * That means based on the time line, {@code beta} comes before {@code rc} while {@code alpha} comes before {@code beta}.
   */
  private static final List<String> QUALIFIER_LIST = Arrays.asList(
      "alpha",
      "beta",
      "rc"
  );

  @Override
  public int compareTo(MavenVersionComparator rhs) {
    if (this.major() == NOT_DEFINED || rhs.major() == NOT_DEFINED) {
      return 0;
    }

    int majorComparison = Integer.compare(this.major(), rhs.major());
    if (majorComparison != 0) {
      return majorComparison;
    }

    if (this.minor() == NOT_DEFINED || rhs.minor() == NOT_DEFINED) {
      return 0;
    }

    int minorComparison = Integer.compare(this.minor(), rhs.minor());
    if (minorComparison != 0) {
      return minorComparison;
    }

    if (this.patch() == NOT_DEFINED || rhs.patch() == NOT_DEFINED) {
      return 0;
    }

    int patchComparison = Integer.compare(this.patch(), rhs.patch());
    if (patchComparison != 0) {
      return patchComparison;
    }

    boolean lhsContained = QUALIFIER_LIST.contains(this.qualifier());
    boolean rhsContained = QUALIFIER_LIST.contains(rhs.qualifier());
    if (lhsContained && rhsContained) {
      int qualifierComparison = Integer.compare(QUALIFIER_LIST.indexOf(this.qualifier()), QUALIFIER_LIST.indexOf(rhs.qualifier()));
      if (qualifierComparison != 0) {
        return qualifierComparison;
      }
      return Integer.compare(this.qualifierNumber(), rhs.qualifierNumber());
    } else {
      return lhsContained ? -1 : rhsContained ? +1 : Boolean.compare(this.qualifier().isEmpty(), rhs.qualifier().isEmpty());
    }
  }
}
