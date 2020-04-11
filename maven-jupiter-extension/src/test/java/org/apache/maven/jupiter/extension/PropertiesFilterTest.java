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

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class PropertiesFilterTest {

  @Test
  void replace_property_with_given_value() {
    Map<String, String> keyValues = new HashMap<>();
    keyValues.put("project.version", "1.0.0");
    PropertiesFilter propertiesFilter = new PropertiesFilter(keyValues,
        Collections.singletonList("${project.version}"));

    List<String> filter = propertiesFilter.filter();
    assertThat(filter).hasSize(1).containsExactly("1.0.0");
  }

  @Test
  void keep_property_cause_the_property_is_not_known() {
    Map<String, String> keyValues = new HashMap<>();
    keyValues.put("project.version", "1.0.0");
    PropertiesFilter propertiesFilter = new PropertiesFilter(keyValues, Collections.singletonList("${maven.version}"));

    List<String> filter = propertiesFilter.filter();
    assertThat(filter).hasSize(1).containsExactly("${maven.version}");
  }

  @Test
  void replace_placeholder_within_a_string() {
    Map<String, String> keyValues = new HashMap<>();
    keyValues.put("project.version", "1.0.0");
    PropertiesFilter propertiesFilter = new PropertiesFilter(keyValues,
        Collections.singletonList("org.codehaus.mojo:versions-maven-plugin:${project.version}:compare-dependencies"));

    List<String> filter = propertiesFilter.filter();
    assertThat(filter).hasSize(1).containsExactly("org.codehaus.mojo:versions-maven-plugin:1.0.0:compare-dependencies");
  }

}