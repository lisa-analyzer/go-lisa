package it.unive.golisa.analysis;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonEq;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonNe;
import it.unive.lisa.symbolic.value.operator.binary.StringConcat;
import it.unive.lisa.symbolic.value.operator.binary.StringContains;
import it.unive.lisa.symbolic.value.operator.binary.StringEndsWith;
import it.unive.lisa.symbolic.value.operator.binary.StringEquals;
import it.unive.lisa.symbolic.value.operator.binary.StringStartsWith;
import it.unive.lisa.symbolic.value.operator.ternary.StringReplace;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;

/**
 * The string constant propagation abstract domain.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class StringConstantPropagation implements BaseNonRelationalValueDomain<StringConstantPropagation> {

	private static final StringConstantPropagation TOP = new StringConstantPropagation(true, false);
	private static final StringConstantPropagation BOTTOM = new StringConstantPropagation(false, true);

	private final boolean isTop, isBottom;

	/**
	 * The string constant representing a value of this domain element.
	 */
	private final String value;

	/**
	 * Builds the string constant propagation.
	 */
	public StringConstantPropagation() {
		this(null, true, false);
	}

	/**
	 * Builds the string constant propagation.
	 * 
	 * @param value    the string value of this domain
	 * @param isTop    if it is top
	 * @param isBottom if it is bottom
	 */
	private StringConstantPropagation(String value, boolean isTop, boolean isBottom) {
		this.value = value;
		this.isTop = isTop;
		this.isBottom = isBottom;
	}

	/**
	 * Builds the string constant propagation.
	 * 
	 * @param value the string value of this domain
	 */
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
			return Lattice.bottomRepresentation();
		if (isTop())
			return Lattice.topRepresentation();

		return new StringRepresentation(value.toString());
	}

	@Override
	public StringConstantPropagation evalNullConstant(ProgramPoint pp) {
		return top();
	}

	@Override
	public StringConstantPropagation evalNonNullConstant(Constant constant, ProgramPoint pp) {
		if (constant.getValue() instanceof String)
			return new StringConstantPropagation((String) constant.getValue());
		return top();
	}

	@Override
	public StringConstantPropagation evalUnaryExpression(UnaryOperator operator, StringConstantPropagation arg,
			ProgramPoint pp) {
		return top();
	}

	@Override
	public StringConstantPropagation evalBinaryExpression(BinaryOperator operator, StringConstantPropagation left,
			StringConstantPropagation right, ProgramPoint pp) {

		if (left.isTop() || right.isTop())
			return top();

		if (operator == StringConcat.INSTANCE)
			return new StringConstantPropagation(left.value + right.value);
		else
			return top();
	}

	@Override
	public StringConstantPropagation evalTernaryExpression(TernaryOperator operator,
			StringConstantPropagation left,
			StringConstantPropagation middle, StringConstantPropagation right, ProgramPoint pp) {

		if (left.isTop() || middle.isTop || right.isTop())
			return top();
		else if (operator == StringReplace.INSTANCE)
			return new StringConstantPropagation(left.value.replaceAll(middle.value, right.value));
		else
			return top();

	}

	@Override
	public StringConstantPropagation lubAux(StringConstantPropagation other) throws SemanticException {
		return TOP;
	}

	@Override
	public StringConstantPropagation wideningAux(StringConstantPropagation other) throws SemanticException {
		return lubAux(other);
	}

	@Override
	public boolean lessOrEqualAux(StringConstantPropagation other) throws SemanticException {
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
	public Satisfiability satisfiesBinaryExpression(BinaryOperator operator, StringConstantPropagation left,
			StringConstantPropagation right, ProgramPoint pp) {

		if (left.isTop() || right.isTop())
			return Satisfiability.UNKNOWN;

		if (operator == StringContains.INSTANCE)
			return left.value.contains(right.value) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		else if (operator == StringStartsWith.INSTANCE)
			return left.value.startsWith(right.value) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		else if (operator == StringEndsWith.INSTANCE)
			return left.value.endsWith(right.value) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		else if (operator == StringEquals.INSTANCE)
			return left.value.equals(right.value) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		else if (operator == ComparisonEq.INSTANCE)
			return left.value.equals(right.value) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		else if (operator == ComparisonNe.INSTANCE)
			return !left.value.equals(right.value) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		else
			return Satisfiability.UNKNOWN;
	}

	/**
	 * Yields the string value.
	 * 
	 * @return the string value
	 */
	public String getString() {
		return value;
	}

	@Override
	public ValueEnvironment<StringConstantPropagation> assumeBinaryExpression(
			ValueEnvironment<StringConstantPropagation> environment, BinaryOperator operator, ValueExpression left,
			ValueExpression right, ProgramPoint pp) throws SemanticException {
		if (operator == StringEquals.INSTANCE || operator == ComparisonEq.INSTANCE) {
			if (left instanceof Identifier)
				environment = environment.assign((Identifier) left, right, pp);
			else if (right instanceof Identifier)
				environment = environment.assign((Identifier) right, left, pp);
			return environment;
		} else
			return environment;
	}
}