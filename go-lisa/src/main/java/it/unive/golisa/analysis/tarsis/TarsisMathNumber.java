package it.unive.golisa.analysis.tarsis;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * A wrapper around {@link BigDecimal} to represent the mathematical concept of
 * a number, that can be also plus or minus infinity, in a convenient way.
 * 
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 */
public class TarsisMathNumber implements Comparable<TarsisMathNumber> {

	/**
	 * The constant for plus infinity.
	 */
	public static final TarsisMathNumber PLUS_INFINITY = new TarsisMathNumber((byte) 0);

	/**
	 * The constant for minus infinity.
	 */
	public static final TarsisMathNumber MINUS_INFINITY = new TarsisMathNumber((byte) 1);

	/**
	 * The constant {@code 0}.
	 */
	public static final TarsisMathNumber ZERO = new TarsisMathNumber(0);

	/**
	 * The constant {@code 1}.
	 */
	public static final TarsisMathNumber ONE = new TarsisMathNumber(1);

	/**
	 * The constant {@code -1}.
	 */
	public static final TarsisMathNumber MINUS_ONE = new TarsisMathNumber(-1);

	/**
	 * A constant for representing numbers obtained from an operation that does
	 * not produce a result (e.g. infinity divided by infinity).
	 */
	public static final TarsisMathNumber NaN = new TarsisMathNumber((byte) 3);

	private final BigDecimal number;

	/**
	 * True means this number is positive or zero
	 */
	private final byte sign;

	/**
	 * Builds a math number representing the given value.
	 * 
	 * @param number the value
	 */
	public TarsisMathNumber(long number) {
		this.number = BigDecimal.valueOf(number);
		this.sign = number >= 0 ? (byte) 0 : (byte) 1;
	}

	/**
	 * Builds a math number representing the given value.
	 * 
	 * @param number the value
	 */
	public TarsisMathNumber(double number) {
		this.number = BigDecimal.valueOf(number);
		this.sign = number >= 0 ? (byte) 0 : (byte) 1;
	}

	/**
	 * Builds a math number representing the given value.
	 * 
	 * @param number the value
	 */
	public TarsisMathNumber(BigDecimal number) {
		this.number = number;
		this.sign = number.signum() >= 0 ? (byte) 0 : (byte) 1;
	}

	private TarsisMathNumber(byte sign) {
		this.number = null;
		this.sign = sign;
	}

	/**
	 * Yields {@code true} if this number is minus infinity.
	 * 
	 * @return {@code true} if that condition holds
	 */
	public boolean isMinusInfinity() {
		return number == null && isNegative();
	}

	/**
	 * Yields {@code true} if this number is plus infinity.
	 * 
	 * @return {@code true} if that condition holds
	 */
	public boolean isPlusInfinity() {
		return number == null && isPositiveOrZero();
	}

	/**
	 * Yields {@code true} if this number is infinite (i.e. plus or minus
	 * infinity).
	 * 
	 * @return {@code true} if that condition holds
	 */
	public boolean isInfinite() {
		return isPlusInfinity() || isMinusInfinity();
	}

	/**
	 * Yields {@code true} if this number is finite (i.e. not infinity).
	 * 
	 * @return {@code true} if that condition holds
	 */
	public boolean isFinite() {
		return !isInfinite();
	}

	/**
	 * Yields {@code true} if this number is number represents exactly the given
	 * integer.
	 *
	 * @param n the integer to test
	 * 
	 * @return {@code true} if that condition holds
	 */
	public boolean is(int n) {
		return number != null && number.equals(new BigDecimal(n));
	}

	/**
	 * Yields {@code true} if this number is positive or equal to zero.
	 * 
	 * @return {@code true} if that condition holds
	 */
	public boolean isPositiveOrZero() {
		return sign == (byte) 0;
	}

	/**
	 * Yields {@code true} if this number is negative.
	 * 
	 * @return {@code true} if that condition holds
	 */
	public boolean isNegative() {
		return sign == (byte) 1;
	}

	/**
	 * Yields {@code true} if this number is not a number, that is, obtained
	 * from an operation that does not produce a result (e.g. infinity divided
	 * by infinity).
	 * 
	 * @return {@code true} if that condition holds
	 */
	public boolean isNaN() {
		return number == null && sign == (byte) 3;
	}

	private static TarsisMathNumber cached(TarsisMathNumber i) {
		if (i.is(0))
			return ZERO;
		if (i.is(1))
			return ONE;
		if (i.is(-1))
			return MINUS_ONE;
		return i;
	}

	/**
	 * Yields the result of {@code this + other}. If one of them is not a number
	 * (according to {@link #isNaN()}), then {@link #NaN} is returned.
	 * 
	 * @param other the other operand
	 * 
	 * @return {@code this + other}
	 */
	public TarsisMathNumber add(TarsisMathNumber other) {
		if (isNaN() || other.isNaN())
			return NaN;

		if (isPlusInfinity() || other.isPlusInfinity())
			return PLUS_INFINITY;

		if (isMinusInfinity() || other.isMinusInfinity())
			return MINUS_INFINITY;

		return cached(new TarsisMathNumber(number.add(other.number)));
	}

	/**
	 * Yields the result of {@code this - other}. If one of them is not a number
	 * (according to {@link #isNaN()}), then {@link #NaN} is returned.
	 * 
	 * @param other the other operand
	 * 
	 * @return {@code this - other}
	 */
	public TarsisMathNumber subtract(TarsisMathNumber other) {
		if (isNaN() || other.isNaN())
			return NaN;

		if (isPlusInfinity() || other.isPlusInfinity())
			return PLUS_INFINITY;

		if (isMinusInfinity() || other.isMinusInfinity())
			return MINUS_INFINITY;

		return cached(new TarsisMathNumber(number.subtract(other.number)));
	}

	/**
	 * Yields the result of {@code this * other}. If one of them is not a number
	 * (according to {@link #isNaN()}), then {@link #NaN} is returned.
	 * 
	 * @param other the other factor
	 * 
	 * @return {@code this * other}
	 */
	public TarsisMathNumber multiply(TarsisMathNumber other) {
		if (isNaN() || other.isNaN())
			return NaN;

		if (is(0) || other.is(0))
			return ZERO;

		if ((isPlusInfinity() && other.isNegative())
				|| (other.isPlusInfinity() && isNegative())
				|| (isMinusInfinity() && other.isPositiveOrZero())
				|| (other.isMinusInfinity() && isPositiveOrZero()))
			return MINUS_INFINITY;

		if ((isMinusInfinity() && other.isNegative())
				|| (other.isMinusInfinity() && isNegative())
				|| (isPlusInfinity() && other.isPositiveOrZero())
				|| (other.isPlusInfinity() && isPositiveOrZero()))
			return PLUS_INFINITY;

		return cached(new TarsisMathNumber(number.multiply(other.number)));
	}

	/**
	 * Yields the result of {@code this / other}. If one of them is not a number
	 * (according to {@link #isNaN()}), then {@link #NaN} is returned. If
	 * {@code other} is zero (according to {@link #is(int)}), then an
	 * {@link ArithmeticException} is thrown. If both are infinite (according to
	 * {@link #isInfinite()}), then {@link #NaN} is returned.
	 * 
	 * @param other the divisor
	 * 
	 * @return {@code this / other}
	 * 
	 * @throws ArithmeticException if {@code other} is 0
	 */
	public TarsisMathNumber divide(TarsisMathNumber other) {
		if (isNaN() || other.isNaN())
			return NaN;

		if (other.is(0))
			throw new ArithmeticException("MathInt divide by zero");

		if (is(0))
			return ZERO;

		if (isInfinite() && other.isInfinite())
			return NaN;

		if (other.isPlusInfinity() || other.isMinusInfinity())
			return ZERO;

		if (isPlusInfinity() || isMinusInfinity())
			if (isPositiveOrZero() == other.isPositiveOrZero())
				return PLUS_INFINITY;
			else
				return MINUS_INFINITY;

		return cached(
				new TarsisMathNumber(number.divide(other.number, 100, RoundingMode.HALF_UP).stripTrailingZeros()));
	}

	@Override
	public int compareTo(TarsisMathNumber other) {
		if (equals(other))
			return 0;

		if (isNaN() && !other.isNaN())
			return -1;

		if (!isNaN() && other.isNaN())
			return 1;

		if (isNaN())
			return 0;

		if (isMinusInfinity() || other.isPlusInfinity() || (isNegative() && other.isPositiveOrZero()))
			return -1;

		if (isPlusInfinity() || other.isMinusInfinity() || (isPositiveOrZero() && other.isNegative()))
			return 1;

		return number.compareTo(other.number);
	}

	/**
	 * Yields the minimum number between {@code this} and {@code other}. If one
	 * of them is not a number (according to {@link #isNaN()}), then
	 * {@link #NaN} is returned.
	 * 
	 * @param other the other number
	 * 
	 * @return the minimum between {@code this} and {@code other}
	 */
	public TarsisMathNumber min(TarsisMathNumber other) {
		if (isNaN() || other.isNaN())
			return NaN;

		if (isMinusInfinity() || other.isPlusInfinity())
			return this;

		if (other.isMinusInfinity() || isPlusInfinity())
			return other;

		return cached(new TarsisMathNumber(number.min(other.number)));
	}

	/**
	 * Yields the maximum number between {@code this} and {@code other}. If one
	 * of them is not a number (according to {@link #isNaN()}), then
	 * {@link #NaN} is returned.
	 * 
	 * @param other the other number
	 * 
	 * @return the maximum between {@code this} and {@code other}
	 */
	public TarsisMathNumber max(TarsisMathNumber other) {
		if (isNaN() || other.isNaN())
			return NaN;

		if (other.isMinusInfinity() || isPlusInfinity())
			return this;

		if (isMinusInfinity() || other.isPlusInfinity())
			return other;

		return cached(new TarsisMathNumber(number.max(other.number)));
	}

	/**
	 * Rounds down this number to the next integer value towards plus infinity
	 * (see {@link RoundingMode#CEILING}). If this number is infinite or is not
	 * a number (according to {@link #isInfinite()} and {@link #isNaN()},
	 * respectively), {@code this} is returned without rounding.
	 * 
	 * @return this number rounded up towards plus infinity
	 */
	public TarsisMathNumber roundUp() {
		if (isInfinite() || isNaN())
			return this;
		return cached(new TarsisMathNumber(number.setScale(0, RoundingMode.CEILING)));
	}

	/**
	 * Rounds down this number to the next integer value towards minus infinity
	 * (see {@link RoundingMode#FLOOR}). If this number is infinite or is not a
	 * number (according to {@link #isInfinite()} and {@link #isNaN()},
	 * respectively), {@code this} is returned without rounding.
	 * 
	 * @return this number rounded down towards minus infinity
	 */
	public TarsisMathNumber roundDown() {
		if (isInfinite() || isNaN())
			return this;
		return cached(new TarsisMathNumber(number.setScale(0, RoundingMode.FLOOR)));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + sign;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TarsisMathNumber other = (TarsisMathNumber) obj;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (sign != other.sign)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return isNaN() ? "NaN" : isMinusInfinity() ? "-Inf" : isPlusInfinity() ? "+Inf" : number.toString();
	}

	/**
	 * Yields the integer value {@code this}.
	 * 
	 * @return the integer value {@code this}
	 */
	public int getNumber() {
		return number.intValue();
	}
}
