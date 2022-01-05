package it.unive.golisa.analysis.tarsis;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.UnaryOperator;
import it.unive.lisa.symbolic.value.ValueExpression;

public class TarsisIntv extends BaseNonRelationalValueDomain<TarsisIntv> {

	private static final TarsisIntv ZERO = new TarsisIntv(TarsisIntInterval.ZERO);
	private static final TarsisIntv TOP = new TarsisIntv(TarsisIntInterval.INFINITY);
	private static final TarsisIntv BOTTOM = new TarsisIntv(null);

	final TarsisIntInterval interval;

	private TarsisIntv(TarsisIntInterval interval) {
		this.interval = interval;
	}

	public TarsisIntv(TarsisMathNumber low, TarsisMathNumber high) {
		this(new TarsisIntInterval(low, high));
	}

	/**
	 * Builds the top interval.
	 */
	public TarsisIntv() {
		this(TarsisIntInterval.INFINITY);
	}

	@Override
	public TarsisIntv top() {
		return TOP;
	}

	@Override
	public boolean isTop() {
		return interval != null && interval.isInfinity();
	}

	@Override
	public TarsisIntv bottom() {
		return BOTTOM;
	}

	@Override
	public boolean isBottom() {
		return interval == null;
	}

	@Override
	public DomainRepresentation representation() {
		if (isBottom())
			return Lattice.BOTTOM_REPR;

		return new StringRepresentation(interval.toString());
	}

	@Override
	protected TarsisIntv evalNonNullConstant(Constant constant, ProgramPoint pp) {
		if (constant.getValue() instanceof Integer) {
			Integer i = (Integer) constant.getValue();
			return new TarsisIntv(new TarsisMathNumber(i), new TarsisMathNumber(i));
		}

		return top();
	}

	@Override
	protected TarsisIntv evalUnaryExpression(UnaryOperator operator, TarsisIntv arg, ProgramPoint pp) {
		switch (operator) {
		case NUMERIC_NEG:
			if (arg.isTop())
				return top();
			return new TarsisIntv(arg.interval.mul(TarsisIntInterval.MINUS_ONE));
		case STRING_LENGTH:
			return new TarsisIntv(TarsisMathNumber.ZERO, TarsisMathNumber.PLUS_INFINITY);
		default:
			return top();
		}
	}

	private boolean is(int n) {
		return !isBottom() && interval.is(n);
	}

	@Override
	protected TarsisIntv evalBinaryExpression(BinaryOperator operator, TarsisIntv left, TarsisIntv right,
			ProgramPoint pp) {
		if (operator != BinaryOperator.NUMERIC_NON_OVERFLOWING_DIV && (left.isTop() || right.isTop()))
			// with div, we can return zero or bottom even if one of the
			// operands is top
			return top();

		switch (operator) {
		case NUMERIC_NON_OVERFLOWING_ADD:
			return new TarsisIntv(left.interval.plus(right.interval));
		case NUMERIC_NON_OVERFLOWING_SUB:
			return new TarsisIntv(left.interval.diff(right.interval));
		case NUMERIC_NON_OVERFLOWING_MUL:
			if (left.is(0) || right.is(0))
				return ZERO;
			return new TarsisIntv(left.interval.mul(right.interval));
		case NUMERIC_NON_OVERFLOWING_DIV:
			if (right.is(0))
				return bottom();
			if (left.is(0))
				return ZERO;
			if (left.isTop() || right.isTop())
				return top();

			return new TarsisIntv(left.interval.div(right.interval, false, false));
		default:
			return top();
		}
	}

	@Override
	protected TarsisIntv lubAux(TarsisIntv other) throws SemanticException {
		TarsisMathNumber newLow = interval.getLow().min(other.interval.getLow());
		TarsisMathNumber newHigh = interval.getHigh().max(other.interval.getHigh());
		return newLow.isMinusInfinity() && newHigh.isPlusInfinity() ? top() : new TarsisIntv(newLow, newHigh);
	}

	@Override
	protected TarsisIntv glbAux(TarsisIntv other) {
		TarsisMathNumber newLow = interval.getLow().max(other.interval.getLow());
		TarsisMathNumber newHigh = interval.getHigh().min(other.interval.getHigh());

		if (newLow.compareTo(newHigh) > 0)
			return bottom();
		return newLow.isMinusInfinity() && newHigh.isPlusInfinity() ? top() : new TarsisIntv(newLow, newHigh);
	}

	@Override
	protected TarsisIntv wideningAux(TarsisIntv other) throws SemanticException {
		TarsisMathNumber newLow, newHigh;
		if (other.interval.getHigh().compareTo(interval.getHigh()) > 0)
			newHigh = TarsisMathNumber.PLUS_INFINITY;
		else
			newHigh = interval.getHigh();

		if (other.interval.getLow().compareTo(interval.getLow()) < 0)
			newLow = TarsisMathNumber.MINUS_INFINITY;
		else
			newLow = interval.getLow();

		return newLow.isMinusInfinity() && newHigh.isPlusInfinity() ? top() : new TarsisIntv(newLow, newHigh);
	}

	@Override
	protected boolean lessOrEqualAux(TarsisIntv other) throws SemanticException {
		return other.interval.includes(interval);
	}

	@Override
	protected Satisfiability satisfiesBinaryExpression(BinaryOperator operator, TarsisIntv left, TarsisIntv right,
			ProgramPoint pp) {

		if (left.isTop() || right.isTop())
			return Satisfiability.UNKNOWN;

		switch (operator) {
		case COMPARISON_EQ:
			TarsisIntv glb = null;
			try {
				glb = left.glb(right);
			} catch (SemanticException e) {
				return Satisfiability.UNKNOWN;
			}

			if (glb.isBottom())
				return Satisfiability.NOT_SATISFIED;
			else if (left.interval.isSingleton() && left.equals(right))
				return Satisfiability.SATISFIED;
			return Satisfiability.UNKNOWN;
		case COMPARISON_GE:
			return satisfiesBinaryExpression(BinaryOperator.COMPARISON_LE, right, left, pp);
		case COMPARISON_GT:
			return satisfiesBinaryExpression(BinaryOperator.COMPARISON_LT, right, left, pp);
		case COMPARISON_LE:
			glb = null;
			try {
				glb = left.glb(right);
			} catch (SemanticException e) {
				return Satisfiability.UNKNOWN;
			}

			if (glb.isBottom())
				return Satisfiability.fromBoolean(left.interval.getHigh().compareTo(right.interval.getLow()) <= 0);
			// we might have a singleton as glb if the two intervals share a
			// bound
			if (glb.interval.isSingleton() && left.interval.getHigh().compareTo(right.interval.getLow()) == 0)
				return Satisfiability.SATISFIED;
			return Satisfiability.UNKNOWN;
		case COMPARISON_LT:
			glb = null;
			try {
				glb = left.glb(right);
			} catch (SemanticException e) {
				return Satisfiability.UNKNOWN;
			}

			if (glb.isBottom())
				return Satisfiability.fromBoolean(left.interval.getHigh().compareTo(right.interval.getLow()) < 0);
			return Satisfiability.UNKNOWN;
		case COMPARISON_NE:
			glb = null;
			try {
				glb = left.glb(right);
			} catch (SemanticException e) {
				return Satisfiability.UNKNOWN;
			}
			if (glb.isBottom())
				return Satisfiability.SATISFIED;
			return Satisfiability.UNKNOWN;
		default:
			return Satisfiability.UNKNOWN;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((interval == null) ? 0 : interval.hashCode());
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
		if (interval == null) {
			if (other.interval != null)
				return false;
		} else if (!interval.equals(other.interval))
			return false;
		return true;
	}

	@Override
	protected ValueEnvironment<TarsisIntv> assumeBinaryExpression(
			ValueEnvironment<TarsisIntv> environment, BinaryOperator operator, ValueExpression left,
			ValueExpression right, ProgramPoint pp) throws SemanticException {

		Identifier id;
		TarsisIntv eval;
		boolean rightIsExpr;
		if (left instanceof Identifier) {
			eval = eval(right, environment, pp);
			id = (Identifier) left;
			rightIsExpr = true;
		} else if (right instanceof Identifier) {
			eval = eval(left, environment, pp);
			id = (Identifier) right;
			rightIsExpr = false;
		} else
			return environment;

		if (eval.isBottom())
			return environment.bottom();

		boolean lowIsMinusInfinity = eval.interval.lowIsMinusInfinity();
		TarsisIntv low_inf = new TarsisIntv(eval.interval.getLow(), TarsisMathNumber.PLUS_INFINITY);
		TarsisIntv lowp1_inf = new TarsisIntv(eval.interval.getLow().add(TarsisMathNumber.ONE),
				TarsisMathNumber.PLUS_INFINITY);
		TarsisIntv inf_high = new TarsisIntv(TarsisMathNumber.MINUS_INFINITY, eval.interval.getHigh());
		TarsisIntv inf_highm1 = new TarsisIntv(TarsisMathNumber.MINUS_INFINITY,
				eval.interval.getHigh().subtract(TarsisMathNumber.ONE));

		switch (operator) {
		case COMPARISON_EQ:
			return environment.putState(id, eval);
		case COMPARISON_GE:
			if (rightIsExpr)
				return lowIsMinusInfinity ? environment : environment.putState(id, low_inf);
			else
				return environment.putState(id, inf_high);
		case COMPARISON_GT:
			if (rightIsExpr)
				return lowIsMinusInfinity ? environment : environment.putState(id, lowp1_inf);
			else
				return environment.putState(id, lowIsMinusInfinity ? eval : inf_highm1);
		case COMPARISON_LE:
			if (rightIsExpr)
				return environment.putState(id, inf_high);
			else
				return lowIsMinusInfinity ? environment : environment.putState(id, low_inf);
		case COMPARISON_LT:
			if (rightIsExpr)
				return environment.putState(id, lowIsMinusInfinity ? eval : inf_highm1);
			else
				return lowIsMinusInfinity ? environment : environment.putState(id, lowp1_inf);
		default:
			return environment;
		}
	}

	public boolean isFinite() {
		return interval.isFinite();
	}

	public TarsisIntv plus(TarsisIntv other) {
		return new TarsisIntv(this.interval.plus(other.interval));
	}

	public int getHighNumber() {
		return interval.getHigh().getNumber();
	}

	public int getLowNumber() {
		return interval.getLow().getNumber();
	}

	public TarsisMathNumber getHigh() {
		return interval.getHigh();
	}

	public TarsisMathNumber getLow() {
		return interval.getLow();
	}
}