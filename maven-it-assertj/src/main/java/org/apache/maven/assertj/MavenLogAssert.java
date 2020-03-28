package org.apache.maven.assertj;

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
import java.util.List;
import java.util.stream.Collectors;
import org.apache.maven.jupiter.maven.MavenLog;
import org.assertj.core.api.AbstractAssert;

/**
 * @author Karl Heinz Marbaise
 */
public class MavenLogAssert extends AbstractAssert<MavenLogAssert, MavenLog> {

  protected MavenLogAssert(MavenLog actual) {
    super(actual, MavenLogAssert.class);
  }

  /**
   * An entry point for MavenExecutionResultAssert to follow AssertJ standard <code>assertThat()</code> statements.<br>
   * With a static import, one's can write directly : <code>assertThat(result).isSuccessful();</code>
   *
   * @param actual the MavenLog we want to make assertions on.
   * @return a new </code>{@link MavenLogAssert}</code>
   */
  public static MavenLogAssert assertThat(MavenLog actual) {
    return new MavenLogAssert(actual);
  }

  /**
   * @return {@link MavenLogAssert} for method chaining.
   * @throws AssertionError if the actual value is {@code null}.
   * @throws AssertionError if the actual value is not {@code true}.
   */
  public MavenLogAssert isSuccessful() {
    isNotNull();
    //this.actual.getStderr();
    //    if (!this.actual.isSuccesful()) {
    //      failWithMessage("The build was not successful but was <%s> with returnCode:<%s>", actual.getResult(),
    //          actual.getReturnCode());
    //    }
    return myself;
  }

  List<String> createLogStream() {
    try {
      //TODO: Need to reconsider if there isn't a better way to return the stream?
      // lines() gives a stream which might be a better solution?
      //    InputStream resourceAsStream = this.getClass().getResourceAsStream("/mvn-stdout.out");
      //    return new BufferedReader(new InputStreamReader(resourceAsStream, Charset.defaultCharset())).lines();
      return Files.readAllLines(this.actual.getStdout());
    } catch (IOException e) {
      //FIXME: Logging exception.
    }
    //FIXME: Need to reconsider the following?
    return null;
  }

  /**
   * The logging output on stdout is given back in a list of strings. The prefix {@code [INFO]} (including the following
   * space) has been filtered out of this already.
   *
   * @return list of Strings which contains the log output
   */
  public List<String> info() {
    return createLogStream().stream()
        .filter(p -> p.startsWith("[INFO]"))
        .map(s -> s.substring(7))
        .collect(Collectors.toList());
  }

  public void buildSuccess() {
    this.info()
        .stream()
        .filter(s -> s.equals("BUILD SUCESS"))
        .findAny()
        .orElseThrow(() -> new IllegalStateException("CCC"));
  }

  public void buildFailure() {
    this.info()
        .stream()
        .filter(s -> s.equals("BUILD FAILURE"))
        .findAny()
        .orElseThrow(() -> new IllegalStateException("Not .."));
  }

  public List<String> debug() {
    return createLogStream().stream()
        .filter(p -> p.startsWith("[DEBUG]"))
        .map(s -> s.substring(9))
        .collect(Collectors.toList());
  }

}
