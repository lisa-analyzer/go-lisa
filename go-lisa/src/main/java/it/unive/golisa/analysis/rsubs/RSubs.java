package it.unive.golisa.analysis.rsubs;

import it.unive.lisa.analysis.BaseLattice;
import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.lattices.Satisfiability;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.NullConstant;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.Skip;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonEq;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonGe;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonGt;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonLe;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonLt;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonNe;
import it.unive.lisa.symbolic.value.operator.binary.LogicalAnd;
import it.unive.lisa.symbolic.value.operator.binary.LogicalOr;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingAdd;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingDiv;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingMod;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingMul;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingSub;
import it.unive.lisa.symbolic.value.operator.binary.StringConcat;
import it.unive.lisa.symbolic.value.operator.binary.StringContains;
import it.unive.lisa.symbolic.value.operator.binary.StringEndsWith;
import it.unive.lisa.symbolic.value.operator.binary.StringEquals;
import it.unive.lisa.symbolic.value.operator.binary.StringIndexOf;
import it.unive.lisa.symbolic.value.operator.binary.StringStartsWith;
import it.unive.lisa.symbolic.value.operator.binary.TypeCast;
import it.unive.lisa.symbolic.value.operator.binary.TypeCheck;
import it.unive.lisa.symbolic.value.operator.unary.LogicalNegation;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.symbolic.value.operator.unary.StringLength;
import it.unive.lisa.symbolic.value.operator.unary.TypeOf;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.lisa.util.representation.ListRepresentation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;
import java.util.List;
import java.util.function.Predicate;

/**
 * The reduced product between relational substring abstract domain and
 * intervals.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class RSubs implements BaseLattice<RSubs>, ValueDomain<RSubs> {

	private static final RSubs TOP = new RSubs(true, false);
	private static final RSubs BOTTOM = new RSubs(false, false);

	private final RelationalSubstringDomain string;
	private final ValueEnvironment<Interval> num;

	private final boolean isTop;
	private final boolean isBottom;

	/**
	 * Builds the top abstract value.
	 */
	public RSubs() {
		this(new RelationalSubstringDomain(), new ValueEnvironment<Interval>(new Interval()), true, false);
	}

	private RSubs(boolean isTop, boolean isBottom) {
		this(new RelationalSubstringDomain(), new ValueEnvironment<Interval>(new Interval()), isTop, isBottom);
	}

	private RSubs(RelationalSubstringDomain string, ValueEnvironment<Interval> num) {
		this(string, num, false, false);
	}

	private RSubs(RelationalSubstringDomain string, ValueEnvironment<Interval> num, boolean isTop, boolean isBottom) {
		this.string = string;
		this.num = num;
		this.isTop = isTop;
		this.isBottom = isBottom;
	}

	@Override
	public RSubs assign(Identifier id, ValueExpression expression, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		if (processableByStringDomain(expression))
			return new RSubs(string.assign(id, expression, pp, oracle), num);

		if (processableByNumericalDomain(expression))
			return new RSubs(string, num.assign(id, expression, pp, oracle));

		return new RSubs(string, num.top());
	}

	private boolean processableByNumericalDomain(ValueExpression expression) {

		if (expression instanceof Identifier) {
			Identifier id = (Identifier) expression;
			return num.getState(id).isBottom() ? false : true;
		}

		if (expression instanceof NullConstant)
			return false;

		if (expression instanceof Constant) {
			Constant c = (Constant) expression;
			return c.getValue() instanceof Integer ? true : false;
		}

		if (expression instanceof Skip)
			return false;

		if (expression instanceof PushAny)
			return true;

		if (expression instanceof UnaryExpression) {
			UnaryExpression unary = (UnaryExpression) expression;
			UnaryOperator op = unary.getOperator();
			if (op == LogicalNegation.INSTANCE)
				return false;
			else if (op == NumericNegation.INSTANCE)
				return processableByNumericalDomain((ValueExpression) unary.getExpression());
			else if (op == StringLength.INSTANCE)
				return false;
			else if (op == TypeOf.INSTANCE)
				return false;
		} else if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;
			SymbolicExpression left = binary.getLeft();
			SymbolicExpression right = binary.getRight();

			BinaryOperator op = binary.getOperator();
			if (op == NumericNonOverflowingAdd.INSTANCE || op == NumericNonOverflowingDiv.INSTANCE
					|| op == NumericNonOverflowingMod.INSTANCE || op == NumericNonOverflowingMul.INSTANCE
					|| op == NumericNonOverflowingSub.INSTANCE)
				return processableByNumericalDomain((ValueExpression) left)
						&& processableByNumericalDomain((ValueExpression) right);
			else
				return false;
		}

		return false;
	}

	private boolean processableByStringDomain(ValueExpression expression) {

		if (expression instanceof Identifier) {
			Identifier id = (Identifier) expression;
			return string.getState(id).isBottom() ? false : true;
		}

		if (expression instanceof NullConstant)
			return false;

		if (expression instanceof Constant) {
			Constant c = (Constant) expression;
			return c.getValue() instanceof String ? true : false;
		}

		if (expression instanceof Skip)
			return false;

		if (expression instanceof PushAny)
			return true;

		if (expression instanceof UnaryExpression)
			return false;

		if (expression instanceof BinaryExpression) {
			BinaryOperator op = ((BinaryExpression) expression).getOperator();
			if (op == ComparisonEq.INSTANCE
					|| op == ComparisonGe.INSTANCE
					|| op == ComparisonGt.INSTANCE
					|| op == ComparisonLe.INSTANCE
					|| op == ComparisonLt.INSTANCE
					|| op == ComparisonLe.INSTANCE
					|| op == ComparisonNe.INSTANCE
					|| op == LogicalAnd.INSTANCE
					|| op == LogicalOr.INSTANCE
					|| op == NumericNonOverflowingAdd.INSTANCE
					|| op == NumericNonOverflowingMod.INSTANCE
					|| op == NumericNonOverflowingSub.INSTANCE
					|| op == NumericNonOverflowingMul.INSTANCE
					|| op == NumericNonOverflowingDiv.INSTANCE
					|| op == StringContains.INSTANCE
					|| op == StringEndsWith.INSTANCE
					|| op == StringEquals.INSTANCE
					|| op == StringIndexOf.INSTANCE
					|| op == StringStartsWith.INSTANCE
					|| op == TypeCast.INSTANCE
					|| op == TypeCheck.INSTANCE)
				return false;
			else if (op == StringConcat.INSTANCE)
				return true;
			else
				return false;
		}

		if (expression instanceof TernaryExpression)
			return true;

		return false;
	}

	@Override
	public RSubs smallStepSemantics(ValueExpression expression, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		return new RSubs(string, num, isTop, isBottom);
	}

	@Override
	public RSubs assume(ValueExpression expression, ProgramPoint src, ProgramPoint dest, SemanticOracle oracle)
			throws SemanticException {
		return new RSubs(string, num, isTop, isBottom);
	}

	@Override
	public RSubs forgetIdentifier(Identifier id) throws SemanticException {
		if (isTop() || isBottom())
			return new RSubs(isTop, isBottom);
		else
			return new RSubs(string.forgetIdentifier(id), num.forgetIdentifier(id));
	}

	@Override
	public RSubs forgetIdentifiersIf(Predicate<Identifier> test) throws SemanticException {
		if (isTop() || isBottom())
			return new RSubs(isTop, isBottom);
		else
			return new RSubs(string.forgetIdentifiersIf(test), num.forgetIdentifiersIf(test));
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		// TODO satisfies
		return Satisfiability.UNKNOWN;
	}

	@Override
	public StructuredRepresentation representation() {
		if (isTop())
			return Lattice.topRepresentation();
		if (isBottom())
			return Lattice.bottomRepresentation();
		return new ListRepresentation(List.of(string, num), StringRepresentation::new);
	}

	@Override
	public RSubs top() {
		return TOP;
	}

	@Override
	public RSubs bottom() {
		return BOTTOM;
	}

	@Override
	public RSubs lubAux(RSubs other) throws SemanticException {
		return new RSubs(string.lub(other.string), num.lub(other.num));
	}

	@Override
	public RSubs wideningAux(RSubs other) throws SemanticException {
		return new RSubs(string.widening(other.string), num.widening(other.num));

	}

	@Override
	public boolean lessOrEqualAux(RSubs other) throws SemanticException {
		return string.lessOrEqual(other.string) && num.lessOrEqual(other.num);
	}

	@Override
	public int hashCode() {
		if (isTop())
			return 1;
		if (isBottom())
			return 2;

		final int prime = 31;
		int result = 1;
		result = prime * result + (isBottom ? 1231 : 1237);
		result = prime * result + (isTop ? 1231 : 1237);
		result = prime * result + ((num == null) ? 0 : num.hashCode());
		result = prime * result + ((string == null) ? 0 : string.hashCode());
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
		RSubs other = (RSubs) obj;
		if (isBottom != other.isBottom)
			return false;
		if (isTop != other.isTop)
			return false;
		if (num == null) {
			if (other.num != null)
				return false;
		} else if (!num.equals(other.num))
			return false;
		if (string == null) {
			if (other.string != null)
				return false;
		} else if (!string.equals(other.string))
			return false;
		return isTop && other.isTop;
	}

	@Override
	public String toString() {
		return representation().toString();
	}

	@Override
	public RSubs pushScope(ScopeToken token) throws SemanticException {
		return new RSubs(string.pushScope(token), num.pushScope(token));
	}

	@Override
	public RSubs popScope(ScopeToken token) throws SemanticException {
		return new RSubs(string.popScope(token), num.popScope(token));
	}

	@Override
	public boolean knowsIdentifier(Identifier id) {
		return string.knowsIdentifier(id) || num.knowsIdentifier(id);
	}
}
