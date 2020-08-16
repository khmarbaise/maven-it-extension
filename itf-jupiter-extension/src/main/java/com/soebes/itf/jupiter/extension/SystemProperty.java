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
 * {@code @SystemProperty} is used to define a system property which will be added
 * to the call of Maven during the execution of the tests.
 *
 * <p>When applied at the class level, all test methods within that class
 * are automatically inheriting the given goal.</p>
 *
 * <p>The annotation {@code @SystemProperty} is an repeatable annotation.</p>
 * <p>
 * Example:
 * <pre>
 *    &#x40;SystemProperty(value = "remotePom", content="localhost:dummy-bom-pom:1.0")
 *    &#x40;SystemProperty(value = "reportOutputFile", content="target/depDiffs.txt")
 * </pre>
 * This will be the equivalent of giving the properties via command line
 * like this:
 * <pre>
 *   mvn -DremotePom=localhost:dummy-bom-pom:1.0 -DreportOutputFile=target/depDiffs.txt ...
 * </pre>
 *
 * @author Karl Heinz Marbaise
 * @since 0.9.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(value = SystemProperties.class)
@API(status = EXPERIMENTAL, since = "0.9.0")
public @interface SystemProperty {

  String value();

  String content() default "";

}
