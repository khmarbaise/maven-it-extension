package org.apache.maven.jupiter.assertj.archiver;

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

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

/**
 * @author Karl Heinz Marbaise
 * @implNote Currently I'm not sure if the entries within an archive always follow a particular/same order so using {@link
 * org.assertj.core.api.ListAssert#containsExactlyInAnyOrder} instead of {@link org.assertj.core.api.ListAssert#containsExactly}
 * for the given tests.
 */
class ArchiverHelperTest {

  @Test
  @DisplayName("Two entries should be converted into corresponding directories including files.")
  void should_be_converted_to_corresponding_directories_and_files() {
    //@formatter:off
    String[] givenFiles = {
        "META-INF/application.xml",
        "APP-INF/classes/foo.properties"};
    String[] expectedArchiveEntries = {
        "META-INF/",
        "META-INF/application.xml",
        "APP-INF/",
        "APP-INF/classes/",
        "APP-INF/classes/foo.properties"};
    //@formatter:on

    List<String> actual = ArchiverHelper.convertToEntries(givenFiles);
    assertThat(actual).containsExactlyInAnyOrder(expectedArchiveEntries);
  }

  @Test
  @DisplayName("Should convert single entry into two entries which comprises of directory and directory plus filename.")
  void should_convert_single_entry_into_two() {
    //@formatter:off
    String[] givenFiles = {
        "META-INF/application.xml"
    };
    String[] expectedArchiveEntries = {
        "META-INF/",
        "META-INF/application.xml"
    };
    //@formatter:on

    List<String> result = ArchiverHelper.convertToEntries(givenFiles);
    assertThat(result).containsExactlyInAnyOrder(expectedArchiveEntries);
  }

  @Test
  @DisplayName("Should convert name list with duplicated directories into corrosponding entry list.")
  void should_convert_name_list_from_same_directories_into_corrosponding_entry_list() {
    //@formatter:off
    String[] givenFiles = {
        "META-INF/MANIFEST.MF",
        "META-INF/application.xml",
        "APP-INF/classes/foo.properties",
        "META-INF/maven/org.apache.maven.its.ear.resourcecustomdirectory/test/pom.xml",
        "META-INF/maven/org.apache.maven.its.ear.resourcecustomdirectory/test/pom.properties"
    };
    String[] expectedArchiveEntries = {
      "META-INF/",
      "META-INF/MANIFEST.MF",
      "META-INF/application.xml",
      "META-INF/maven/",
      "META-INF/maven/org.apache.maven.its.ear.resourcecustomdirectory/",
      "META-INF/maven/org.apache.maven.its.ear.resourcecustomdirectory/test/",
      "META-INF/maven/org.apache.maven.its.ear.resourcecustomdirectory/test/pom.xml",
      "META-INF/maven/org.apache.maven.its.ear.resourcecustomdirectory/test/pom.properties",
      "APP-INF/",
      "APP-INF/classes/",
      "APP-INF/classes/foo.properties"
    };
    //@formatter:on
    List<String> actual = ArchiverHelper.convertToEntries(givenFiles);
    assertThat(actual).containsExactlyInAnyOrder(expectedArchiveEntries);
  }
}
