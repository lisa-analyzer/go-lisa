package it.unive.golisa.cfg.expression;

import java.util.Collection;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.impl.types.TypeEnvironment;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NativeCall;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.symbolic.SymbolicExpression;

public class GoTypeConversion extends NativeCall {

	private Type type;
	
	public GoTypeConversion(CFG cfg, Type type, Expression exp) {
		super(cfg, "(" + type + ")", exp);
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}

	@Override
	public <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> callSemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V>[] computedStates,
			Collection<SymbolicExpression>[] params) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <A extends AbstractState<A, H, TypeEnvironment>, H extends HeapDomain<H>> AnalysisState<A, H, TypeEnvironment> callTypeInference(
			AnalysisState<A, H, TypeEnvironment> entryState, CallGraph callGraph,
			AnalysisState<A, H, TypeEnvironment>[] computedStates, Collection<SymbolicExpression>[] params)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

}
