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

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * {@code @MavenDebug} is a meta annotation for convenience purposes
 * to make it easier to activate debugging option for a Maven build
 * just by simply adding {@code @MavenDebug}.
 *
 * <p>When applied at the class level, all test methods within that class
 * are automatically inheriting the given goal.</p>
 *
 * @since 0.9.0
 * @see MavenOption
 * @author Karl Heinz Marbaise
 * @deprecated Will be removed with Release 0.14.0. Use {@link MavenVerbose} instead.
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RUNTIME)
@Inherited
@MavenOption(value = MavenCLIOptions.DEBUG)
@API(status = EXPERIMENTAL, since = "0.9.0")
@Deprecated
public @interface MavenDebug {
}
