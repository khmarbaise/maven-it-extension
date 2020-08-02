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

import org.apiguardian.api.API;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.apiguardian.api.API.Status.DEPRECATED;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * <p>
 * Defines the method to be a test which can be executed.</p>
 *
 * @author Karl Heinz Marbaise
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Test
@API(status = EXPERIMENTAL, since = "0.1.0")
public @interface MavenTest {

  /**
   * <b>Hint: This annotation element is marked deprecated. Please use {@code @MavenProfile} instead.</b>
   * <p>
   * Defines the goals which are being executed. This can be used to simply override the goals given in {@link
   * MavenJupiterExtension#goals()}.</p>
   *
   * @return The default goals are none given.
   * @see MavenGoal
   */
  @Deprecated
  @API(status = DEPRECATED, since = "0.9.0")
  String[] goals() default {};

  /**
   * <b>Hint: This annotation element is marked deprecated. Please use {@code @MavenProfile} instead.</b>
   * <p>
   * You can define the profile you would like to activate: For example for a single profile:
   * <pre>
   *    &#x40;MavenTest(profiles = {"run-its"})
   * </pre>
   * </p>
   * For multiple profiles:
   * <pre>
   *    &#x40;MavenTest(profiles = {"run-its", "second-profile"})
   * </pre>
   * This is the equivalent to the command line like: {@code -Prun-its,second-profile}
   *
   * @return The defines profiles.
   * @see MavenProfile
   */
  @Deprecated
  @API(status = DEPRECATED, since = "0.9.0")
  String[] activeProfiles() default {};

  /**
   * <b>Hint: This annotation element is marked deprecated. Please use {@code @MavenOption} instead.</b>
   * <p>
   * You can define a single value like this:
   * <pre>
   *    &#x40;MavenTest(value = {"-o"})
   * </pre>
   * This is the equivalent to the command line like: {@code -o}.
   * </p>
   * <p>
   * You can of course combine several value like the following:
   * <pre>
   *    &#x40;MavenTest(value = {"-X", "-o"})
   * </pre>
   * This is the equivalent to the command line like: {@code -X -o}
   * <p>
   * You can make your value a little bit more readable by using
   * {@link MavenCLIOptions} like the following:
   * <pre>
   *    &#x40;MavenTest(value = {MavenOptions.DEBUG, "-o"})
   * </pre>
   *
   * @return The defined value.
   * @see MavenCLIOptions
   * @see MavenOption
   */
  @Deprecated
  @API(status = DEPRECATED, since = "0.9.0")
  String[] options() default {};


  /**
   * <b>Hint: This annotation element is marked deprecated. Please use {@code @SystemProperty} instead.</b>
   * <p>
   * By using {@code systemProperties} you can define the
   * properties which will be given to the command line
   * call of Maven.</p>
   *
   * <pre>
   *    &#x40;MavenTest(value = {systemProperteis {
   *      "remotePom=localhost:dummy-bom-pom:1.0",
   *      "reportOutputFile=target/depDiffs.txt"
   *    })
   * </pre>
   * This will be the equivalent of giving the properties via command line
   * like this:
   * <pre>
   *   mvn -DremotePom=localhost:dummy-bom-pom:1.0 -DreportOutputFile=target/depDiffs.txt ...
   * </pre>
   *
   * @return The defines system properties.
   * @see SystemProperty
   */
  @Deprecated
  @API(status = DEPRECATED, since = "0.9.0")
  String[] systemProperties() default {};

  /**
   * <b>Hint: This annotation element is marked deprecated. Please use {@code @MavenDebug} instead.</b>
   * <p>
   * This turns on {@code -X} (debug:true) for the Maven run or not (debug:false).</p>
   *
   * @see MavenDebug
   * @return Debug
   */
  @Deprecated
  @API(status = DEPRECATED, since = "0.9.0")
  boolean debug() default false;

}
