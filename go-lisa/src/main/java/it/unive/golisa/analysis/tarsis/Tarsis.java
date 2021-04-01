package it.unive.golisa.analysis.tarsis;

import it.unive.lisa.analysis.BaseLattice;
import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.value.NonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.NullConstant;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.Skip;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.TernaryOperator;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.UnaryOperator;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.tarsis.AutomatonString;
import it.unive.tarsis.automata.Automata;


public class Tarsis extends BaseLattice<Tarsis> implements NonRelationalValueDomain<Tarsis> {

	private static final Tarsis TOP = new Tarsis();
	private static final Tarsis BOTTOM = new Tarsis(new AutomatonString(Automata.mkEmptyLanguage()), new TarsisIntv(null, null, false, true) , false, true);

	private final AutomatonString stringValue;
	private final TarsisIntv intValue;

	private final boolean isTop;
	private final boolean isBottom;

	public Tarsis() {
		this(new AutomatonString(), new TarsisIntv(), true, false);
	}	

	private Tarsis(AutomatonString stringValue, TarsisIntv intValue) {
		this(stringValue, intValue, stringValue.getAutomaton().equals(Automata.mkEmptyLanguage()) && intValue.isTop(), stringValue.isEqualTo(BOTTOM.stringValue) && intValue.isTop());
	}

	private Tarsis(AutomatonString stringValue, TarsisIntv intValue, boolean isTop, boolean isBottom) {
		this.stringValue = stringValue;
		this.intValue = intValue;
		this.isBottom = isBottom;
		this.isTop = isTop;
	}

	@Override
	public boolean isTop() {
		return isTop;
	}

	@Override
	public boolean isBottom() {
		return isBottom;
	}

	@Override
	public Tarsis top() {
		return TOP;
	}

	@Override
	public Tarsis bottom() {
		return BOTTOM;
	}

	private AutomatonString bottomString() {
		return new AutomatonString(Automata.mkEmptyLanguage());
	}

	@Override
	public String representation() {
		if (isTop())
			return "TOP";
		if (isBottom())
			return "BOTTOM";
		return stringValue.getAutomaton().equals(Automata.mkEmptyLanguage()) ? intValue.representation() : stringValue.toString();
	}

	
	public Tarsis eval(ValueExpression expression, ValueEnvironment<Tarsis> environment, ProgramPoint pp) {
		if (expression instanceof Identifier)
			return environment.getState((Identifier) expression);

		if (expression instanceof NullConstant)
			return evalNullConstant();

		if (expression instanceof Constant)
			return evalNonNullConstant((Constant) expression, pp);

		if (expression instanceof Skip)
			return bottom();

		if (expression instanceof PushAny)
			return top();

		if (expression instanceof UnaryExpression) {
			UnaryExpression unary = (UnaryExpression) expression;

			Tarsis arg = eval((ValueExpression) unary.getExpression(), environment, pp);
			if (arg.isTop() || arg.isBottom())
				return arg;

			return evalUnaryExpression(unary.getOperator(), arg, pp);
		}

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;

			Tarsis left = eval((ValueExpression) binary.getLeft(), environment, pp);
			if (left.isBottom())
				return left;

			Tarsis right = eval((ValueExpression) binary.getRight(), environment, pp);
			if (right.isBottom())
				return right;

			return evalBinaryExpression(binary.getOperator(), left, right, pp);
		}

		if (expression instanceof TernaryExpression) {
			TernaryExpression ternary = (TernaryExpression) expression;

			Tarsis left = eval((ValueExpression) ternary.getLeft(), environment, pp);
			if (left.isBottom())
				return left;

			Tarsis middle = eval((ValueExpression) ternary.getMiddle(), environment, pp);
			if (middle.isBottom())
				return middle;

			Tarsis right = eval((ValueExpression) ternary.getRight(), environment, pp);
			if (right.isBottom())
				return right;

			return evalTernaryExpression(ternary.getOperator(), left, middle, right);
		}

		return bottom();
	}

	protected Tarsis evalNullConstant() {
		return top();
	}

	protected Tarsis evalNonNullConstant(Constant constant, ProgramPoint pp) {
		if (constant.getValue() instanceof String) {
			String str = (String) constant.getValue();
			return new Tarsis(new AutomatonString(str), intValue.bottom(), false, false);
		}

		if (constant.getValue() instanceof Integer) 
			return new Tarsis(new AutomatonString(Automata.mkEmptyLanguage()), intValue.eval(constant, null, pp), false, false);

		return top();
	}

	protected Tarsis evalUnaryExpression(UnaryOperator operator, Tarsis arg, ProgramPoint pp) {
		switch(operator) {
		case NUMERIC_NEG:
			return new Tarsis(bottomString(), intValue.evalUnaryExpression(UnaryOperator.NUMERIC_NEG, arg.intValue, pp));
		case STRING_LENGTH:
			it.unive.tarsis.AutomatonString.Interval result = arg.stringValue.length();
			return new Tarsis(bottomString(), new TarsisIntv(result.getLower(), result.getUpper()));
		default:
			return top();
		}
	} 

	protected Tarsis evalBinaryExpression(BinaryOperator operator, Tarsis left, Tarsis right, ProgramPoint pp) {
		switch(operator) {
		case STRING_INDEX_OF:
			// Checking top cases
			if (left.stringValue.getAutomaton().equals(Automata.mkTopAutomaton()))
				return new Tarsis(bottomString(), new TarsisIntv(-1, null));
			if (right.stringValue.getAutomaton().equals(Automata.mkTopAutomaton()))
				return new Tarsis(bottomString(), new TarsisIntv(-1, left.stringValue.length().getUpper()));
	
			it.unive.tarsis.AutomatonString.Interval result = left.stringValue.indexOf(right.stringValue);
			return new Tarsis(bottomString(), new TarsisIntv(result.getLower(), result.getUpper()));
		case NUMERIC_ADD:
			return new Tarsis(new AutomatonString(Automata.mkEmptyLanguage()), left.intValue.plus(right.intValue));
		case STRING_CONCAT:
			return new Tarsis(left.stringValue.concat(right.stringValue), intValue.bottom());
		default:
			return top();
		}
	}

	protected Tarsis evalTernaryExpression(TernaryOperator operator, Tarsis left, Tarsis middle, Tarsis right) {	
		switch(operator) {
		case STRING_REPLACE:
			return new Tarsis(left.stringValue.replace(middle.stringValue, right.stringValue), intValue.bottom());
		case STRING_SUBSTRING:
			TarsisIntv iIntv = middle.intValue;
			TarsisIntv jIntv = right.intValue;

			AutomatonString result = new AutomatonString(Automata.mkEmptyLanguage());

			if (iIntv.isFinite() && jIntv.isFinite()) {
				for (int i = iIntv.getLow(); i <= iIntv.getHigh(); i++)
					for (int j = jIntv.getLow(); j <= jIntv.getHigh(); j++)
						if (i <= j)
							result = result.lub(left.stringValue.substring(i, j));
				return new Tarsis(result, intValue.bottom());

			}

			return new Tarsis(new AutomatonString(Automata.factors(left.stringValue.getAutomaton())), intValue.bottom());
		default:
			return top();
		}
	}

	protected Satisfiability satisfiesAbstractValue(Tarsis value) {
		return Satisfiability.UNKNOWN;
	}

	protected Satisfiability satisfiesNullConstant() {
		return Satisfiability.UNKNOWN;
	}

	protected Satisfiability satisfiesNonNullConstant(Constant constant) {
		return Satisfiability.UNKNOWN;
	}

	protected Satisfiability satisfiesUnaryExpression(UnaryOperator operator, Tarsis arg) {
		return Satisfiability.UNKNOWN;
	}

	protected Satisfiability satisfiesBinaryExpression(BinaryOperator operator, Tarsis left, Tarsis right) {
		if (left.isTop() || right.isTop())
			return Satisfiability.UNKNOWN;

		switch(operator) {
		case COMPARISON_EQ:
			break;
		case COMPARISON_GE:
			break;
		case COMPARISON_GT:
			break;
		case COMPARISON_LE:
			break;
		case COMPARISON_LT:
			break;
		case COMPARISON_NE:
			break;
		case STRING_CONTAINS:
			if (left.stringValue.contains(right.stringValue))
				return Satisfiability.SATISFIED;
			if (left.stringValue.mayContain(right.stringValue))
				return Satisfiability.UNKNOWN;
			return Satisfiability.NOT_SATISFIED;	
		case STRING_ENDS_WITH:
			if (left.stringValue.endsWith(right.stringValue))
				return Satisfiability.SATISFIED;
			if (left.stringValue.mayEndWith(right.stringValue))
				return Satisfiability.UNKNOWN;
			return Satisfiability.NOT_SATISFIED;		
		case STRING_EQUALS:
			break;
		case STRING_INDEX_OF:
			break;
		case STRING_STARTS_WITH:
			if (left.stringValue.startsWith(right.stringValue))
				return Satisfiability.SATISFIED;
			if (left.stringValue.mayStartWith(right.stringValue))
				return Satisfiability.UNKNOWN;
			return Satisfiability.NOT_SATISFIED;
		case TYPE_CAST:
			break;
		case TYPE_CHECK:
			break;
		default:
			return Satisfiability.UNKNOWN;
		}
		return Satisfiability.UNKNOWN;
	}

	protected Satisfiability satisfiesTernaryExpression(TernaryOperator operator, Tarsis left, Tarsis middle,
			Tarsis right) {
		return Satisfiability.UNKNOWN;
	}

	@Override
	protected Tarsis lubAux(Tarsis other) throws SemanticException {
		AutomatonString stringLub = stringValue.lub(other.stringValue);
		TarsisIntv intLub = intValue.lub(other.intValue);
		return new Tarsis(stringLub, intLub);
	}

	@Override
	protected Tarsis wideningAux(Tarsis other) throws SemanticException {
		AutomatonString stringWid = stringValue.widen(other.stringValue);
		TarsisIntv intWid = intValue.widening(other.intValue);
		return new Tarsis(stringWid, intWid);
	}

	@Override
	protected boolean lessOrEqualAux(Tarsis other) throws SemanticException {
		return Automata.isContained(stringValue.getAutomaton(), other.stringValue.getAutomaton())
				&& intValue.lessOrEqual(other.intValue);
	}

	@Override
	public int hashCode() {
		if (isTop())
			return 1;
		else if (isBottom())
			return 2;

		final int prime = 31;
		int result = 1;
		result = prime * result + (isBottom ? 1231 : 1237);
		result = prime * result + (isTop ? 1231 : 1237);
		result = prime * result + ((stringValue == null) ? 0 : stringValue.hashCode());
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
		Tarsis other = (Tarsis) obj;
		if (isBottom != other.isBottom)
			return false;
		if (isTop != other.isTop)
			return false;
		if (stringValue == null) {
			if (other.stringValue != null)
				return false;
		} else if (!stringValue.equals(other.stringValue))
			return false;
		return isTop && other.isTop;
	}



	@Override
	public Satisfiability satisfies(ValueExpression expression, ValueEnvironment<Tarsis> environment, ProgramPoint pp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String toString() {
		return representation();
	}

	@Override
	public boolean tracksIdentifiers(Identifier id) {
		return !id.getDynamicType().isPointerType();
	}

	@Override
	public boolean canProcess(SymbolicExpression expression) {
		return !expression.getDynamicType().isPointerType();
	}
}
