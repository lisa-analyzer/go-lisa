package it.unive.golisa.cfg.custom;

import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.callgraph.CallGraph;
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
	protected <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> callSemantics(
			AnalysisState<H, V> computedState, CallGraph callGraph, SymbolicExpression[] params)
			throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}

}
