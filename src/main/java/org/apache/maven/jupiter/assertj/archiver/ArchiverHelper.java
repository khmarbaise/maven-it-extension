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

import java.util.ArrayList;
import java.util.List;

/**
 * @author Karl Heinz Marbaise
 * @implNote Better name?
 */
public final class ArchiverHelper {

  private ArchiverHelper() {
    // intentionally empty.
  }

  /**
   * This will convert a given entry {@code META-INF/MANIFEST.MF} into two entries
   * <ul>
   *   <li>META-INF/</li>
   *   <li>META-INF/MANIFEST.MF</li>
   * </ul>
   * cause the entries in an archive are given like this. This will make it easier
   * to define the list of entries which should be compared with.
   *
   * @param inputList given the list with entries like {@code META-INF/MANIFEST.MF}
   */
  public static List<String> convertToEntries(String[] inputList) {
    List<String> result = new ArrayList<>();
    for (String s : inputList) {
      String[] split = s.split("/");
      String tmp = "";
      for (int i = 0; i < split.length - 1; i++) {
        tmp += split[i] + "/";
        if (!result.contains(tmp)) {
          result.add(tmp);
        }
      }
      result.add(s);
    }
    return result;
  }

}
