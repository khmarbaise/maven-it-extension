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

import org.junit.jupiter.api.Test;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.Diff;

import java.io.IOException;
import java.net.URI;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Karl Heinz Marbaise
 */
class CreateSettingsFromTemplateTest {

  @Test
  void name() throws IOException {
    URI uri = URI.create("http://test.repo:1234");
    CreateSettingsFromTemplate createSettingsFromTemplate = new CreateSettingsFromTemplate(uri);
    String createdSettings = createSettingsFromTemplate.create()
        .stream()
        .collect(Collectors.joining(System.lineSeparator()));

    Diff build = DiffBuilder.compare(Input.fromString(createdSettings))
        .withTest(Input.fromStream(this.getClass().getResourceAsStream("/expected-settings.xml")))
        .build();

    assertThat(build.hasDifferences()).as("The differences are %s", build.getDifferences()).isFalse();
  }

}