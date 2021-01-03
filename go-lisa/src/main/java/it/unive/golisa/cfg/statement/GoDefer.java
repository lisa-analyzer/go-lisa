package it.unive.golisa.cfg.statement;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.UnaryNativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;

public class GoDefer extends UnaryNativeCall {
	
	public GoDefer(CFG cfg, Expression expression) {
		this(cfg, null, -1, -1, expression);
	}
	
	public GoDefer(CFG cfg, String sourceFile, int line, int col, Expression expression) {
		super(cfg, sourceFile, line, col, "defer", expression);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V> exprState,
			SymbolicExpression expr) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}
}
