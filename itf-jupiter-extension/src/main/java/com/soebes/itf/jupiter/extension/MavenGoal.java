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
 * {@code @MavenGoal} is used to define the goals which will be executed
 * during an integration test.
 *
 * <p>When applied at the class level, all test methods within that class
 * are automatically inheriting the given goal.</p>
 *
 * <p>
 * You can define a goal on command line for Maven like the following:</p>
 * <pre>
 *    &#x40;MavenGoal("install")
 * </pre>
 * <p>For multiple command line options:</p>
 * <pre>
 *    &#x40;MavenGoal("clean")
 *    &#x40;MavenGoal("install")
 * </pre>
 * <p>This is the equivalent to the command line like: {@code mvn clean install}</p>
 *
 * <p>The annotation {@code @MavenGoal} is an repeatable annotation.</p>
 *
 * @author Karl Heinz Marbaise
 * @see MavenGoals
 * @since 0.9.0
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@Repeatable(value = MavenGoals.class)
@API(status = EXPERIMENTAL, since = "0.9.0")
public @interface MavenGoal {

  String[] value() default {"package"}; //Need to reconsider the default value.

}
