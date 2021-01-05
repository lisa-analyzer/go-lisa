package it.unive.golisa.cfg.expression.ternary;

import java.util.Collection;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.impl.types.TypeEnvironment;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.TernaryOperator;

public class GoSimpleSlice extends NativeCall {

	public GoSimpleSlice(CFG cfg, Expression left, Expression middle, Expression right) {
		super(cfg, "slice", left, middle, right);
	}

	@Override
	public <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> callSemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V>[] computedStates,
			Collection<SymbolicExpression>[] params) throws SemanticException {
		AnalysisState<A, H, V> result = null;
		for (SymbolicExpression left : params[0])
			for (SymbolicExpression middle : params[1])
				for (SymbolicExpression right : params[2]) {
					AnalysisState<A, H, V> tmp = ternarySemantics(entryState, callGraph, computedStates[0], 
							left, computedStates[1], middle, computedStates[2], right);
					if (result == null)
						result = tmp;
					else
						result = result.lub(tmp);
				}

		return result;
	}

	@Override
	public <A extends AbstractState<A, H, TypeEnvironment>, H extends HeapDomain<H>> AnalysisState<A, H, TypeEnvironment> callTypeInference(
			AnalysisState<A, H, TypeEnvironment> entryState, CallGraph callGraph,
			AnalysisState<A, H, TypeEnvironment>[] computedStates, Collection<SymbolicExpression>[] params)
					throws SemanticException {
		AnalysisState<A, H, TypeEnvironment> result = null;
		for (SymbolicExpression left : params[0])
			for (SymbolicExpression middle : params[1])
				for (SymbolicExpression right : params[2]) {
					AnalysisState<A, H, TypeEnvironment> tmp = ternarySemantics(entryState, callGraph, computedStates[0], 
							left, computedStates[1], middle, computedStates[2], right);
					if (result == null)
						result = tmp;
					else
						result = result.lub(tmp);
				}

		setRuntimeTypes(result.getState().getValueState().getLastComputedTypes().getRuntimeTypes());
		return result;
	}

	private <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V>
	ternarySemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, 
			AnalysisState<A, H, V> leftState, SymbolicExpression left,
			AnalysisState<A, H, V> middleState, SymbolicExpression middle,
			AnalysisState<A, H, V> rightState, SymbolicExpression right) throws SemanticException {
		
		if (!left.getDynamicType().isStringType() && !middle.getDynamicType().isNumericType() && !right.getDynamicType().isNumericType())
			return entryState.bottom();
		
		return rightState.smallStepSemantics(new TernaryExpression(Caches.types().mkSingletonSet(GoStringType.INSTANCE), left, middle, right, TernaryOperator.STRING_SUBSTRING));
	}
}
