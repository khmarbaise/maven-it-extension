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

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * Control the need to provide projects in the
 * default project setup directory `resources-its`
 * or provide them on your own.
 *
 * @author Karl Heinz Marbaise
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@API(status = EXPERIMENTAL, since = "0.12.0")
public @interface MavenProjectSources {

  enum ResourceUsage {
    DEFAULT,
    NONE
  }

  /**
   * The source structure looks like this:
   * <pre>
   *   src/test/resources-its/.../
   *      +--- FirstIT/
   *              +--- test_case_one
   *                      +--- src/...
   *                      +--- pom.xml
   *
   * </pre>
   * The content of the directory `test_case_one` is
   * required to execute an integration tests.
   * If you have a situation where you don't provide the test project
   * via this you can turn that off via {@link #resourcesUsage()}={@link ResourceUsage#NONE}.
   * Doing will require to provide a project setup on your own.
   * This can be done what ever comes into your mind. The easiest
   * way to make such setup is to us the setup via `{@code @BeforeEach}`.
   */
  ResourceUsage resourcesUsage() default ResourceUsage.DEFAULT;

  /**
   * This is by default empty which means the location will be calculated
   * based on the given integration test class + the method name and/or
   * a nested class.
   *
   * @return The location where to find the source project.
   */
  String sources() default "";

}
