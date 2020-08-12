package com.soebes.itf.examples.defaults;

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

import com.soebes.itf.jupiter.extension.MavenCLIOptions;
import com.soebes.itf.jupiter.extension.MavenJupiterExtension;
import com.soebes.itf.jupiter.extension.MavenOption;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * A meta annotation which defines the used default options for all
 * test classes which are annotated by this annotation instead of
 * {@code MavenJupiterExtension}.
 *
 * This makes it possible to override the defaults which are defined in
 * {@code MavenITExtension} code.
 *
 * @author Karl Heinz Marbaise
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RUNTIME)
@Inherited
@MavenJupiterExtension
@MavenOption(MavenCLIOptions.DEBUG)
@MavenOption(MavenCLIOptions.ERRORS)
@MavenOption(MavenCLIOptions.FAIL_AT_END)
public @interface MavenJupiterExtensionWithDefaults {
}
