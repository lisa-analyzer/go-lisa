package it.unive.golisa.cfg.statement;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.UnaryStatement;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.symbolic.SymbolicExpression;

/**
 * A Go routine.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoRoutine extends UnaryStatement {

	/**
	 * Builds a Go routine.
	 * 
	 * @param cfg      the {@link CFG} where this statement lies
	 * @param location the location where this statement is defined
	 * @param call     the call
	 */
	public GoRoutine(CFG cfg, CodeLocation location, Call call) {
		super(cfg, location, "go", call);
	}

	@Override
	public String toString() {
		return "go " + getSubExpression().toString();
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(
			InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
			SymbolicExpression expr, StatementStore<A> expressions) throws SemanticException {
		// TODO semantics of go routine not supported yet
		return state;
	}
}
