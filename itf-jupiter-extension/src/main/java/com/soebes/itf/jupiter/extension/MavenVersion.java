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

import java.util.stream.Stream;

import static com.soebes.itf.jupiter.extension.Preconditions.requireNotEmpty;
import static com.soebes.itf.jupiter.extension.Preconditions.requireNotNull;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * @author Karl Heinz Marbaise
 */
@API(status = EXPERIMENTAL, since = "0.1.0")
public enum MavenVersion {
  UNKNOWN("UNKOWN"), //TODO: Need to reconsider?
  M3_0_5("3.0.5"),
  M3_1_1("3.1.1"),
  M3_2_5("3.2.5"),
  M3_3_1("3.3.1"),
  M3_3_9("3.3.9"),
  M3_5_0("3.5.0"),
  M3_5_2("3.5.2"),
  M3_5_3("3.5.3"),
  M3_5_4("3.5.4"),
  M3_6_0("3.6.0"),
  M3_6_1("3.6.1"),
  M3_6_2("3.6.2"),
  M3_6_3("3.6.3"),
  M3_8_1("3.8.1"),
  M3_8_2("3.8.2"),
  M3_8_3("3.8.3"),
  M3_8_4("3.8.4"),
  M3_8_5("3.8.5"),
  M3_8_6("3.8.6"),
  M3_8_7("3.8.7"),
  M3_9_0("3.9.0");

  private static final MavenVersion CURRENT_MAVEN_VERSION = determineCurrentVersion();

  private final String versionString;

  MavenVersion(String versionString) {
    this.versionString = versionString;
  }

  private static MavenVersion determineCurrentVersion() {
    String currentVersion = requireNotNull(System.getProperty("maven.version"), "JVM system property 'maven.version' is empty.");
    requireNotEmpty(currentVersion, "JVM system property 'maven.version' is empty. The maven version can not being detected.");
    return Stream.of(values())
        .filter(mavenVersion -> mavenVersion.getVersionString().equals(currentVersion))
        .findFirst()
        .orElseGet(() -> MavenVersion.UNKNOWN);
  }

  /**
   * @return {@code true} if <em>this</em> {@link MavenVersion} is known to be the Maven version for the currently being
   * executed Maven version.
   */
  public boolean isCurrentVersion() {
    return this == CURRENT_MAVEN_VERSION;
  }

  public String getVersionString() {
    return versionString;
  }
}
