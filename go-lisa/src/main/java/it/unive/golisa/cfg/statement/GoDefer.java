package it.unive.golisa.cfg.statement;

import java.util.Collection;

import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.impl.types.TypeEnvironment;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;

public class GoDefer extends NativeCall {
	
	public GoDefer(CFG cfg, Expression expression) {
		this(cfg, null, -1, -1, expression);
	}
	
	public GoDefer(CFG cfg, String sourceFile, int line, int col, Expression expression) {
		super(cfg, sourceFile, line, col, "defer", expression);
	}

	@Override
	public <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> callSemantics(
			AnalysisState<H, V> computedState, CallGraph callGraph, Collection<SymbolicExpression>[] params)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <H extends HeapDomain<H>> AnalysisState<H, TypeEnvironment> callTypeInference(
			AnalysisState<H, TypeEnvironment> computedState, CallGraph callGraph,
			Collection<SymbolicExpression>[] params) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

}
