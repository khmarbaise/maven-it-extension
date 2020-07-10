package com.soebes.itf.extension.assertj;

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
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * Helper class for {@link LogAssert} and {@link MavenLogAssert}.
 *
 * @author Karl Heinz Marbaise
 */
class Helper {

  private Helper() {
    // intentionally empty.
  }

  /**
   * Prefix for each line which is logged in {@code DEBUG} state.
   */
  static final Predicate<String> IS_DEBUG = p -> p.startsWith("[DEBUG] ");
  /**
   * Prefix for each line which is logged in {@code INFO} state.
   */
  static final Predicate<String> IS_INFO = p -> p.startsWith("[INFO] ");
  /**
   * Prefix for each line which is logged in {@code WARNING} state.
   */
  static final Predicate<String> IS_WARNING = s -> s.startsWith("[WARNING] ");
  /**
   * Prefix for each line which is logged in {@code ERROR} state.
   */
  static final Predicate<String> IS_ERROR = s -> s.startsWith("[ERROR] ");

  /**
   * @param path The location of the file which will be read as a {@code Stream}.
   * @return stream of {@code String}.
   */
  static Stream<String> logs(Path path) {
    try {
      return Files.lines(path);
    } catch (IOException e) {
      throw new IllegalStateException("Exception occured.", e);
    }
  }

}
