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
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.apache.maven.jupiter.extension.maven.MavenVersion;
import org.apiguardian.api.API;
import org.junit.jupiter.api.extension.ExtendWith;

/**
 * @author Karl Heinz Marbaise
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(MavenITExtension.class)
@Documented
@API(status = EXPERIMENTAL, since = "0.1")
public @interface MavenIT {

  //TODO: Reconsider?
  MavenVersion[] versions() default MavenVersion.M3_6_2;

  /**
   * Defines the goals which are being executed by default for
   * all test cases within the class.
   *
   * @return The default goals which is executed is {@code package}.
   */
  String[] goals() default {"package"};

  /**
   * This can be used to activate debugging output within the maven build {@code -X} option.
   *
   * @return {@code true} or {@code false}
   */
  boolean debug() default false;

}
