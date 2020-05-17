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

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author Karl Heinz Marbaise
 */
class CreateSettingsFromTemplate {

  private final URI repository;

  CreateSettingsFromTemplate(URI repository) {
    this.repository = repository;
  }

  List<String> create() throws IOException {
    InputStream resourceAsStream = this.getClass().getResourceAsStream("/interpolator-template-settings.xml");

    List<String> strings = IOUtils.readLines(resourceAsStream, Charset.defaultCharset());
    Map<String, String> mapping = new HashMap<>();
    mapping.put("mirror.url", this.repository.toASCIIString());
    StringInterpolator si = new StringInterpolator(mapping);
    return strings.stream()
        .map(line -> si.interpolate(line))
        .collect(Collectors.toList());
  }

}
