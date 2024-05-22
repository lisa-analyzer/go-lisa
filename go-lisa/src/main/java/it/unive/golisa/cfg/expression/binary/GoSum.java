package it.unive.golisa.cfg.expression.binary;

import java.util.Set;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingAdd;
import it.unive.lisa.symbolic.value.operator.binary.StringConcat;
import it.unive.lisa.type.Type;

/**
 * A Go numerical sum expression (e.g., x + y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoSum extends it.unive.lisa.program.cfg.statement.BinaryExpression implements GoBinaryNumericalOperation {

	/**
	 * Builds the sum expression.
	 *
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left-hand side of this expression
	 * @param right    the right-hand side of this expression
	 */
	public GoSum(CFG cfg, SourceCodeLocation location, Expression left, Expression right) {
		super(cfg, location, "+", left, right);
	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(InterproceduralAnalysis<A> arg0,
			AnalysisState<A> state, SymbolicExpression left, SymbolicExpression right, StatementStore<A> arg4)
			throws SemanticException {
		BinaryOperator op;
		Type type;
		AnalysisState<A> result = state.bottom();

		if (left.getStaticType().isNumericType() && right.getStaticType().isNumericType()) {
			op = NumericNonOverflowingAdd.INSTANCE;
			type = resultType(left.getStaticType(), right.getStaticType());
			result = state.smallStepSemantics(new BinaryExpression(type, left, right, op, getLocation()), this);
		} else if (left.getStaticType().isStringType() && right.getStaticType().isStringType()) {
			op = StringConcat.INSTANCE;
			type = GoStringType.INSTANCE;
			result = state.smallStepSemantics(new BinaryExpression(type, left, right, op, getLocation()), this);
		} else {
			Set<Type> ltypes = state.getState().getRuntimeTypesOf(left, this, state.getState());
			Set<Type> rtypes = state.getState().getRuntimeTypesOf(right, this, state.getState());
			for (Type leftType : ltypes)
				for (Type rightType : rtypes) {
					if (leftType.isStringType() && rightType.isStringType()) {
						op = StringConcat.INSTANCE;
						type = GoStringType.INSTANCE;
					} else if (leftType.isNumericType() || rightType.isNumericType()) {
						op = NumericNonOverflowingAdd.INSTANCE;
						type = resultType(leftType, rightType);
					} else
						continue;
					result = result.lub(state.smallStepSemantics(
							new BinaryExpression(type, left, right, op, getLocation()), this));
				}
		}
		return result;
	}
}