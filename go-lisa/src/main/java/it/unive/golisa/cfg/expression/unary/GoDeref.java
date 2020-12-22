package it.unive.golisa.cfg.expression.unary;

import java.util.Collection;

import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.impl.types.TypeEnvironment;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;

public class GoDeref extends NativeCall {
	
	public GoDeref(CFG cfg, Expression exp) {
		super(cfg, null, -1, -1, "&", exp);
	}

	@Override
	public <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> callSemantics(
			AnalysisState<H, V> computedState, it.unive.lisa.callgraph.CallGraph callGraph,
			Collection<SymbolicExpression>[] params) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <H extends HeapDomain<H>> AnalysisState<H, TypeEnvironment> callTypeInference(
			AnalysisState<H, TypeEnvironment> computedState, it.unive.lisa.callgraph.CallGraph callGraph,
			Collection<SymbolicExpression>[] params) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

}
