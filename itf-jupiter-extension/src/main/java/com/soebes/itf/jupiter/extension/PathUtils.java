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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PathUtils {

  private PathUtils() {
  }

  public static void deleteRecursively(Path target) throws IOException {
    if (!Files.exists(target)) {
      return;
    }
    try (Stream<Path> walk = Files.walk(target)) {
      List<Path> allElements = walk.filter(s -> Files.isRegularFile(s) || Files.isDirectory(s))
          .collect(Collectors.toList());

      Collections.reverse(allElements);

      allElements.stream().forEachOrdered(s -> {
        System.out.println("Delete s = " + s);
        try {
          if (Files.exists(s)) {
            Files.delete(s);
          }
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }

  public static void copyDirectoryRecursively(Path source, Path target) throws IOException {
    if (!Files.isDirectory(source)) {
      Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
      return;
    }

    if (Files.notExists(target)) {
      Files.createDirectory(target);
    }

    try (Stream<Path> paths = Files.list(source)) {
      paths.forEachOrdered(s -> {
        try {
          copyDirectoryRecursively(s, target.resolve(source.relativize(s)));
        } catch (IOException e) {
          throw new RuntimeException(e);
        }
      });
    }
  }
}
