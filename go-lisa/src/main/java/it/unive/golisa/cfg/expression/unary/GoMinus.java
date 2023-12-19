package it.unive.golisa.cfg.expression.unary;

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
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.operator.unary.NumericNegation;
import it.unive.lisa.type.Type;

/**
 * The Go unary minus expression (e.g., -x).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoMinus extends it.unive.lisa.program.cfg.statement.UnaryExpression {

	/**
	 * Builds the unary minus expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param exp      the expression
	 */
	public GoMinus(CFG cfg, SourceCodeLocation location, Expression exp) {
		super(cfg, location, "-", exp);
	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(InterproceduralAnalysis<A> interprocedural,
			AnalysisState<A> state, SymbolicExpression expr, StatementStore<A> expressions) throws SemanticException {
		Type etype = state.getState().getDynamicTypeOf(expr, this, state.getState());
		if (!etype.isNumericType() && !etype.isUntyped())
			return state.bottom();

		return state.smallStepSemantics(
				new UnaryExpression(etype, expr,
						NumericNegation.INSTANCE, getLocation()),
				this);
	}
}
