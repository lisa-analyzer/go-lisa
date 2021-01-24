package it.unive.golisa.cfg.expression;

import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.BinaryNativeCall;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.type.Untyped;

public class GoCollectionAccess extends BinaryNativeCall {

	public GoCollectionAccess(CFG cfg, Expression container, Expression index) {
		this(cfg, "", -1, -1, container, index);
	}

	public GoCollectionAccess(CFG cfg, String sourceFile, int line, int col, Expression container, Expression index) {
		super(cfg, sourceFile, line, col, "[]", container, index);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V> leftState,
			SymbolicExpression left, AnalysisState<A, H, V> rightState, SymbolicExpression right)
					throws SemanticException {

		// it is not possible to detect the correct type of the field without
		// resolving it. we rely on the rewriting that will happen inside heap
		// domain to translate this into a variable that will have its correct
		// type
		if (left.getDynamicType() instanceof GoSliceType 
				|| left.getDynamicType() instanceof GoArrayType
				|| left.getDynamicType() instanceof GoMapType
				|| left.getDynamicType() instanceof GoSliceType
				|| left.getDynamicType() instanceof Untyped)
			return rightState.smallStepSemantics(new AccessChild(getRuntimeTypes(), left, right));

		return entryState.bottom();
	}
}
