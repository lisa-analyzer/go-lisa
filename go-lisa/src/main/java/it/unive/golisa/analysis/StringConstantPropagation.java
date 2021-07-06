package it.unive.golisa.analysis;

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
import it.unive.lisa.symbolic.value.TernaryOperator;
import it.unive.lisa.symbolic.value.UnaryOperator;
import it.unive.lisa.symbolic.value.ValueExpression;

public class StringConstantPropagation extends BaseNonRelationalValueDomain<StringConstantPropagation> {

	private static final StringConstantPropagation TOP = new StringConstantPropagation(true, false);
	private static final StringConstantPropagation BOTTOM = new StringConstantPropagation(false, true);

	private final boolean isTop, isBottom;

	private final String value;

	/**
	 * Builds the top abstract value.
	 */
	public StringConstantPropagation() {
		this(null, true, false);
	}

	private StringConstantPropagation(String value, boolean isTop, boolean isBottom) {
		this.value = value;
		this.isTop = isTop;
		this.isBottom = isBottom;
	}

	private StringConstantPropagation(String value) {
		this(value, false, false);
	}

	private StringConstantPropagation(boolean isTop, boolean isBottom) {
		this(null, isTop, isBottom);
	}

	@Override
	public StringConstantPropagation top() {
		return TOP;
	}

	@Override
	public boolean isTop() {
		return isTop;
	}

	@Override
	public StringConstantPropagation bottom() {
		return BOTTOM;
	}

	@Override
	public DomainRepresentation representation() {
		if (isBottom())
			return Lattice.BOTTOM_REPR;
		if (isTop())
			return Lattice.TOP_REPR;

		return new StringRepresentation(value.toString());
	}

	@Override
	protected StringConstantPropagation evalNullConstant(ProgramPoint pp) {
		return top();
	}

	@Override
	protected StringConstantPropagation evalNonNullConstant(Constant constant, ProgramPoint pp) {
		if (constant.getValue() instanceof String)
			return new StringConstantPropagation((String) constant.getValue());
		return top();
	}

	@Override
	protected StringConstantPropagation evalUnaryExpression(UnaryOperator operator, StringConstantPropagation arg,
			ProgramPoint pp) {
		return top();
	}

	@Override
	protected StringConstantPropagation evalBinaryExpression(BinaryOperator operator, StringConstantPropagation left,
			StringConstantPropagation right, ProgramPoint pp) {
		
		if (left.isTop() || right.isTop())
			return top();
		
		switch (operator) {
		case STRING_CONCAT:
			return new StringConstantPropagation(left.value + right.value);
		default:
			return top();
		}
	}

	@Override
	protected StringConstantPropagation evalTernaryExpression(TernaryOperator operator,
			StringConstantPropagation left,
			StringConstantPropagation middle, StringConstantPropagation right, ProgramPoint pp) {
		
		if (left.isTop() || middle.isTop || right.isTop())
			return top();
		
		switch (operator) {
		case STRING_REPLACE:
			return new StringConstantPropagation(left.value.replaceAll(middle.value, right.value));
		default:
			return top();
		}
	}

	@Override
	protected StringConstantPropagation lubAux(StringConstantPropagation other) throws SemanticException {
		return TOP;
	}

	@Override
	protected StringConstantPropagation wideningAux(StringConstantPropagation other) throws SemanticException {
		return lubAux(other);
	}

	@Override
	protected boolean lessOrEqualAux(StringConstantPropagation other) throws SemanticException {
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (isBottom ? 1231 : 1237);
		result = prime * result + (isTop ? 1231 : 1237);
		result = prime * result + ((value == null) ? 0 : value.hashCode());
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
		StringConstantPropagation other = (StringConstantPropagation) obj;
		if (isBottom != other.isBottom)
			return false;
		if (isTop != other.isTop)
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	@Override
	protected Satisfiability satisfiesBinaryExpression(BinaryOperator operator, StringConstantPropagation left,
			StringConstantPropagation right, ProgramPoint pp) {

		if (left.isTop() || right.isTop())
			return Satisfiability.UNKNOWN;

		switch (operator) {
		case STRING_CONTAINS:
			return left.value.contains(right.value) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		case STRING_STARTS_WITH:
			return left.value.startsWith(right.value) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		case STRING_ENDS_WITH:
			return left.value.endsWith(right.value) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		case STRING_EQUALS:
			return left.value.equals(right.value) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		case COMPARISON_EQ:
			return left.value.equals(right.value) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		case COMPARISON_NE:
			return !left.value.equals(right.value) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		default:
			return Satisfiability.UNKNOWN;
		}
	}
	
	public String getString() {
		return value;
	}

	@Override
	protected ValueEnvironment<StringConstantPropagation> assumeBinaryExpression(
			ValueEnvironment<StringConstantPropagation> environment, BinaryOperator operator, ValueExpression left,
			ValueExpression right, ProgramPoint pp) throws SemanticException {
		switch (operator) {
		case STRING_EQUALS:
		case COMPARISON_EQ:
			if (left instanceof Identifier)
				environment = environment.assign((Identifier) left, right, pp);
			else if (right instanceof Identifier)
				environment = environment.assign((Identifier) right, left, pp);
			return environment;
		default:
			return environment;
		}
	}
}