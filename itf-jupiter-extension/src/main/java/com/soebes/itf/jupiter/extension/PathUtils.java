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

import static org.apiguardian.api.API.Status.INTERNAL;

import com.soebes.itf.jupiter.extension.exceptions.PathUtilException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apiguardian.api.API;

/**
 * @author Karl Heinz Marbaise
 */
@API(status = INTERNAL, since = "0.12.0")
public class PathUtils {

  private PathUtils() {
  }

  public static Path copy(Path source, Path target) {
    try {
      return Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
    } catch (IOException e) {
      throw new PathUtilException(e);
    }
  }

  public static Consumer<Path> deletePath = toDelete -> {
    try {
      Files.delete(toDelete);
    } catch (IOException e) {
      throw new PathUtilException(e);
    }
  };

  public static Path createDirectory(Path directoryToCreate) {
    try {
      return Files.createDirectory(directoryToCreate);
    } catch (IOException e) {
      throw new PathUtilException(e);
    }
  }

  public static Stream<Path> list(Path toList) {
    try {
      return Files.list(toList);
    } catch (IOException e) {
      throw new PathUtilException(e);
    }
  }

  public static Stream<Path> walk(Path toWalk) {
    try {
      return Files.walk(toWalk);
    } catch (IOException e) {
      throw new PathUtilException(e);
    }
  }

  private static final Predicate<Path> FILE = Files::isRegularFile;

  private static final Predicate<Path> DIRECTORY = Files::isDirectory;

  private static final Predicate<Path> FILE_OR_DIRECTORY = s -> FILE.or(DIRECTORY).test(s);

  public static void deleteRecursively(Path target) {
    if (!Files.exists(target)) {
      return;
    }

    try (Stream<Path> walk = walk(target)) {
      List<Path> allElements = walk.filter(FILE_OR_DIRECTORY).collect(Collectors.toList());

      Collections.reverse(allElements);

      allElements.stream().filter(Files::exists).forEachOrdered(deletePath);
    }
  }

  public static void copyDirectoryRecursively(Path source, Path target) {
    if (!Files.isDirectory(source)) {
      copy(source, target);
      return;
    }

    if (Files.notExists(target)) {
      createDirectory(target);
    }

    try (Stream<Path> paths = list(source)) {
      paths.forEachOrdered(s -> copyDirectoryRecursively(s, target.resolve(source.relativize(s))));
    }
  }
}
