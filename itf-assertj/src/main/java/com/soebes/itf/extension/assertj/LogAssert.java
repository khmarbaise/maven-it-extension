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

import java.util.stream.Collectors;

import static com.soebes.itf.extension.assertj.Helper.IS_DEBUG;
import static com.soebes.itf.extension.assertj.Helper.IS_ERROR;
import static com.soebes.itf.extension.assertj.Helper.IS_INFO;
import static com.soebes.itf.extension.assertj.Helper.IS_WARNING;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Assertions related to logging output of Maven.
 *
 * @author Karl Heinz Marbaise
 * @see #info()
 * @see #warn()
 * @see #debug()
 * @see #error()
 * @see #plain()
 */
@API(status = EXPERIMENTAL, since = "0.8.0")
public class LogAssert extends AbstractAssert<LogAssert, LogClass> {

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
    return new ListAssert<>(Helper.logs(this.actual.getLog())
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
    return new ListAssert<>(Helper.logs(this.actual.getLog())
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
    return new ListAssert<>(Helper.logs(this.actual.getLog())
        .filter(IS_WARNING)
        .map(s -> s.substring(10)) // Need to reconsider?
        .collect(Collectors.toList()));
  }

  /**
   * Will read the stdout and removes the prefix {@code "[ERROR] "}.
   * This could be used like the following:
   * <pre><code class="java">
   *   assertThat(result)
   *    .out()
   *    .error()
   *    .containsExactly("exception.");
   * </code></pre>
   * The {@code error()} will give you back all entries which have the prefix
   * {@code [ERROR] }.
   *
   * @return {@link ListAssert}
   * @see #debug()
   * @see #warn()
   * @see #info()
   * @see ListAssert#contains(Object[])
   */
  public ListAssert<String> error() {
    return new ListAssert<>(Helper.logs(this.actual.getLog())
        .filter(IS_ERROR)
        .map(s -> s.substring(8)) // Need to reconsider?
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
    return new ListAssert<>(Helper.logs(this.actual.getLog()));
  }

}
