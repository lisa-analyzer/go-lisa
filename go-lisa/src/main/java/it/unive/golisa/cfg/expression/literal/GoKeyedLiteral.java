package it.unive.golisa.cfg.expression.literal;

import java.util.Collection;
import java.util.Map;

import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.analysis.impl.types.TypeEnvironment;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.HeapAllocation;

public class GoKeyedLiteral extends NativeCall {

	private final Map<String, Expression> keyedValues;
	
	public GoKeyedLiteral(CFG cfg, Map<String, Expression> keyedValues, GoType staticType) {
		super(cfg, "keyedLiteral(" + staticType + ")", staticType, new Expression[]{});
		this.keyedValues = keyedValues;
	}

	@Override
	public <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> callSemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V>[] computedStates,
			Collection<SymbolicExpression>[] params) throws SemanticException {
		// it corresponds to the analysis state after the evaluation of all the
		// parameters of this call
		// (the semantics of this call does not need information about the
		// intermediate analysis states)
		AnalysisState<A, H, V> lastPostState = computedStates.length == 0 ? entryState : computedStates[computedStates.length - 1];
		HeapAllocation created = new HeapAllocation(Caches.types().mkSingletonSet(getStaticType()));

		// TODO: at the moment, we are only allocating the object, without considering the paramters
		return lastPostState.smallStepSemantics(created);
	}

	@Override
	public <A extends AbstractState<A, H, TypeEnvironment>, H extends HeapDomain<H>> AnalysisState<A, H, TypeEnvironment> callTypeInference(
			AnalysisState<A, H, TypeEnvironment> entryState, CallGraph callGraph,
			AnalysisState<A, H, TypeEnvironment>[] computedStates, Collection<SymbolicExpression>[] params)
					throws SemanticException {
		// it corresponds to the analysis state after the evaluation of all the
		// parameters of this call
		// (the semantics of this call does not need information about the
		// intermediate analysis states)
		AnalysisState<A, H, TypeEnvironment> lastPostState = computedStates.length == 0 ? entryState : computedStates[computedStates.length - 1];
		HeapAllocation created = new HeapAllocation(Caches.types().mkSingletonSet(getStaticType()));

		// TODO: at the moment, we are only allocating the object, without considering the paramters
		return lastPostState.smallStepSemantics(created);
	}
}
