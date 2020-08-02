package com.soebes.kata.fraction;

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

import java.math.BigInteger;

/**
 * @author Karl Heinz Marbaise
 */
class MathUtil {

  private MathUtil() {
    // intentionally empty.
  }

  /**
   * @param numerator Numerator
   * @param denominator Denominator
   * @return greatest common divisor.
   */
  static int calculateGcd(int numerator, int denominator) {
    return BigInteger.valueOf(numerator).gcd(BigInteger.valueOf(denominator)).intValueExact();
  }

  /**
   * @param numerator Numerator
   * @param denominator Denominator
   * @return greatest common divisor.
   */
  static long calculateGcd(long numerator, long denominator) {
    return BigInteger.valueOf(numerator).gcd(BigInteger.valueOf(denominator)).longValueExact();
  }

  /**
   * @param numerator Numerator
   * @param denominator Denominator
   * @return greatest common divisor.
   */
  static BigInteger calculateGcd(BigInteger numerator, BigInteger denominator) {
    return numerator.gcd(denominator);
  }
}
