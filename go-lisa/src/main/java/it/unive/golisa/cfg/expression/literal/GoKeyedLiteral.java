package it.unive.golisa.cfg.expression.literal;

import java.util.Collection;
import java.util.Map;

import it.unive.golisa.cfg.type.GoType;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapAllocation;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.value.HeapIdentifier;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.PushAny;

public class GoKeyedLiteral extends NativeCall {

	private final Map<Expression, Expression> keyedValues;

	public GoKeyedLiteral(CFG cfg, Map<Expression, Expression> keyedValues, GoType staticType) {
		this(cfg, null, -1 , -1, keyedValues, staticType);
	}

	public GoKeyedLiteral(CFG cfg, String sourceFile, int line, int col, Map<Expression, Expression> keyedValues, GoType staticType) {
		super(cfg, new SourceCodeLocation(sourceFile, line, col), "keyedLiteral(" + staticType + ")", staticType, new Expression[]{});
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

		// Allocates the new heap allocation 
		AnalysisState<A, H, V> containerState = lastPostState.smallStepSemantics(created, this);
		Collection<SymbolicExpression> containerExps = containerState.getComputedExpressions();

		if (getStaticType() instanceof GoMapType) {
			AnalysisState<A, H, V> result = containerState;

			for (SymbolicExpression containerExp : containerExps) {
				if (!(containerExp instanceof HeapIdentifier))
					continue;
				HeapIdentifier hid = (HeapIdentifier) containerExp;

				// Initialize the hid identifier to top
				AnalysisState<A, H, V> hidState = containerState.assign((Identifier) hid, new PushAny(hid.getTypes()), getParentStatement());
				AnalysisState<A, H, V> tmp = null;
				
				for (Expression key : keyedValues.keySet()) {
					Expression value = keyedValues.get(key);
					// Evaluate the key
					AnalysisState<A, H, V> keyState = key.semantics(hidState, callGraph, null);
					// Evaluate the value associated with the keuy
					AnalysisState<A, H, V> mapEntryState = value.semantics(keyState, callGraph, null);

					for (SymbolicExpression k : keyState.getComputedExpressions()) {
						AnalysisState<A, H, V> accessState = mapEntryState.smallStepSemantics(
								new AccessChild(Caches.types().mkSingletonSet(k.getDynamicType()), hid, k), this);

						for (SymbolicExpression access : accessState.getComputedExpressions()) {
							for (SymbolicExpression v : mapEntryState.getComputedExpressions())
								if (tmp == null)
									tmp = mapEntryState.assign((Identifier) access, v, this);
								else
									tmp = tmp.lub(mapEntryState.assign((Identifier) access, v, this));
						}
					}
				}

				result = result.lub(tmp.smallStepSemantics(new HeapReference(hid.getTypes(), hid.getName()), this));
			}

			return result;
		} 

		// TODO: to handle the other cases (maps, array...)
		return entryState.top();
	}
}
