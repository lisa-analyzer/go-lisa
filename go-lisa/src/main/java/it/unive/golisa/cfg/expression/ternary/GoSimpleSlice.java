package it.unive.golisa.cfg.expression.ternary;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.TernaryNativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.TernaryOperator;

public class GoSimpleSlice extends TernaryNativeCall {

	public GoSimpleSlice(CFG cfg, Expression left, Expression middle, Expression right) {
		super(cfg, "slice", left, middle, right);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V>
	ternarySemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, 
			AnalysisState<A, H, V> leftState, SymbolicExpression left,
			AnalysisState<A, H, V> middleState, SymbolicExpression middle,
			AnalysisState<A, H, V> rightState, SymbolicExpression right) throws SemanticException {
		
		if (!left.getDynamicType().isStringType() && ! left.getDynamicType().isUntyped())
			return entryState.bottom();

		if (!middle.getDynamicType().isNumericType() && ! middle.getDynamicType().isUntyped())
			return entryState.bottom();

		if (!right.getDynamicType().isNumericType() && ! right.getDynamicType().isUntyped())
			return entryState.bottom();
		
		return rightState.smallStepSemantics(new TernaryExpression(Caches.types().mkSingletonSet(GoStringType.INSTANCE), left, middle, right, TernaryOperator.STRING_SUBSTRING), this);
	}
}
