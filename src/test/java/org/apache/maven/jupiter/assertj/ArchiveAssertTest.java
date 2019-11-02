package org.apache.maven.jupiter.assertj;

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

import static org.mockito.Mockito.when;

import java.io.File;
import java.net.URL;
import org.apache.maven.model.Model;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * @author Karl Heinz Marbaise
 */
class ArchiveAssertTest {

  /*
    testing: META-INF/
    testing: META-INF/MANIFEST.MF
    testing: META-INF/application.xml
    testing: META-INF/maven/
    testing: META-INF/maven/org.apache.maven.its.ear.resourcecustomdirectory/
    testing: META-INF/maven/org.apache.maven.its.ear.resourcecustomdirectory/test/
    testing: META-INF/maven/org.apache.maven.its.ear.resourcecustomdirectory/test/pom.xml
    testing: META-INF/maven/org.apache.maven.its.ear.resourcecustomdirectory/test/pom.properties
    testing: APP-INF/
    testing: APP-INF/classes/
    testing: APP-INF/classes/foo.properties

   */
  @Test
  void name() {
    URL resource = this.getClass().getResource("/example-files/test-1.0.ear");
    File earFile = new File(resource.getFile());
    String[] files = {"META-INF/application.xml", "APP-INF/classes/foo.properties"};

    Model modelMock = Mockito.mock(Model.class);
    when(modelMock.getGroupId()).thenReturn("org.apache.maven.its.ear.resourcecustomdirectory");
    when(modelMock.getArtifactId()).thenReturn("test");

//    ArchiveEntryContent content = ArchiveEntryContent.create().files("META-INF/application.xml", "APP-INF/classes/foo.properties");
//    assertThat(earFile).isEqualTo(content);

    Assertions.assertThat(earFile);
    ArchiveAssert as = new ArchiveAssert(earFile, modelMock, null);
    as.containsOnly(files);
  }
}