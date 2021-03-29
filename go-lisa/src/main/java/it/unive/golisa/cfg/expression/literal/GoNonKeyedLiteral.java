package it.unive.golisa.cfg.expression.literal;

import java.util.Collection;

import it.unive.golisa.cfg.type.GoType;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapAllocation;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.value.HeapIdentifier;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueIdentifier;

public class GoNonKeyedLiteral extends NativeCall {

	public GoNonKeyedLiteral(CFG cfg, Expression[] value, GoType staticType) {
		super(cfg, "nonKeyedLit("+ staticType + ")", staticType, value);
	}

	@Override
	public <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> callSemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V>[] computedStates,
			Collection<SymbolicExpression>[] params) throws SemanticException {
		// it corresponds to the analysis state after the evaluation of all the
		// parameters of this call
		// (the semantics of this call does not need information about the
		// intermediate analysis states)
		AnalysisState<A, H, V> lastPostState = computedStates[computedStates.length - 1];
		HeapAllocation created = new HeapAllocation(Caches.types().mkSingletonSet(getStaticType()));
		
		// Allocates the new heap allocation 
		AnalysisState<A, H, V> result = lastPostState.smallStepSemantics(created, this);
		HeapIdentifier hid = (HeapIdentifier) result.getComputedExpressions().iterator().next();
		
		if (getStaticType() instanceof GoStructType) {

			// Retrieve the struct type (that is a compilation unit)
			CompilationUnit structUnit = ((GoStructType) getStaticType()).getUnit();
			int i = 0;
			for (Global field : structUnit.getAllGlobals()) {
				AccessChild accessChild = new AccessChild(Caches.types().mkSingletonSet(field.getStaticType()), hid, getVariable(field));

				AnalysisState<A, H, V> tmp = result.smallStepSemantics(accessChild, this);
				AnalysisState<A, H, V> tmpField = null;
				
				for (SymbolicExpression id : tmp.getComputedExpressions()) 
					for (SymbolicExpression exp : params[i])
						if (tmpField == null)
							tmpField = tmp.assign((Identifier) id, exp, this);
						else
							tmpField = tmpField.lub(tmp.assign((Identifier) id, exp, this));
				
				result = tmpField;
				i++;
			}
		}

		return result.smallStepSemantics(new HeapReference(hid.getTypes(), hid.toString()), this);
	}

	private SymbolicExpression getVariable(Global global) {
		SymbolicExpression expr;
		if (global.getStaticType().isPointerType())
			expr = new HeapReference(Caches.types().mkSingletonSet(global.getStaticType()), global.getName());
		else
			expr = new ValueIdentifier(Caches.types().mkSingletonSet(global.getStaticType()), global.getName());
		return expr;
	}
}
