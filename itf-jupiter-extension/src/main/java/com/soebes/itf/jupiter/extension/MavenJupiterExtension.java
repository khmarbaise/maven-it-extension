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
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * This annotation defines the base to run any kind of integration tests.
 *
 * <p>This also defines defines some useful defaults.</p>
 *
 * @author Karl Heinz Marbaise
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MavenITExtension.class)
@Documented
@API(status = EXPERIMENTAL, since = "0.1.0")
public @interface MavenJupiterExtension {

  /**
   * The source structure looks usually like this:
   * <pre>
   *   src/test/resources-its/.../
   *      +--- FirstIT/
   *              +--- test_case_one
   *                      +--- src/...
   *                      +--- pom.xml
   *
   * </pre>
   * If you don't like to provide an appropriate
   * project setup as given in `test_case_one` you can turn that
   * off via setting {@link #resourcesIts()}=false.
   * This means you have to provide the content of a project on your
   * own.
   * The easiest way to provide a project setup is via {@code @BeforeEach} in a
   * base class in all of your tests.
   */
  @API(status = EXPERIMENTAL, since = "0.12.0")
  boolean resourcesIts() default true;
}
