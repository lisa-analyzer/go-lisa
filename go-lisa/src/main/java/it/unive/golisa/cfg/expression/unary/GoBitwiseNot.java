package it.unive.golisa.cfg.expression.unary;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.Type;

/**
 * A Go bitwise unary not expression (e.g., ^x).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoBitwiseNot extends UnaryExpression {

	/**
	 * Builds the bitwise not expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param exp      the expression
	 */
	public GoBitwiseNot(CFG cfg, SourceCodeLocation location, Expression exp) {
		super(cfg, location, "^", exp.getStaticType(), exp);
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(InterproceduralAnalysis<A> interprocedural,
			AnalysisState<A> state, SymbolicExpression expr, StatementStore<A> expressions) throws SemanticException {
		Type exprType = state.getState().getDynamicTypeOf(expr, this, state.getState());
		if (!exprType.isUntyped() || (exprType.isNumericType() && !exprType.asNumericType().isIntegral()))
			return state.bottom();

		// TODO: LiSA has not symbolic expression handling bitwise, return top
		// at the moment
		return state.smallStepSemantics(
				new PushAny(exprType, getLocation()), this);
	}
}
