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

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.soebes.itf.jupiter.extension.Preconditions.requireNotNull;
import static com.soebes.itf.jupiter.extension.StringInterpolator.interpolate;

/**
 * This will replace the placeholders which can be defined in {@link MavenGoal}.
 *
 * @author Karl Heinz Marbaise
 */
class PropertiesFilter {

  private final Map<String, String> keyValues;

  private final List<String> items;

  public PropertiesFilter(Map<String, String> keyValues, List<String> items) {
    this.keyValues = requireNotNull(keyValues, "keyValues is not allowed to be null.");
    this.items = requireNotNull(items, "items not allowed to be null.");
  }

  List<String> filter() {
    return this.items.stream()
        .map(interpolate(this.keyValues))
        .collect(Collectors.toList());
  }

}
