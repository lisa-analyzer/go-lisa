package it.unive.golisa.cfg.expression.binary;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;

/**
 * A Go channel send expression (e.g., x <- "str").
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoChannelSend extends BinaryExpression {

	/**
	 * Builds the channel send expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the expression from to send
	 * @param right    the expression to send to {@code left}
	 */
	public GoChannelSend(CFG cfg, CodeLocation location, Expression left, Expression right) {
		super(cfg, location, "<-", left, right);
	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(InterproceduralAnalysis<A> arg0,
			AnalysisState<A> state, SymbolicExpression left, SymbolicExpression right, StatementStore<A> arg4)
			throws SemanticException {
		// TODO: go channel send semantics
		return state.top();
	}
}
