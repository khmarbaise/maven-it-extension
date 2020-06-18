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

import org.apiguardian.api.API;
import org.assertj.core.api.AbstractAssert;
import org.assertj.core.api.ListAssert;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Assertions related to logging output of Maven.
 *
 * @author Karl Heinz Marbaise
 * @see #info()
 * @see #warn()
 * @see #debug()
 * @see #plain()
 */
@API(status = EXPERIMENTAL, since = "0.8.0")
public class LogAssert extends AbstractAssert<LogAssert, LogClass> {

  /**
   * Prefix for each line which is logged in {@code DEBUG} state.
   */
  private static final Predicate<String> IS_DEBUG = p -> p.startsWith("[DEBUG] ");
  /**
   * Prefix for each line which is logged in {@code INFO} state.
   */
  private static final Predicate<String> IS_INFO = p -> p.startsWith("[INFO] ");
  /**
   * Prefix for each line which is logged in {@code WARNING} state.
   */
  private static final Predicate<String> IS_WARNING = s -> s.startsWith("[WARNING] ");

  /**
   * Create an instance of LogAssert.
   *
   * @param actual {@link LogClass}
   */
  LogAssert(LogClass actual) {
    super(actual, LogAssert.class);
  }

  /**
   * Will give you back the stdout and removes the prefix {@code "[INFO] "} (including the single space) from all lines.
   * <p>
   * It can be used to check for a sequence in the log output like this:
   * <pre><code class="java">
   *   assertThat(result)
   *    .out()
   *    .info()
   *    .containsSequence("The first line matching.", "The second line matching");
   * </code></pre>
   * </p>
   *
   * @return {@link ListAssert}
   * @see ListAssert#containsSequence(Object[])
   * @since 0.8.0
   */
  public ListAssert<String> info() {
    return new ListAssert<>(createLog().stream()
        .filter(IS_INFO)
        .map(s -> s.substring(7)) // Need to reconsider?
        .collect(Collectors.toList()));
  }

  /**
   * Will give you back the stdout and removes the prefix "[DEBUG] " from all lines.
   * This could be used like the following:
   * <pre><code class="java">
   *   assertThat(result)
   *    .out()
   *    .debug()
   *    .contains("Text to be checked for.");
   * </code></pre>
   * The {@code debug()} will give you back all entries which have the prefix
   * {@code [DEBUG] }.
   * The {@code debug()} can be combined with other parts of AssertJ like:
   * <pre><code class="java">
   *   assertThat(result)
   *    .out()
   *    .debug()
   *    .hasSize(1) // Only a single DEBUG line is allowed.
   *    .contains("This is something.");
   * </code></pre>
   * The above example will obviously fail cause there always more than one line
   * of debug output being produced during a Maven build.
   *
   * @return {@link ListAssert}
   * @see #warn()
   * @see #info()
   * @see ListAssert#contains(Object[])
   */
  public ListAssert<String> debug() {
    return new ListAssert<>(createLog().stream()
        .filter(IS_DEBUG)
        .map(s -> s.substring(8)) // Need to reconsider?
        .collect(Collectors.toList()));
  }

  /**
   * Will read the stdout and removes the prefix {@code "[WARNING] "}.
   * This could be used like the following:
   * <pre><code class="java">
   *   assertThat(result)
   *    .out()
   *    .warn()
   *    .containsExactly("Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!");
   * </code></pre>
   * The {@code warn()} will give you back all entries which have the prefix
   * {@code [WARNING] }.
   * The {@code warn()} can be combined with other parts of AssertJ like:
   * <pre><code class="java">
   *   assertThat(result)
   *    .log()
   *    .warn()
   *    .hasSize(1) // Only a single WARNING is allowed.
   *    .contains("Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!");
   * </code></pre>
   *
   * @return {@link ListAssert}
   * @see #debug()
   * @see #info()
   * @see ListAssert#contains(Object[])
   */
  public ListAssert<String> warn() {
    return new ListAssert<>(createLog().stream()
        .filter(IS_WARNING)
        .map(s -> s.substring(10)) // Need to reconsider?
        .collect(Collectors.toList()));
  }

  /**
   * Will read the stdout without any filtering etc.
   * <pre><code class="java">
   *   assertThat(result)
   *    .out()
   *    .plain()
   *    .contains("[WARNING] Using platform encoding (UTF-8 actually) to copy filtered resources, i.e. build is platform dependent!");
   * </code></pre>
   *
   * @return {@link ListAssert}
   * @see #info()
   * @see #debug()
   * @see #warn()
   * @see ListAssert#contains(Object[])
   */
  public ListAssert<String> plain() {
    return new ListAssert<>(createLog());
  }

  private List<String> createLog() {
    try {
      //TODO: Need to reconsider if there isn't a better way to return the stream?
      // lines() gives a stream which might be a better solution?
      //    InputStream resourceAsStream = this.getClass().getResourceAsStream("/mvn-stdout.log");
      //    return new BufferedReader(new InputStreamReader(resourceAsStream, Charset.defaultCharset())).lines();
      return Files.readAllLines(this.actual.getLog());
    } catch (IOException e) {
      //FIXME: Logging exception.
    }
    //FIXME: Need to reconsider the following?
    return null;
  }

}
