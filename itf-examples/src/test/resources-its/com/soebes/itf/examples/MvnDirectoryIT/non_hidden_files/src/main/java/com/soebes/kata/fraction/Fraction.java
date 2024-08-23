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

import org.apiguardian.api.API;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;
import java.util.StringJoiner;

import static org.apiguardian.api.API.Status.EXPERIMENTAL;

/**
 * @author Karl Heinz Marbaise
 */
@API(status = EXPERIMENTAL)
public class Fraction implements Comparable<Fraction> {
  private final int numerator;
  private final int denominator;

  public Fraction(int numerator, int denominator) {
    if (denominator == 0) {
      throw new IllegalArgumentException("denominator is not allowed to be zero.");
    }
    int sign = Integer.signum(numerator) * Integer.signum(denominator);

    int gcd = MathUtil.calculateGcd(numerator, denominator);
    this.numerator = sign * Math.abs(numerator) / gcd;
    this.denominator = Math.abs(denominator) / gcd;
  }

  public Fraction plus(Fraction add) {
    if (this.denominator == add.denominator) {
      return new Fraction(add.numerator + this.numerator, this.denominator);
    } else {
      return new Fraction(add.numerator * this.denominator + this.numerator * add.denominator, add.denominator * this.denominator);
    }
  }

  public Fraction subtract(Fraction subtrahend) {
    if (this.denominator == subtrahend.denominator) {
      return new Fraction(this.numerator - subtrahend.numerator, this.denominator);
    } else {
      return new Fraction(this.numerator * subtrahend.denominator - this.denominator * subtrahend.numerator, subtrahend.denominator * this.denominator);
    }
  }

  public Fraction multiply(Fraction factor) {
    return new Fraction(this.numerator * factor.numerator, this.denominator * factor.denominator);
  }

  public Fraction divide(Fraction divisor) {
    return new Fraction(this.numerator * divisor.denominator, this.denominator * divisor.numerator);
  }

  public Fraction pow(int power) {
    return new Fraction(BigInteger.valueOf(this.numerator).pow(power).intValueExact(), BigInteger.valueOf(this.denominator).pow(power).intValueExact());
  }

  public int numerator() {
    return numerator;
  }

  public int denominator() {
    return denominator;
  }

  @Override
  public int compareTo(Fraction compareTo) {
    if (compareTo == null) {
      throw new NullPointerException("compareTo is not allowed to be null.");
    }
    return this.subtract(compareTo).signum();
  }

  /**
   * Returns the signum function of this {@code Fraction}.
   *
   * @return -1, 0, or 1 as the value of this {@code Fraction}
   * is negative, zero, or positive.
   */
  public int signum() {
    return Integer.signum(numerator);
  }

  public Fraction negate() {
    return new Fraction(Math.negateExact(this.numerator), this.denominator);
  }

  public double doubleValue() {
    return (double) this.numerator / (double) this.denominator;
  }

  public BigDecimal bigDecimalValue() {
    return BigDecimal.valueOf(this.numerator).divide(BigDecimal.valueOf(this.denominator));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Fraction fraction = (Fraction) o;
    return numerator == fraction.numerator &&
        denominator == fraction.denominator;
  }

  @Override
  public int hashCode() {
    return Objects.hash(numerator, denominator);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", Fraction.class.getSimpleName() + "[", "]")
        .add("numerator=" + numerator)
        .add("denominator=" + denominator)
        .toString();
  }
}
