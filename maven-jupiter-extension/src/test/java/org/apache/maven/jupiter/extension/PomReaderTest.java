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

import java.io.IOException;
import java.io.InputStream;
import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.jupiter.api.Test;

class PomReaderTest {

  @Test
  void should_read_the_pom_file_without_any_isseu() throws IOException, XmlPullParserException {
    InputStream resourceAsStream = this.getClass().getResourceAsStream("/pom.xml");
    PomReader pomReader = new PomReader(resourceAsStream);
    Model model = pomReader.getModel();

    assertThat(model.getArtifactId()).isEqualTo("versions-maven-plugin");
    assertThat(model.getVersion()).isEqualTo("2.8-SNAPSHOT");
  }
}
