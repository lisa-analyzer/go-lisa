package it.unive.golisa.analysis.tarsis;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicBoolean;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.TernaryOperator;
import it.unive.lisa.symbolic.value.UnaryOperator;

public class TarsisIntv extends BaseNonRelationalValueDomain<TarsisIntv> {

		private static final TarsisIntv TOP = new TarsisIntv(null, null, true, false);
		private static final TarsisIntv BOTTOM = new TarsisIntv(null, null, false, true);

		private final boolean isTop, isBottom;

		private final Integer low;
		private final Integer high;

		public TarsisIntv(Integer low, Integer high, boolean isTop, boolean isBottom) {
			this.low = low;
			this.high = high;
			this.isTop = isTop;
			this.isBottom = isBottom;
		}

		public TarsisIntv(Integer low, Integer high) {
			this(low, high, false, false);
		}

		/**
		 * Builds the top interval.
		 */
		public TarsisIntv() {
			this(null, null, true, false);
		}
		

		public Integer getLow() {
			return low;
		}
		
		public Integer getHigh() {
			return high;
		}

		@Override
		public TarsisIntv top() {
			return TOP;
		}

		@Override
		public boolean isTop() {
			return isTop;
		}

		@Override
		public TarsisIntv bottom() {
			return BOTTOM;
		}
		
		@Override
		public boolean isBottom() {
			return isBottom;
		}

		@Override
		public DomainRepresentation representation() {
			if (isTop())
				return Lattice.TOP_REPR;
			else if (isBottom())
				return Lattice.BOTTOM_REPR;

			return new StringRepresentation("[" + (lowIsMinusInfinity() ? "-Inf" : low) + ", " + (highIsPlusInfinity() ? "+Inf" : high) + "]");
		}

		@Override
		protected TarsisIntv evalNullConstant(ProgramPoint pp) {
			return top();
		}

		@Override
		protected TarsisIntv evalNonNullConstant(Constant constant, ProgramPoint pp) {
			if (constant.getValue() instanceof Integer) {
				Integer i = (Integer) constant.getValue();
				return new TarsisIntv(i, i);
			}

			return top();
		}

		@Override
		protected TarsisIntv evalUnaryExpression(UnaryOperator operator, TarsisIntv arg, ProgramPoint pp) {

			switch (operator) {
			case NUMERIC_NEG:
				if (arg.getHigh().equals(arg.getLow())) 
					return new TarsisIntv(-arg.getLow(),-arg.getLow());
				return arg.mul(new TarsisIntv(-1, -1));
			case STRING_LENGTH:
				return new TarsisIntv(0, null);
			default:
				return top();
			}
		}

		@Override
		protected TarsisIntv evalBinaryExpression(BinaryOperator operator, TarsisIntv left, TarsisIntv right, ProgramPoint pp) {
			switch (operator) {
			case NUMERIC_ADD:
				return left.plus(right);
			case NUMERIC_SUB:
				return left.diff(right);
			case NUMERIC_MUL:
				return left.mul(right);
			case NUMERIC_DIV:
				return left.div(right);
			case NUMERIC_MOD:
				return top();
			default:
				return top();
			}
		}

		@Override
		protected TarsisIntv evalTernaryExpression(TernaryOperator operator, TarsisIntv left, TarsisIntv middle, TarsisIntv right, ProgramPoint pp) {
			return top();
		}

		@Override
		protected TarsisIntv lubAux(TarsisIntv other) throws SemanticException {
			Integer newLow = lowIsMinusInfinity() || other.lowIsMinusInfinity() ? null : Math.min(low, other.low);
			Integer newHigh = highIsPlusInfinity() || other.highIsPlusInfinity() ? null : Math.max(high, other.high);
			return new TarsisIntv(newLow, newHigh);
		}

		@Override
		protected TarsisIntv wideningAux(TarsisIntv other) throws SemanticException {
			Integer newLow, newHigh;
			if (other.highIsPlusInfinity() || (!highIsPlusInfinity() && other.high > high))
				newHigh = null;
			else
				newHigh = other.high;

			if (other.lowIsMinusInfinity() || (!lowIsMinusInfinity() && other.low < low))
				newLow = null;
			else
				newLow = other.low;

			return new TarsisIntv(newLow, newHigh);
		}

		@Override
		protected boolean lessOrEqualAux(TarsisIntv other) throws SemanticException {
			return geqLow(low, other.low) && leqHigh(high, other.high);
		}

		@Override
		protected Satisfiability satisfiesAbstractValue(TarsisIntv value, ProgramPoint pp) {
			return Satisfiability.UNKNOWN;
		}

		@Override
		protected Satisfiability satisfiesNullConstant(ProgramPoint pp) {
			return Satisfiability.UNKNOWN;
		}

		@Override
		protected Satisfiability satisfiesNonNullConstant(Constant constant, ProgramPoint pp) {
			return Satisfiability.UNKNOWN;
		}

		@Override
		protected Satisfiability satisfiesUnaryExpression(UnaryOperator operator, TarsisIntv arg, ProgramPoint pp) {
			return Satisfiability.UNKNOWN;
		}

		@Override
		protected Satisfiability satisfiesBinaryExpression(BinaryOperator operator, TarsisIntv left, TarsisIntv right, ProgramPoint pp) {
			return Satisfiability.UNKNOWN;
		}

		@Override
		protected Satisfiability satisfiesTernaryExpression(TernaryOperator operator, TarsisIntv left, TarsisIntv middle,
				TarsisIntv right, ProgramPoint pp) {
			return Satisfiability.UNKNOWN;
		}

		private boolean lowIsMinusInfinity() {
			return low == null;
		}

		private boolean highIsPlusInfinity() {
			return high == null;
		}

		public TarsisIntv plus(TarsisIntv other) {
			Integer newLow, newHigh;

			if (lowIsMinusInfinity() || other.lowIsMinusInfinity())
				newLow = null;
			else
				newLow = low + other.low;

			if (highIsPlusInfinity() || other.highIsPlusInfinity())
				newHigh = null;
			else
				newHigh = high + other.high;

			return new TarsisIntv(newLow, newHigh);
		}

		private TarsisIntv diff(TarsisIntv other) {
			Integer newLow, newHigh;

			if (other.highIsPlusInfinity() || lowIsMinusInfinity())
				newLow = null;
			else
				newLow = low - other.high;

			if (other.lowIsMinusInfinity() || highIsPlusInfinity())
				newHigh = null;
			else
				newHigh = high - other.low;

			return new TarsisIntv(newLow, newHigh);
		}

		public TarsisIntv mul(TarsisIntv other) {
			// this = [l1, h1]
			// other = [l2, h2]

			SortedSet<Integer> boundSet = new TreeSet<>();
			Integer l1 = low;
			Integer h1 = high;
			Integer l2 = other.low;
			Integer h2 = other.high;

			AtomicBoolean lowInf = new AtomicBoolean(false), highInf = new AtomicBoolean(false);

			// l1 * l2
			multiplyBounds(boundSet, l1, l2, lowInf, highInf);

			// x1 * y2
			multiplyBounds(boundSet, l1, h2, lowInf, highInf);

			// x2 * y1
			multiplyBounds(boundSet, h2, l2, lowInf, highInf);

			// x2 * y2
			multiplyBounds(boundSet, h1, h2, lowInf, highInf);

			return new TarsisIntv(lowInf.get() ? null : boundSet.first(), highInf.get() ? null : boundSet.last());
		}

		public TarsisIntv div(TarsisIntv other) {
			// this = [l1, h1]
			// other = [l2, h2]

			SortedSet<Integer> boundSet = new TreeSet<>();
			Integer l1 = low;
			Integer h1 = high;
			Integer l2 = other.low;
			Integer h2 = other.high;

			AtomicBoolean lowInf = new AtomicBoolean(false), highInf = new AtomicBoolean(false);

			// l1 / l2
			divideBounds(boundSet, l1, l2, lowInf, highInf);

			// x1 / y2
			divideBounds(boundSet, l1, h2, lowInf, highInf);

			// x2 / y1
			divideBounds(boundSet, h2, l2, lowInf, highInf);

			// x2 / y2
			divideBounds(boundSet, h1, h2, lowInf, highInf);

			return new TarsisIntv(lowInf.get() ? null : boundSet.first(), highInf.get() ? null : boundSet.last());
		}

		private void multiplyBounds(SortedSet<Integer> boundSet, Integer i, Integer j, AtomicBoolean lowInf,
				AtomicBoolean highInf) {
			if (i == null) {
				if (j == null)
					// -inf * -inf = +inf
					highInf.set(true);
				else {
					if (j > 0)
						// -inf * positive
						lowInf.set(true);
					else if (j < 0)
						// -inf * negative
						highInf.set(true);
					else
						boundSet.add(0);
				}
			} else if (j == null) {
				if (i > 0)
					// -inf * positive
					lowInf.set(true);
				else if (i < 0)
					// -inf * negative
					highInf.set(true);
				else
					boundSet.add(0);
			} else
				boundSet.add(i * j);
		}

		private void divideBounds(SortedSet<Integer> boundSet, Integer i, Integer j, AtomicBoolean lowInf,
				AtomicBoolean highInf) {
			if (i == null) {
				if (j == null)
					// -inf * -inf = +inf
					highInf.set(true);
				else {
					if (j > 0)
						// -inf * positive
						lowInf.set(true);
					else if (j < 0)
						// -inf * negative
						highInf.set(true);

					// division by zero!
				}
			} else if (j == null) {
				if (i > 0)
					// -inf * positive
					lowInf.set(true);
				else if (i < 0)
					// -inf * negative
					highInf.set(true);
				else
					boundSet.add(0);
			} else if (j != 0) {
				boundSet.add((int) Math.ceil(i / (double) j));
				boundSet.add((int) Math.floor(i / (double) j));
			}
			// division by zero!
		}

		/**
		 * Given two interval lower bounds, yields {@code true} iff l1 >= l2, taking
		 * into account -Inf values (i.e., when l1 or l2 is {@code null}.) This
		 * method is used for the implementation of {@link TarsisIntv#lessOrEqualAux}.
		 * 
		 * @param l1 the lower bound of the first interval.
		 * @param l2 the lower bounds of the second interval.
		 * 
		 * @return {@code true} iff iff l1 >= l2, taking into account -Inf values;
		 */
		private boolean geqLow(Integer l1, Integer l2) {
			if (l1 == null) {
				if (l2 == null)
					return true;
				else
					return false;
			} else {
				if (l2 == null)
					return true;
				else
					return l1 >= l2;
			}
		}

		/**
		 * Given two interval upper bounds, yields {@code true} iff h1 <= h2, taking
		 * into account +Inf values (i.e., when h1 or h2 is {@code null}.) This
		 * method is used for the implementation of {@link TarsisIntv#lessOrEqualAux}.
		 * 
		 * @param h1 the upper bound of the first interval.
		 * @param h2 the upper bounds of the second interval.
		 * 
		 * @return {@code true} iff iff h1 <= h2, taking into account +Inf values;
		 */
		private boolean leqHigh(Integer h1, Integer h2) {
			if (h1 == null) {
				if (h2 == null)
					return true;
				else
					return false;
			} else {
				if (h2 == null)
					return false;
				else
					return h1 <= h2;
			}
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + ((high == null) ? 0 : high.hashCode());
			result = prime * result + (isBottom ? 1231 : 1237);
			result = prime * result + (isTop ? 1231 : 1237);
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
			TarsisIntv other = (TarsisIntv) obj;
			if (high == null) {
				if (other.high != null)
					return false;
			} else if (!high.equals(other.high))
				return false;
			if (isBottom != other.isBottom)
				return false;
			if (isTop != other.isTop)
				return false;
			if (low == null) {
				if (other.low != null)
					return false;
			} else if (!low.equals(other.low))
				return false;
			return isTop && other.isTop;
		}
		
		public boolean isFinite() {
			return !isTop() && !isBottom() && this.high != null && this.low != null;
		}
}
