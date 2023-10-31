package it.unive.golisa.cfg.expression.binary;

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
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingMod;
import it.unive.lisa.type.Type;

/**
 * A Go module expression (e.g., x % y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoModule extends it.unive.lisa.program.cfg.statement.BinaryExpression {

	/**
	 * Builds the module expression.
	 *
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left-hand side of this expression
	 * @param right    the right-hand side of this expression
	 */
	public GoModule(CFG cfg, SourceCodeLocation location, Expression left, Expression right) {
		super(cfg, location, "%", left, right);
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(
			InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
			SymbolicExpression left, SymbolicExpression right, StatementStore<A> expressions)
			throws SemanticException {
		Type leftType = state.getState().getDynamicTypeOf(left, this, state.getState());
		if (!(leftType.isNumericType() && leftType.asNumericType().isIntegral()) && !leftType.isUntyped())
			return state.bottom();

		Type rightType = state.getState().getDynamicTypeOf(right, this, state.getState());
		if (!(rightType.isNumericType() && rightType.asNumericType().isIntegral()) && !rightType.isUntyped())
			return state.bottom();

		return state
				.smallStepSemantics(new BinaryExpression(leftType, left, right,
						NumericNonOverflowingMod.INSTANCE, getLocation()), this);
	}
}
