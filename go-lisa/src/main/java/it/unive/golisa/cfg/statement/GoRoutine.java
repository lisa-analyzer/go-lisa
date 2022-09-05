package it.unive.golisa.cfg.statement;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.UnaryStatement;
import it.unive.lisa.program.cfg.statement.call.Call;

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
		super(cfg, location, call);
	}

	@Override
	public String toString() {
		return "go " + getExpression().toString();
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> semantics(
					AnalysisState<A, H, V, T> entryState, InterproceduralAnalysis<A, H, V, T> interprocedural,
					StatementStore<A, H, V, T> expressions) throws SemanticException {

		return entryState;//???
	}
}
