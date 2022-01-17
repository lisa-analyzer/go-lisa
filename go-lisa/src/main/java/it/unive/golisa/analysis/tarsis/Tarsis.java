package it.unive.golisa.analysis.tarsis;

import it.unive.lisa.analysis.BaseLattice;
import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.SemanticDomain.Satisfiability;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.value.NonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
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
import it.unive.lisa.symbolic.value.operator.binary.StringConcat;
import it.unive.lisa.symbolic.value.operator.binary.StringContains;
import it.unive.lisa.symbolic.value.operator.binary.StringEndsWith;
import it.unive.lisa.symbolic.value.operator.binary.StringIndexOf;
import it.unive.lisa.symbolic.value.operator.binary.StringStartsWith;
import it.unive.lisa.symbolic.value.operator.binary.TypeCast;
import it.unive.lisa.symbolic.value.operator.binary.TypeConv;
import it.unive.lisa.symbolic.value.operator.ternary.StringReplace;
import it.unive.lisa.symbolic.value.operator.ternary.StringSubstring;
import it.unive.lisa.symbolic.value.operator.ternary.TernaryOperator;
import it.unive.lisa.symbolic.value.operator.unary.LogicalNegation;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.symbolic.value.operator.unary.StringLength;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;
import it.unive.tarsis.AutomatonString;
import it.unive.tarsis.automata.Automata;
import it.unive.tarsis.automata.Automaton;

public class Tarsis extends BaseLattice<Tarsis> implements NonRelationalValueDomain<Tarsis> {

	private static final Tarsis TOP = new Tarsis();
	private static final Tarsis BOTTOM = new Tarsis(new AutomatonString(Automata.mkEmptyLanguage()),
			new TarsisIntv().bottom(), false, true);

	private final AutomatonString stringValue;
	private final TarsisIntv intValue;

	private final boolean isTop;
	private final boolean isBottom;

	public Tarsis() {
		this(new AutomatonString(), new TarsisIntv(), true, false);
	}

	private Tarsis(AutomatonString stringValue, TarsisIntv intValue) {
		this(stringValue, intValue, stringValue.getAutomaton().equals(Automata.mkEmptyLanguage()) && intValue.isTop(),
				stringValue.isEqualTo(BOTTOM.stringValue) && intValue.isTop());
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
	public DomainRepresentation representation() {
		if (isTop())
			return Lattice.TOP_REPR;
		if (isBottom())
			return Lattice.BOTTOM_REPR;

		return stringValue.getAutomaton().equals(Automata.mkEmptyLanguage()) ? intValue.representation()
				: new StringRepresentation(stringValue.toString());
	}

	@Override
	public Tarsis eval(ValueExpression expression, ValueEnvironment<Tarsis> environment, ProgramPoint pp)
			throws SemanticException {
		if (expression instanceof Identifier)
			return environment.getState((Identifier) expression);

		if (expression instanceof NullConstant)
			return top();

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

			if (binary.getOperator() == TypeCast.INSTANCE)
				return evalTypeCast(binary, left, right, pp);

			if (binary.getOperator() == TypeConv.INSTANCE)
				return evalTypeConv(binary, left, right, pp);

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

	protected Tarsis evalNonNullConstant(Constant constant, ProgramPoint pp) {
		if (constant.getValue() instanceof String) {
			String str = (String) constant.getValue();
			return new Tarsis(new AutomatonString(str), intValue.bottom(), false, false);
		}

		if (constant.getValue() instanceof Integer)
			try {
				return new Tarsis(new AutomatonString(Automata.mkEmptyLanguage()), intValue.eval(constant, null, pp),
						false, false);
			} catch (SemanticException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		return top();
	}

	protected Tarsis evalUnaryExpression(UnaryOperator operator, Tarsis arg, ProgramPoint pp) {
		if (operator == NumericNegation.INSTANCE)
			return new Tarsis(bottomString(),
					intValue.evalUnaryExpression(NumericNegation.INSTANCE, arg.intValue, pp));
		else if (operator == StringLength.INSTANCE) {
			it.unive.tarsis.AutomatonString.Interval result = arg.stringValue.length();
			return new Tarsis(bottomString(),
					new TarsisIntv(new TarsisMathNumber(result.getLower()), new TarsisMathNumber(result.getUpper())));
		} else
			return top();
	}

	protected Tarsis evalBinaryExpression(BinaryOperator operator, Tarsis left, Tarsis right, ProgramPoint pp)
			throws SemanticException {
		if (operator == StringIndexOf.INSTANCE) {
			Automaton leftAutomaton = left.stringValue.getAutomaton();
			Automaton rightAutomaton = left.stringValue.getAutomaton();

			if (leftAutomaton.hasCycle() || rightAutomaton.hasCycle())
				return new Tarsis(bottomString(),
						new TarsisIntv(TarsisMathNumber.MINUS_ONE, TarsisMathNumber.PLUS_INFINITY));

			TarsisIntv result = intValue.bottom();

			for (String str : leftAutomaton.getLanguage())
				for (String src : rightAutomaton.getLanguage())
					if (str.contains(src))
						result = result.lub(new TarsisIntv(new TarsisMathNumber(str.indexOf(src)),
								new TarsisMathNumber(str.indexOf(src))));
					else
						result = result.lub(new TarsisIntv(TarsisMathNumber.MINUS_ONE, TarsisMathNumber.MINUS_ONE));

			if (result.getHigh().isInfinite())
				result = new TarsisIntv(result.getLow(), new TarsisMathNumber(leftAutomaton.maxLengthString()));

			return new Tarsis(bottomString(), result);
		} else if (operator == NumericNonOverflowingAdd.INSTANCE)
			return new Tarsis(bottomString(), left.intValue.plus(right.intValue));
		else if (operator == StringConcat.INSTANCE)
			return new Tarsis(left.stringValue.concat(right.stringValue), intValue.bottom());
		else
			return top();
	}

	protected Tarsis evalTernaryExpression(TernaryOperator operator, Tarsis left, Tarsis middle, Tarsis right) {
		if (operator == StringReplace.INSTANCE) 
			return new Tarsis(left.stringValue.replace(middle.stringValue, right.stringValue), intValue.bottom());
		else if (operator == StringSubstring.INSTANCE) {
			TarsisIntv iIntv = middle.intValue;
			TarsisIntv jIntv = right.intValue;

			AutomatonString result = new AutomatonString(Automata.mkEmptyLanguage());

			if (iIntv.isFinite() && jIntv.isFinite()) {
				for (int i = iIntv.getLowNumber(); i <= iIntv.getHighNumber(); i++)
					for (int j = jIntv.getLowNumber(); j <= jIntv.getHighNumber(); j++)
						if (i <= j)
							result = result.lub(left.stringValue.substring(i, j));
				return new Tarsis(result, intValue.bottom());
			}

			return new Tarsis(new AutomatonString(Automata.factors(left.stringValue.getAutomaton())),
					intValue.bottom());
		} else
			return top();
	}

	protected Satisfiability satisfiesAbstractValue(Tarsis value, ProgramPoint pp) {
		return Satisfiability.UNKNOWN;
	}

	protected Satisfiability satisfiesNullConstant(ProgramPoint pp) {
		return Satisfiability.UNKNOWN;
	}

	protected Satisfiability satisfiesNonNullConstant(Constant constant, ProgramPoint pp) {
		return Satisfiability.UNKNOWN;
	}

	protected Satisfiability satisfiesUnaryExpression(UnaryOperator operator, Tarsis arg, ProgramPoint pp) {
		return Satisfiability.UNKNOWN;
	}

	protected Satisfiability satisfiesBinaryExpression(BinaryOperator operator, Tarsis left, Tarsis right,
			ProgramPoint pp) {
		if (left.isTop() || right.isTop())
			return Satisfiability.UNKNOWN;

		if (operator == ComparisonLe.INSTANCE
				|| operator == ComparisonLt.INSTANCE
				|| operator == ComparisonNe.INSTANCE
				|| operator == ComparisonGt.INSTANCE
				|| operator == ComparisonGe.INSTANCE
				|| operator == ComparisonEq.INSTANCE) 
			return intValue.satisfiesBinaryExpression(operator, left.intValue, right.intValue, pp);
		else if (operator == StringContains.INSTANCE) {
			if (left.stringValue.contains(right.stringValue))
				return Satisfiability.SATISFIED;
			if (left.stringValue.mayContain(right.stringValue))
				return Satisfiability.UNKNOWN;
			return Satisfiability.NOT_SATISFIED;
		} else if (operator == StringEndsWith.INSTANCE) {
			if (left.stringValue.endsWith(right.stringValue))
				return Satisfiability.SATISFIED;
			if (left.stringValue.mayEndWith(right.stringValue))
				return Satisfiability.UNKNOWN;
			return Satisfiability.NOT_SATISFIED;
		} else if (operator == StringStartsWith.INSTANCE) {
			if (left.stringValue.startsWith(right.stringValue))
				return Satisfiability.SATISFIED;
			if (left.stringValue.mayStartWith(right.stringValue))
				return Satisfiability.UNKNOWN;
			return Satisfiability.NOT_SATISFIED;
		} else 
			return Satisfiability.UNKNOWN;
	}

	protected Satisfiability satisfiesTernaryExpression(TernaryOperator operator, Tarsis left, Tarsis middle,
			Tarsis right, ProgramPoint pp) {
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
	public Satisfiability satisfies(ValueExpression expression, ValueEnvironment<Tarsis> environment, ProgramPoint pp)
			throws SemanticException {
		if (expression instanceof Identifier)
			return satisfiesAbstractValue(environment.getState((Identifier) expression), pp);

		if (expression instanceof NullConstant)
			return satisfiesNullConstant(pp);

		if (expression instanceof Constant)
			return satisfiesNonNullConstant((Constant) expression, pp);

		if (expression instanceof UnaryExpression) {
			UnaryExpression unary = (UnaryExpression) expression;

			if (unary.getOperator() == LogicalNegation.INSTANCE)
				return satisfies((ValueExpression) unary.getExpression(), environment, pp).negate();
			else {
				Tarsis arg = eval((ValueExpression) unary.getExpression(), environment, pp);
				if (arg.isBottom())
					return Satisfiability.BOTTOM;

				return satisfiesUnaryExpression(unary.getOperator(), arg, pp);
			}
		}

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;

			if (binary.getOperator() == LogicalAnd.INSTANCE)
				return satisfies((ValueExpression) binary.getLeft(), environment, pp)
						.and(satisfies((ValueExpression) binary.getRight(), environment, pp));
			else if (binary.getOperator() == LogicalOr.INSTANCE)
				return satisfies((ValueExpression) binary.getLeft(), environment, pp)
						.or(satisfies((ValueExpression) binary.getRight(), environment, pp));
			else {
				Tarsis left = eval((ValueExpression) binary.getLeft(), environment, pp);
				if (left.isBottom())
					return Satisfiability.BOTTOM;

				Tarsis right = eval((ValueExpression) binary.getRight(), environment, pp);
				if (right.isBottom())
					return Satisfiability.BOTTOM;

				return satisfiesBinaryExpression(binary.getOperator(), left, right, pp);
			}
		}

		if (expression instanceof TernaryExpression) {
			TernaryExpression ternary = (TernaryExpression) expression;

			Tarsis left = eval((ValueExpression) ternary.getLeft(), environment, pp);
			if (left.isBottom())
				return Satisfiability.BOTTOM;

			Tarsis middle = eval((ValueExpression) ternary.getMiddle(), environment, pp);
			if (middle.isBottom())
				return Satisfiability.BOTTOM;

			Tarsis right = eval((ValueExpression) ternary.getRight(), environment, pp);
			if (right.isBottom())
				return Satisfiability.BOTTOM;

			return satisfiesTernaryExpression(ternary.getOperator(), left, middle, right, pp);
		}

		return Satisfiability.UNKNOWN;
	}

	@Override
	public String toString() {
		return representation().toString();
	}

	@Override
	public boolean tracksIdentifiers(Identifier id) {
		return !id.getDynamicType().isPointerType() || id.getDynamicType().isUntyped();
	}

	@Override
	public boolean canProcess(SymbolicExpression expression) {
		return !expression.getDynamicType().isPointerType();
	}

	@Override
	public ValueEnvironment<Tarsis> assume(ValueEnvironment<Tarsis> environment, ValueExpression expression,
			ProgramPoint pp) throws SemanticException {
		return environment;
	}

	@Override
	public Tarsis glb(Tarsis other) throws SemanticException {
		// TODO glb on stringValue
		return new Tarsis(stringValue, intValue.glb(other.intValue));
	}

	protected Tarsis evalTypeConv(BinaryExpression conv, Tarsis left, Tarsis right, ProgramPoint pp) {
		return conv.getTypes().isEmpty() ? bottom() : left;
	}

	/**
	 * Yields the evaluation of a type cast expression.
	 * 
	 * @param cast  the type casted expression
	 * @param left  the left expression, namely the expression to be casted
	 * @param right the right expression, namely the types to which left should
	 *                  be casted
	 * @param pp    the program point that where this operation is being
	 *                  evaluated
	 * 
	 * @return the evaluation of the type cast expression
	 */
	protected Tarsis evalTypeCast(BinaryExpression cast, Tarsis left, Tarsis right, ProgramPoint pp) {
		return cast.getTypes().isEmpty() ? bottom() : left;
	}
}
