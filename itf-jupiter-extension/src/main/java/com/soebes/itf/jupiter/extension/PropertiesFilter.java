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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.soebes.itf.jupiter.extension.Preconditions.requireNotNull;

/**
 * This will replace the placeholders which can be defined in {@link MavenTest#goals()}.
 *
 * @author Karl Heinz Marbaise
 */
class PropertiesFilter {

  private final Map<String, String> keyValues;

  private final List<String> listOfItems;

  public PropertiesFilter(Map<String, String> keyValues, List<String> listOfItems) {
    this.keyValues = requireNotNull(keyValues, "keyValues is not allowed to be null.");
    this.listOfItems = requireNotNull(listOfItems, "listOfItems not allowed to be empty nor null.");
  }

  List<String> filter() {
    StringInterpolator si = new StringInterpolator(this.keyValues);
    List<String> result = new ArrayList<>();
    for (String item : this.listOfItems) {
      result.add(si.interpolate(item));
    }
    return result;
  }

  /**
   * This is list of currently supported properties.
   * <p>
   * TODO: Need to reconsider this.
   */
  public enum MavenPropertyNames {
    groupId("project.groupId"),
    artifactId("project.artifactId"),
    version("project.version");

    private final String name;

    MavenPropertyNames(String name) {
      this.name = name;
    }

    public final String getName() {
      return name;
    }

  }

}
