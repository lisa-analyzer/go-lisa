package it.unive.golisa.analysis.tarsis;

/**
 * An interval with integer bounds.
 * 
 * @author <a href="mailto:luca.negrini@unive.it">Luca Negrini</a>
 */
public class TarsisIntInterval {

	/**
	 * The interval {@code [-Inf, +Inf]}.
	 */
	public static final TarsisIntInterval INFINITY = new TarsisIntInterval();

	/**
	 * The interval {@code [0, 0]}.
	 */
	public static final TarsisIntInterval ZERO = new TarsisIntInterval(0, 0);

	/**
	 * The interval {@code [1, 1]}.
	 */
	public static final TarsisIntInterval ONE = new TarsisIntInterval(1, 1);

	/**
	 * The interval {@code [-1, -1]}.
	 */
	public static final TarsisIntInterval MINUS_ONE = new TarsisIntInterval(-1, -1);

	private final TarsisMathNumber low;

	private final TarsisMathNumber high;

	private TarsisIntInterval() {
		this(TarsisMathNumber.MINUS_INFINITY, TarsisMathNumber.PLUS_INFINITY);
	}

	/**
	 * Builds a new interval.
	 * 
	 * @param low  the lower bound
	 * @param high the upper bound
	 * 
	 * @throws IllegalArgumentException if {@code low > high}
	 */
	public TarsisIntInterval(int low, int high) {
		this(new TarsisMathNumber(low), new TarsisMathNumber(high));
	}

	/**
	 * Builds a new interval.
	 * 
	 * @param low  the lower bound
	 * @param high the upper bound
	 * 
	 * @throws IllegalArgumentException if {@code low > high}
	 */
	public TarsisIntInterval(Integer low, Integer high) {
		this(low == null ? TarsisMathNumber.MINUS_INFINITY : new TarsisMathNumber(low),
				high == null ? TarsisMathNumber.PLUS_INFINITY : new TarsisMathNumber(high));
	}

	/**
	 * Builds a new interval.
	 * 
	 * @param low  the lower bound
	 * @param high the upper bound
	 * 
	 * @throws IllegalArgumentException if {@code low > high}
	 */
	public TarsisIntInterval(TarsisMathNumber low, TarsisMathNumber high) {
		if (low.compareTo(high) > 0)
			throw new IllegalArgumentException("Lower bound is bigger than higher bound");

		this.low = low;
		this.high = high;
	}

	/**
	 * Yields the upper bound of this interval.
	 * 
	 * @return the upper bound of this interval
	 */
	public TarsisMathNumber getHigh() {
		return high;
	}

	/**
	 * Yields the lower bound of this interval.
	 * 
	 * @return the lower bound of this interval
	 */
	public TarsisMathNumber getLow() {
		return low;
	}

	/**
	 * Yields {@code true} if the lower bound of this interval is set to minus
	 * infinity.
	 * 
	 * @return {@code true} if that condition holds
	 */
	public boolean lowIsMinusInfinity() {
		return low.isMinusInfinity();
	}

	/**
	 * Yields {@code true} if the upper bound of this interval is set to plus
	 * infinity.
	 * 
	 * @return {@code true} if that condition holds
	 */
	public boolean highIsPlusInfinity() {
		return high.isPlusInfinity();
	}

	/**
	 * Yields {@code true} if this is interval is not finite, that is, if at
	 * least one bound is set to infinity.
	 * 
	 * @return {@code true} if that condition holds
	 */
	public boolean isInfinite() {
		return this == INFINITY || (highIsPlusInfinity() || lowIsMinusInfinity());
	}

	/**
	 * Yields {@code true} if this is interval is finite, that is, if neither
	 * bound is set to infinity.
	 * 
	 * @return {@code true} if that condition holds
	 */
	public boolean isFinite() {
		return !isInfinite();
	}

	/**
	 * Yields {@code true} if this is the interval representing infinity, that
	 * is, {@code [-Inf, +Inf]}.
	 * 
	 * @return {@code true} if that condition holds
	 */
	public boolean isInfinity() {
		return this == INFINITY;
	}

	/**
	 * Yields {@code true} if this is a singleton interval, that is, if the
	 * lower bound and the upper bound are the same.
	 * 
	 * @return {@code true} if that condition holds
	 */
	public boolean isSingleton() {
		return isFinite() && low.equals(high);
	}

	/**
	 * Yields {@code true} if this is a singleton interval containing only
	 * {@code n}.
	 *
	 * @param n the integer to test
	 * 
	 * @return {@code true} if that condition holds
	 */
	public boolean is(int n) {
		return isSingleton() && low.is(n);
	}

	private static TarsisIntInterval cacheAndRound(TarsisIntInterval i) {
		if (i.is(0))
			return ZERO;
		if (i.is(1))
			return ONE;
		if (i.is(-1))
			return MINUS_ONE;
		return new TarsisIntInterval(i.low.roundDown(), i.high.roundUp());
	}

	/**
	 * Performs the interval addition between {@code this} and {@code other}.
	 * 
	 * @param other the other interval
	 * 
	 * @return {@code this + other}
	 */
	public TarsisIntInterval plus(TarsisIntInterval other) {
		if (isInfinity() || other.isInfinity())
			return INFINITY;

		return cacheAndRound(new TarsisIntInterval(low.add(other.low), high.add(other.high)));
	}

	/**
	 * Performs the interval subtraction between {@code this} and {@code other}.
	 * 
	 * @param other the other interval
	 * 
	 * @return {@code this - other}
	 */
	public TarsisIntInterval diff(TarsisIntInterval other) {
		if (isInfinity() || other.isInfinity())
			return INFINITY;

		return cacheAndRound(new TarsisIntInterval(low.subtract(other.high), high.subtract(other.low)));
	}

	private static TarsisMathNumber min(TarsisMathNumber... nums) {
		if (nums.length == 0)
			throw new IllegalArgumentException("No numbers provided");

		TarsisMathNumber min = nums[0];
		for (int i = 1; i < nums.length; i++)
			min = min.min(nums[i]);

		return min;
	}

	private static TarsisMathNumber max(TarsisMathNumber... nums) {
		if (nums.length == 0)
			throw new IllegalArgumentException("No numbers provided");

		TarsisMathNumber max = nums[0];
		for (int i = 1; i < nums.length; i++)
			max = max.max(nums[i]);

		return max;
	}

	/**
	 * Performs the interval multiplication between {@code this} and
	 * {@code other}.
	 * 
	 * @param other the other interval
	 * 
	 * @return {@code this * other}
	 */
	public TarsisIntInterval mul(TarsisIntInterval other) {
		if (is(0) || other.is(0))
			return ZERO;
		if (isInfinity() || other.isInfinity())
			return INFINITY;

		if (low.compareTo(TarsisMathNumber.ZERO) >= 0 && other.low.compareTo(TarsisMathNumber.ZERO) >= 0)
			return cacheAndRound(new TarsisIntInterval(low.multiply(other.low), high.multiply(other.high)));

		TarsisMathNumber ll = low.multiply(other.low);
		TarsisMathNumber lh = low.multiply(other.high);
		TarsisMathNumber hl = high.multiply(other.low);
		TarsisMathNumber hh = high.multiply(other.high);
		return cacheAndRound(new TarsisIntInterval(min(ll, lh, hl, hh), max(ll, lh, hl, hh)));
	}

	/**
	 * Performs the interval division between {@code this} and {@code other}.
	 * 
	 * @param other       the other interval
	 * @param ignoreZero  if {@code true}, causes the division to ignore the
	 *                        fact that {@code other} might contain 0, producing
	 *                        a smaller result
	 * @param errorOnZero whether or not an {@link ArithmeticException} should
	 *                        be thrown immediately if {@code other} contains
	 *                        zero
	 * 
	 * @return {@code this / other}
	 * 
	 * @throws ArithmeticException if {@code other} contains 0 and
	 *                                 {@code errorOnZero} is set to
	 *                                 {@code true}
	 */
	public TarsisIntInterval div(TarsisIntInterval other, boolean ignoreZero, boolean errorOnZero) {
		if (errorOnZero && (other.is(0) || other.includes(ZERO)))
			throw new ArithmeticException("IntInterval divide by zero");

		if (is(0))
			return ZERO;

		if (!other.includes(ZERO))
			return mul(new TarsisIntInterval(TarsisMathNumber.ONE.divide(other.high),
					TarsisMathNumber.ONE.divide(other.low)));
		else if (other.high.is(0))
			return mul(new TarsisIntInterval(TarsisMathNumber.MINUS_INFINITY, TarsisMathNumber.ONE.divide(other.low)));
		else if (other.low.is(0))
			return mul(new TarsisIntInterval(TarsisMathNumber.ONE.divide(other.high), TarsisMathNumber.PLUS_INFINITY));
		else if (ignoreZero)
			return mul(new TarsisIntInterval(TarsisMathNumber.ONE.divide(other.low),
					TarsisMathNumber.ONE.divide(other.high)));
		else {
			TarsisIntInterval lower = mul(
					new TarsisIntInterval(TarsisMathNumber.MINUS_INFINITY, TarsisMathNumber.ONE.divide(other.low)));
			TarsisIntInterval higher = mul(
					new TarsisIntInterval(TarsisMathNumber.ONE.divide(other.high), TarsisMathNumber.PLUS_INFINITY));

			if (lower.includes(higher))
				return lower;
			else if (higher.includes(lower))
				return higher;
			else
				return cacheAndRound(new TarsisIntInterval(lower.low.compareTo(higher.low) > 0 ? higher.low : lower.low,
						lower.high.compareTo(higher.high) < 0 ? higher.high : lower.high));
		}
	}

	/**
	 * Yields {@code true} if this interval includes the given one.
	 * 
	 * @param other the other interval
	 * 
	 * @return {@code true} if it is included, {@code false} otherwise
	 */
	public boolean includes(TarsisIntInterval other) {
		return low.compareTo(other.low) <= 0 && high.compareTo(other.high) >= 0;
	}

	/**
	 * Yields {@code true} if this interval intersects with the given one.
	 * 
	 * @param other the other interval
	 * 
	 * @return {@code true} if those intersects, {@code false} otherwise
	 */
	public boolean intersects(TarsisIntInterval other) {
		return includes(other) || other.includes(this)
				|| (high.compareTo(other.low) >= 0 && high.compareTo(other.high) <= 0)
				|| (other.high.compareTo(low) >= 0 && other.high.compareTo(high) <= 0);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((high == null) ? 0 : high.hashCode());
		result = prime * result + ((low == null) ? 0 : low.hashCode());
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
		TarsisIntInterval other = (TarsisIntInterval) obj;
		if (high == null) {
			if (other.high != null)
				return false;
		} else if (!high.equals(other.high))
			return false;
		if (low == null) {
			if (other.low != null)
				return false;
		} else if (!low.equals(other.low))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "[" + low + ", " + high + "]";
	}
}
