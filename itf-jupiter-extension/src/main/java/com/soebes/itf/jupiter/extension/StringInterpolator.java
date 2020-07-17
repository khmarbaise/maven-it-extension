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

import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * @author Karl Heinz Marbaise
 */
@API(status = EXPERIMENTAL, since = "0.9.0")
public class StringInterpolator {

  /**
   * This pattern {@code ${..}} with the given group {@code (.*?)} is defined to extract the {@code ${key}} as
   * {@code key} which is needed to access the {@link #mapping}.
   */
  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile(
      Pattern.quote("${") + "(.*?)" + Pattern.quote("}"));

  private Map<String, String> mapping;

  /**
   * @param mapping the map which contains the mapping between key and value. {@code null is not allowed}
   * @throws IllegalArgumentException in case of giving {@code null} for {@code mapping}.
   */
  public StringInterpolator(Map<String, String> mapping) {
    this.mapping = Objects.requireNonNull(mapping, "mapping is not allowed to be null.");
  }

  /**
   * @param stringToBeInterpolated The string which will interpolated.
   * @return The interpolated string.
   * @throws IllegalArgumentException in case of giving {@code null} for {@code givenString}.
   * @throws IllegalArgumentException in case of missing a key in the mapping.
   */
  public String interpolate(String stringToBeInterpolated) {
    Objects.requireNonNull(stringToBeInterpolated, "stringToBeInterpolated is not allowed to be null.");
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(stringToBeInterpolated);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      if (!mapping.containsKey(matcher.group(1))) {
        continue;
      }

      String escaped = Matcher.quoteReplacement(mapping.get(matcher.group(1)));
      matcher.appendReplacement(sb, escaped);
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

}
