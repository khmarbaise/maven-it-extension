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

/**
 * @author Karl Heinz Marbaise
 */
class Preconditions {

  /**
   * Check that the given parameter is not {@code null}.
   *
   * @param parameter The parameter which is checked not to be {@code null}.
   * @param message The message which is thrown in the exception.
   * @return the given paremter in case not being {@code null}.
   * @throws IllegalArgumentException in case of parameter is {@code null}.
   */
  static <T> T requireNotNull(T parameter, String message) {
    if (parameter == null) {
      throw new IllegalArgumentException(message);
    }
    return parameter;
  }

  /**
   * Check that the given array has a length which is greater than zero.
   *
   * @param parameter The array parameter of which the length will be checked.
   * @param message The message to thrown in case of not fulfilling the condition.
   * @return Return the original {@code paramter} if not raised the execption.
   * @throws IllegalArgumentException in case the condition has not been fulfilled.
   */
  static <T> T[] requireGreaterZero(T[] parameter, String message) {
    if (parameter.length < 1) {
      throw new IllegalArgumentException(message);
    }
    return parameter;
  }

  /**
   * Check that the given parameter is not empty.
   *
   * @param parameter The String parameter which should not be empty.
   * @param message The message to thrown in case of not fulfilling the condition.
   * @return Return the original {@code parameter} if not raised the execption.
   * @throws IllegalArgumentException in case the condition has not been fulfilled.
   */
  static String requireNotEmpty(String parameter, String message) {
    if (parameter.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
    return parameter;
  }
}
