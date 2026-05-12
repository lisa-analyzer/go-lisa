package it.unive.golisa.analysis;

import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.nonrelational.value.BaseNonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.lattices.Satisfiability;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonEq;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonNe;
import it.unive.lisa.symbolic.value.operator.binary.StringConcat;
import it.unive.lisa.symbolic.value.operator.binary.StringContains;
import it.unive.lisa.symbolic.value.operator.binary.StringEndsWith;
import it.unive.lisa.symbolic.value.operator.binary.StringEquals;
import it.unive.lisa.symbolic.value.operator.binary.StringStartsWith;
import it.unive.lisa.symbolic.value.operator.ternary.StringReplace;

/**
 * The string constant propagation abstract domain.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class StringConstantPropagation implements BaseNonRelationalValueDomain<StringConstantPropagationLattice> {

	@Override
	public StringConstantPropagationLattice evalConstant(Constant constant, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		if (constant.getValue() instanceof String)
			return new StringConstantPropagationLattice((String) constant.getValue());
		return top();
	}

	@Override
	public StringConstantPropagationLattice evalUnaryExpression(UnaryExpression expression,
			StringConstantPropagationLattice arg, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		return top();
	}

	@Override
	public StringConstantPropagationLattice evalBinaryExpression(BinaryExpression expression,
			StringConstantPropagationLattice left, StringConstantPropagationLattice right, ProgramPoint pp,
			SemanticOracle oracle) throws SemanticException {
		if (left.isTop() || right.isTop())
			return top();

		if (expression.getOperator() == StringConcat.INSTANCE)
			return new StringConstantPropagationLattice(left.getValue() + right.getValue());
		else
			return top();
	}

	@Override
	public StringConstantPropagationLattice evalTernaryExpression(TernaryExpression expression,
			StringConstantPropagationLattice left, StringConstantPropagationLattice middle,
			StringConstantPropagationLattice right, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {
		if (left.isBottom() || middle.isBottom() || right.isBottom())
			return bottom();
		
		if (left.isTop() || middle.isTop() || right.isTop())
			return top();
		else if (expression.getOperator() == StringReplace.INSTANCE)
			return new StringConstantPropagationLattice(left.getValue().replaceAll(middle.getValue(), right.getValue()));
		else
			return top();
	}

	@Override
	public Satisfiability satisfiesBinaryExpression(BinaryExpression expression, StringConstantPropagationLattice left,
			StringConstantPropagationLattice right, ProgramPoint pp, SemanticOracle oracle) throws SemanticException {

		if (left.isTop() || right.isTop())
			return Satisfiability.UNKNOWN;

		if (expression.getOperator() == StringContains.INSTANCE)
			return left.getValue().contains(right.getValue()) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		else if (expression.getOperator() == StringStartsWith.INSTANCE)
			return left.getValue().startsWith(right.getValue()) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		else if (expression.getOperator() == StringEndsWith.INSTANCE)
			return left.getValue().endsWith(right.getValue()) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		else if (expression.getOperator() == StringEquals.INSTANCE)
			return left.getValue().equals(right.getValue()) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		else if (expression.getOperator() == ComparisonEq.INSTANCE)
			return left.getValue().equals(right.getValue()) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		else if (expression.getOperator() == ComparisonNe.INSTANCE)
			return !left.getValue().equals(right.getValue()) ? Satisfiability.SATISFIED
					: Satisfiability.NOT_SATISFIED;
		else
			return Satisfiability.UNKNOWN;
	}

	@Override
	public ValueEnvironment<StringConstantPropagationLattice> assumeBinaryExpression(
			ValueEnvironment<StringConstantPropagationLattice> environment, BinaryExpression expression,
			ProgramPoint src, ProgramPoint dest, SemanticOracle oracle) throws SemanticException {
	
		if (expression.getOperator() == StringEquals.INSTANCE || expression.getOperator() == ComparisonEq.INSTANCE) {
			ValueEnvironment<StringConstantPropagationLattice> env = environment;
			if (expression.getLeft() instanceof Identifier)
				 env = assign(environment, (Identifier) expression.getLeft(), (ValueExpression) expression.getRight(), src, oracle);
			else if (expression.getRight() instanceof Identifier)
				env = assign(environment, (Identifier) expression.getRight(), (ValueExpression) expression.getLeft(), src, oracle);
			return env;
		} else
			return environment;
	}

	@Override
	public StringConstantPropagationLattice top() {
		return StringConstantPropagationLattice.TOP;
	}

	@Override
	public StringConstantPropagationLattice bottom() {
		return StringConstantPropagationLattice.BOTTOM;
	}
}