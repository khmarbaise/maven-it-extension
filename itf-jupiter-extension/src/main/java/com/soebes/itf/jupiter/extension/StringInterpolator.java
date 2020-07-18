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

import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Karl Heinz Marbaise
 */
interface StringInterpolator {

  /**
   * This pattern {@code ${..}} with the given group {@code (.*?)} is defined to extract the {@code ${key}} as
   * {@code key}.
   */
  Pattern PLACEHOLDER_PATTERN = Pattern.compile(
      Pattern.quote("${") + "(.*?)" + Pattern.quote("}"));

  /**
   * @param mapping The map which contains the mapping between {@code key} and {@code value}. {@code null is not allowed}
   * @return The interpolated string.
   */
  static Function<String, String> interpolate(Map<String, String> mapping) {
    Objects.requireNonNull(mapping, "mapping is not allowed to be null.");
    return s -> {
      Matcher matcher = PLACEHOLDER_PATTERN.matcher(s);
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
    };
  }

}
