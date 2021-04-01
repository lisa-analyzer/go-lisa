package it.unive.golisa.cfg.expression;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.BinaryNativeCall;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;

public class GoCollectionAccess extends BinaryNativeCall {

	public GoCollectionAccess(CFG cfg, Expression container, Expression child) {
		this(cfg, null, -1, -1, container, child);
	}

	public GoCollectionAccess(CFG cfg, String sourceFile, int line, int col, Expression container, Expression child) {
		super(cfg, new SourceCodeLocation(sourceFile, line, col),  container + "::" +  child, container, child);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V> leftState,
			SymbolicExpression left, AnalysisState<A, H, V> rightState, SymbolicExpression right)
					throws SemanticException {
		if (left.getDynamicType().isPointerType())
			return rightState.smallStepSemantics(new AccessChild(getRuntimeTypes(), left, right), this);
		else
			return entryState.bottom();
	}
}
