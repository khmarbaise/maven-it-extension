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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Karl Heinz Marbaise
 */
public class StringInterpolator {

  /**
   * This pattern {@code ${..}} with the given group {@code (.*?)} is defined to extract the {@code ${key}} as
   * {@code key} which is needed to access the {@link #mapping}.
   */
  private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile(
      Pattern.quote("${") + "(.*?)" + Pattern.quote("}"));

  private Map<String, Object> mapping;

  /**
   * @param mapping the map which contains the mapping between key and value. {@code null is not allowed}
   * @throws IllegalArgumentException in case of giving {@code null} for {@code mapping}.
   */
  public StringInterpolator(Map<String, Object> mapping) {
    this.mapping = Objects.requireNonNull(mapping, "mapping is not allowed to be null.");
  }

  public static List<String> keys(String keyString) {
    Objects.requireNonNull(keyString, "keyString is not allowed to be null.");
    List<String> result = new ArrayList<>();
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(keyString);
    while (matcher.find()) {
      result.add(matcher.group(1));
    }
    return result;
  }

  /**
   * @param stringToBeInterpolated The string which will interpolated.
   * @return The interpolated string.
   * @throws IllegalArgumentException in case of giving {@code null} for {@code givenString}.
   * @throws IllegalArgumentException in case of missing a key in the mapping. TODO: Correct? Or should we use a
   * different Exception type?
   */
  public String interpolate(String stringToBeInterpolated) {
    Objects.requireNonNull(stringToBeInterpolated, "stringToBeInterpolated is not allowed to be null.");
    Matcher matcher = PLACEHOLDER_PATTERN.matcher(stringToBeInterpolated);
    StringBuffer sb = new StringBuffer();
    while (matcher.find()) {
      if (!mapping.containsKey(matcher.group(1))) {
        throw new IllegalArgumentException("The key " + matcher.group(1) + " does not exist.");
      }

      String escaped = Matcher.quoteReplacement(mapping.get(matcher.group(1)).toString());
      matcher.appendReplacement(sb, escaped);
    }
    matcher.appendTail(sb);
    return sb.toString();
  }

}
