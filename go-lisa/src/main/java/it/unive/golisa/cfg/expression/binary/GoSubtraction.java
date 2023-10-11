package it.unive.golisa.cfg.expression.binary;

import java.util.Set;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingSub;
import it.unive.lisa.type.Type;

/**
 * A Go numerical subtraction expression (e.g., x - y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoSubtraction extends it.unive.lisa.program.cfg.statement.BinaryExpression
		implements GoBinaryNumericalOperation {

	/**
	 * Builds the subtraction expression.
	 *
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left-hand side of this expression
	 * @param right    the right-hand side of this expression
	 */
	public GoSubtraction(CFG cfg, SourceCodeLocation location, Expression left, Expression right) {
		super(cfg, location, "-", left, right);
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(InterproceduralAnalysis<A> arg0,
			AnalysisState<A> state, SymbolicExpression left, SymbolicExpression right, StatementStore<A> arg4)
			throws SemanticException {
		Set<Type> ltypes = state.getState().getRuntimeTypesOf(left, this, state.getState());
		Set<Type> rtypes = state.getState().getRuntimeTypesOf(right, this, state.getState());
		
		AnalysisState<A> result = state.bottom();
		for (Type leftType : ltypes)
			for (Type rightType : rtypes) {
				if (leftType.isNumericType() || rightType.isNumericType())
					result = result.lub(state.smallStepSemantics(
							new BinaryExpression(resultType(leftType, rightType), left, right,
									NumericNonOverflowingSub.INSTANCE, getLocation()),
							this));
			}

		return result;
	}
}
