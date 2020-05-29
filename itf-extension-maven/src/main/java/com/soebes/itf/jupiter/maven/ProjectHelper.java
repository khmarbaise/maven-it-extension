package com.soebes.itf.jupiter.maven;

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

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apiguardian.api.API;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * @author Karl Heinz Marbaise
 */
@API(status = EXPERIMENTAL, since = "0.1.0")
public final class ProjectHelper {

  private ProjectHelper() {
    // intentionally private.
  }

  /**
   * @param inputStream The stream where to read the {@code pom.xml} from.
   * @return The {@link Model}
   */
  public static Model readProject(InputStream inputStream) {
    MavenXpp3Reader mavenXpp3Reader = new MavenXpp3Reader();
    try {
      return mavenXpp3Reader.read(inputStream);
    } catch (XmlPullParserException | IOException e) {
      throw new IllegalStateException("Failed to read pom.xml", e);
    }
  }

  /**
   * @param pomFile The directory where to read the {@code pom.xml} from.
   * @return The {@link Model}
   */
  public static Model readProject(File pomFile) {
    try (InputStream is = new FileInputStream(pomFile)) {
      return readProject(is);
    } catch (IOException e) {
      throw new IllegalStateException("Failed to read pom.xml", e);
    }
  }
}
