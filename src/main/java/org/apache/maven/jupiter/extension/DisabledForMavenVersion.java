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
 * {@code @DisabledForMavenVersion} is used to signal that the annotated test class or test method is
 * <em>disabled</em> on one or more specified Maven version which the tests are being run with.
 *
 * <p>When applied at the class level, all test methods within that class
 * will be disabled on the same specified Maven version.
 *
 * <p>If a test method is disabled via this annotation, that does not prevent
 * the test class from being instantiated. Rather, it prevents the execution of the test method and method-level
 * lifecycle callbacks such as {@code @BeforeEach} methods, {@code @AfterEach} methods, and corresponding extension
 * APIs.
 *
 * <p>This annotation may be used as a meta-annotation in order to create a
 * custom <em>composed annotation</em> that inherits the semantics of this annotation.
 *
 * <h4>Warning</h4>
 *
 * @author Karl Heinz Marbaise
 * @see org.apache.maven.jupiter.extension.maven.MavenVersion
 * @see org.junit.jupiter.api.condition.EnabledOnOs
 * @see org.junit.jupiter.api.condition.EnabledOnJre
 * @see org.junit.jupiter.api.condition.DisabledOnJre
 * @see org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable
 * @see org.junit.jupiter.api.condition.DisabledIfEnvironmentVariable
 * @see org.junit.jupiter.api.condition.EnabledIfSystemProperty
 * @see org.junit.jupiter.api.condition.DisabledIfSystemProperty
 * @see org.junit.jupiter.api.Disabled
 * @since 0.1
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@ExtendWith(DisabledForMavenVersionCondition.class)
@API(status = EXPERIMENTAL, since = "0.1.0")
public @interface DisabledForMavenVersion {

  MavenVersion[] value();
}
