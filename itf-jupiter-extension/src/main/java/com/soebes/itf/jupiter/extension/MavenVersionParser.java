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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.soebes.itf.jupiter.extension.MavenVersionComparator.of;
import static org.apiguardian.api.API.Status.INTERNAL;

/**
 * @author Karl Heinz Marbaise
 */
@API(status = INTERNAL, since = "0.13.0")
public class MavenVersionParser {

  private static final Pattern QUALIFIER_PATTERN = Pattern.compile("(\\d+)-(\\w+)-(\\d+)");

  private MavenVersionParser() {
  }

  /**
   * @param version The given version as String.
   * @return {@link MavenVersionComparator}
   */
  static MavenVersionComparator parseVersion(String version) {
    String[] split = Pattern.compile("\\.").split(version);
    String majorStr = split[0];
    String minorStr = split[1];
    String qualifierStr = split[2];

    Matcher qualifierMatcher = QUALIFIER_PATTERN.matcher(qualifierStr);
    if (qualifierMatcher.matches()) {
      int major = Integer.parseInt(majorStr);
      int minor = Integer.parseInt(minorStr);
      int patch = Integer.parseInt(qualifierMatcher.group(1));
      String qualifier = qualifierMatcher.group(2);
      int qualifierNumber = Integer.parseInt(qualifierMatcher.group(3));
      return of(major, minor, patch, qualifier, qualifierNumber);
    } else {
      int major = Integer.parseInt(majorStr);
      int minor = Integer.parseInt(minorStr);
      int patch = Integer.parseInt(qualifierStr);
      return of(major, minor, patch);
    }
  }
}
