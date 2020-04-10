package org.apache.maven.jupiter.extension;

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

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apiguardian.api.API;
import org.junit.jupiter.api.Test;

/**
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
   * Defines the goals which are being executed. This can be used to simply override the goals given in {@link
   * MavenIT#goals()}.
   *
   * @return The default goals are none given.
   */
  String[] goals() default {};

  /**
   * You can define the profile you would like to activate: For example for a single profile:
   * <pre>
   *    &#x40;MavenTest(profiles = {"run-its"})
   * </pre>
   * For multiple profiles:
   * <pre>
   *    &#x40;MavenTest(profiles = {"run-its", "second-profile"})
   * </pre>
   * This is the equivalent to the command line like: {@code -Prun-its,second-profile}
   *
   * @return The defines profiles.
   */
  String[] activeProfiles() default {};

  /**
   * You can define a single option like this:
   * <pre>
   *    &#x40;MavenTest(options = {"-o"})
   * </pre>
   * This is the equivalent to the command line like: {@code -o}.
   *
   * You can of course combine several options like the following:
   * <pre>
   *    &#x40;MavenTest(options = {"-X", "-o"})
   * </pre>
   * This is the equivalent to the command line like: {@code -X -o}
   *
   * You can make your options a little bit more readable by using
   * {@link MavenOptions} like the following:
   * <pre>
   *    &#x40;MavenTest(options = {MavenOptions.DEBUG, "-o"})
   * </pre>
   *
   * @see MavenOptions
   * @return The defined options.
   *
   */
  String[] options() default {};


  /**
   * By using {@code systemProperties} you can define the
   * properties which will be given to the command line
   * call of Maven.
   *
   * <pre>
   *    &#x40;MavenTest(options = {systemProperteis {
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
   */
  String[] systemProperties() default {};

  /**
   * This turns on {@code -X} (debug:true) for the Maven run or not (debug:false).
   *
   * @return Debug
   */
  boolean debug() default false;

  /**
   * By default the project name is interpolated by the name of the test method. This can be overwritten by this
   * configuration if you need. Best practice is to follow the convention over configuration paradigm.
   *
   * @return The name of the project which is used.
   * @implNote FIXME: Currently not implemented. Idea!
   */
  String project() default "";
}
