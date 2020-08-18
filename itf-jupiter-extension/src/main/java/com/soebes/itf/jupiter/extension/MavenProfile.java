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
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * This annotation is intended to define a profile you would like to activate.
 * <p>
 * You can define the profile you would like to activate like this:</p>
 * <pre>
 *    &#x40;MavenProfile("run-its")
 * </pre>
 *
 * <p>For multiple profiles:</p>
 * <pre>
 *    &#x40;MavenProfile({"run-its", "second-profile"})
 * </pre>
 * <p>This is the equivalent to the command line like: {@code -Prun-its,second-profile}</p>
 *
 * <p>You can take advantage of the feature being a repeatable annotation like this:</p>
 * <pre>
 *    &#x40;MavenProfile("first-profile")
 *    &#x40;MavenProfile("second-profile")
 * </pre>
 *
 * @author Karl Heinz Marbaise
 * @see MavenProfiles
 */
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.PACKAGE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(value = MavenProfiles.class)
@API(status = EXPERIMENTAL, since = "0.9.0")
public @interface MavenProfile {

  String[] value() default "";

}
