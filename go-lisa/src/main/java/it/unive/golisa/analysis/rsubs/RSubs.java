package it.unive.golisa.analysis.rsubs;

import it.unive.lisa.analysis.BaseLattice;
import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.impl.numeric.Interval;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.PairRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
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

public class RSubs extends BaseLattice<RSubs> implements ValueDomain<RSubs> {

	private static final RSubs TOP = new RSubs(true, false);
	private static final RSubs BOTTOM = new RSubs(false, false);

	private final RelationalSubstringDomain string;
	private final ValueEnvironment<Interval> num;

	private final boolean isTop;
	private final boolean isBottom;


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
	public RSubs assign(Identifier id, ValueExpression expression, ProgramPoint pp) throws SemanticException {
		if (processableByStringDomain(expression))
			return new RSubs(string.assign(id, expression, pp), num);

		if (processableByNumericalDomain(expression))
			return new RSubs(string, num.assign(id, expression, pp));

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

			switch(unary.getOperator()) {
			case LOGICAL_NOT:
				return false;
			case NUMERIC_NEG:
				return processableByNumericalDomain((ValueExpression) unary.getExpression());
			case STRING_LENGTH:
				return false;
			case TYPEOF:
				return false;
			default:
				break;
			}
		}

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;
			SymbolicExpression left = binary.getLeft();
			SymbolicExpression right = binary.getRight();

			switch (binary.getOperator()) {
			case NUMERIC_ADD:
			case NUMERIC_DIV:
			case NUMERIC_MOD:
			case NUMERIC_MUL:
			case NUMERIC_SUB:
				return processableByNumericalDomain((ValueExpression) left) && processableByNumericalDomain((ValueExpression) right);
			case COMPARISON_EQ:
			case COMPARISON_GE:
			case COMPARISON_GT:
			case COMPARISON_LE:
			case COMPARISON_LT:
			case COMPARISON_NE:
			case LOGICAL_AND:
			case LOGICAL_OR:
			case STRING_CONCAT:
			case STRING_CONTAINS:
			case STRING_ENDS_WITH:
			case STRING_EQUALS:
			case STRING_INDEX_OF:
			case STRING_STARTS_WITH:
			case TYPE_CAST:
			case TYPE_CHECK:
			default:
				return false;
			}
		}

		if (expression instanceof TernaryExpression) {
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

		if (expression instanceof UnaryExpression) {
			UnaryExpression unary = (UnaryExpression) expression;

			switch(unary.getOperator()) {
			case LOGICAL_NOT:
				return false;
			case NUMERIC_NEG:
				return false;
			case STRING_LENGTH:
				return false;
			case TYPEOF:
				return false;
			default:
				break;
			}
		}

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;

			switch (binary.getOperator()) {
			case COMPARISON_EQ:
			case COMPARISON_GE:
			case COMPARISON_GT:
			case COMPARISON_LE:
			case COMPARISON_LT:
			case COMPARISON_NE:
			case LOGICAL_AND:
			case LOGICAL_OR:
			case NUMERIC_ADD:
			case NUMERIC_DIV:
			case NUMERIC_MOD:
			case NUMERIC_MUL:
			case NUMERIC_SUB:
				return false;
			case STRING_CONCAT:
				return true;
			case STRING_CONTAINS:
			case STRING_ENDS_WITH:
			case STRING_EQUALS:
			case STRING_INDEX_OF:
			case STRING_STARTS_WITH:
			case TYPE_CAST:
			case TYPE_CHECK:
			default:
				return false;

			}
		}

		if (expression instanceof TernaryExpression) {
			return true;
		}

		return false;
	}

	@Override
	public RSubs smallStepSemantics(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		return new RSubs(string, num, isTop, isBottom);
	}

	@Override
	public RSubs assume(ValueExpression expression, ProgramPoint pp) throws SemanticException {
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
	public Satisfiability satisfies(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		// TODO satisfies
		return Satisfiability.UNKNOWN;
	}

	@Override
	public DomainRepresentation representation() {
		if (isTop())
			return Lattice.TOP_REPR;
		if (isBottom())
			return Lattice.BOTTOM_REPR;
		return new PairRepresentation(string,  num, StringRepresentation::new, StringRepresentation::new);
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
	protected RSubs lubAux(RSubs other) throws SemanticException {
		return new RSubs(string.lub(other.string), num.lub(other.num));
	}

	@Override
	protected RSubs wideningAux(RSubs other) throws SemanticException {
		return new RSubs(string.widening(other.string), num.widening(other.num));

	}

	@Override
	protected boolean lessOrEqualAux(RSubs other) throws SemanticException {
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
}
