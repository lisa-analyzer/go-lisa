package it.unive.golisa.cfg.expression.binary;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.BinaryNativeCall;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
 
public class GoChannelSend extends BinaryNativeCall {

	public GoChannelSend(CFG cfg, CodeLocation location, Expression left, Expression right) {
		super(cfg, location, "<-", left, right);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
			AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural,
			AnalysisState<A, H, V> leftState, SymbolicExpression leftExp, AnalysisState<A, H, V> rightState,
			SymbolicExpression rightExp) throws SemanticException {
		// TODO: go channel send semantics
		return entryState.top();
	}

}