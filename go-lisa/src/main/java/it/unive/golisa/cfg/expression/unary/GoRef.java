package it.unive.golisa.cfg.expression.unary;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.UnaryNativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;

public class GoRef extends UnaryNativeCall {
	
	public GoRef(CFG cfg, Expression exp) {
		this(cfg, null, -1, -1, exp);
	}

	public GoRef(CFG cfg, String sourceFile, int line, int col, Expression exp) {
		super(cfg, new SourceCodeLocation(sourceFile, line, col), "*", exp);
	}

	
	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V> exprState,
			SymbolicExpression expr) throws SemanticException {
		// TODO Auto-generated method stub
		return null;
	}
}
