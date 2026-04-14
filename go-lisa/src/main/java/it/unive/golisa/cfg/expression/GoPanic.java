package it.unive.golisa.cfg.expression;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;

/**
 * A Go panic expression (e.g., panic("Error!")).
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class GoPanic extends NaryExpression {


	/**
	 * Builds the make expression.
	 * 
	 * @param cfg        the {@link CFG} where this expression lies
	 * @param location   the location where this expression is defined
	 * @param type       the type to allocate
	 * @param parameters the parameters
	 */
	public GoPanic(CFG cfg, CodeLocation location, Expression[] parameters) {
		super(cfg, location, "panic", parameters);
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> forwardSemanticsAux(
			InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
			ExpressionSet[] params, StatementStore<A> expressions)
			throws SemanticException {
		
		AnalysisState<A> result = state;
		for(ExpressionSet set : params) {
			for(SymbolicExpression exp : set)
				result =result.lub(result.smallStepSemantics(exp, this));
		}
		
		return result;
	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}
}
