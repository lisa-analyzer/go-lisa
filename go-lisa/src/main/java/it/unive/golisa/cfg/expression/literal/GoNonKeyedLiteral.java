package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.type.GoType;
import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapAllocation;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.HeapLocation;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

public class GoNonKeyedLiteral extends NativeCall {

	public GoNonKeyedLiteral(CFG cfg, SourceCodeLocation location, Expression[] value, GoType staticType) {
		super(cfg, location, "nonKeyedLit("+ staticType + ")", staticType, value);
	}

	@Override
	public <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> callSemantics(
			AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V>[] computedStates,
			ExpressionSet<SymbolicExpression>[] params) throws SemanticException {
		// it corresponds to the analysis state after the evaluation of all the
		// parameters of this call
		// (the semantics of this call does not need information about the
		// intermediate analysis states)
		AnalysisState<A, H, V> lastPostState = computedStates.length == 0 ? entryState : computedStates[computedStates.length - 1];
		HeapAllocation created = new HeapAllocation(Caches.types().mkSingletonSet(getStaticType()));

		// Allocates the new heap allocation 
		AnalysisState<A, H, V> containerState = lastPostState.smallStepSemantics(created, this);
		ExpressionSet<SymbolicExpression> containerExps = containerState.getComputedExpressions();

		if (getStaticType() instanceof GoStructType) {
			// Retrieve the struct type (that is a compilation unit)
			CompilationUnit structUnit = ((GoStructType) getStaticType()).getUnit();

			AnalysisState<A, H, V> result = entryState.bottom();

			for (SymbolicExpression containerExp : containerExps) {
				if (!(containerExp instanceof HeapLocation))
					continue;
				HeapLocation hid = (HeapLocation) containerExp;
				// Initialize the hid identifier to top
				AnalysisState<A, H, V> hidState = containerState.smallStepSemantics(hid, this);

				if (getParameters().length == 0) {
					result = result.lub(hidState);
					continue;
				}

				int i = 0;
				AnalysisState<A, H, V> tmp = hidState;
				for (Global field : structUnit.getInstanceGlobals(true)) {
					AccessChild access = new AccessChild(Caches.types().mkSingletonSet(field.getStaticType()), hid, getVariable(field));
					AnalysisState<A, H, V> fieldState = tmp.smallStepSemantics(
							access, this);
					for (SymbolicExpression id : fieldState.getComputedExpressions()) 
						for (SymbolicExpression exp : params[i])
							tmp = fieldState.assign((Identifier) id, exp, this);
					i++;
				}

				result = result.lub(tmp);
			}

			return result.smallStepSemantics(created, this);
		} 

		if (getStaticType() instanceof GoArrayType) {
			AnalysisState<A, H, V> result = entryState.bottom();

			GoArrayType arrayType = (GoArrayType) getStaticType();
			Type contentType = arrayType.getContentType();
			int arrayLength = arrayType.getLength();

			for (SymbolicExpression containerExp : containerExps) {
				if (!(containerExp instanceof HeapLocation))
					continue;

				HeapLocation hid = (HeapLocation) containerExp;

				// Assign the len property to this hid
				Variable lenProperty = new Variable(Caches.types().mkSingletonSet(Untyped.INSTANCE), "len");
				AccessChild lenAccess = new AccessChild(Caches.types().mkSingletonSet(GoIntType.INSTANCE), hid, lenProperty);
				AnalysisState<A, H, V> lenState = containerState.smallStepSemantics(lenAccess, this);

				AnalysisState<A, H, V> lenResult = entryState.bottom();
				for (SymbolicExpression lenId : lenState.getComputedExpressions())
					lenResult = lenResult.lub(lenState.assign((Identifier) lenId, new Constant(GoIntType.INSTANCE, arrayLength), this));

				// Assign the cap property to this hid
				Variable capProperty = new Variable(Caches.types().mkSingletonSet(Untyped.INSTANCE), "cap");
				AccessChild capAccess = new AccessChild(Caches.types().mkSingletonSet(GoIntType.INSTANCE), hid, capProperty);
				AnalysisState<A, H, V> capState = lenResult.smallStepSemantics(capAccess, this);

				AnalysisState<A, H, V> capResult = entryState.bottom();
				for (SymbolicExpression lenId : capState.getComputedExpressions())
					capResult = capResult.lub(capState.assign((Identifier) lenId, new Constant(GoIntType.INSTANCE, arrayLength), this));

				if (getParameters().length == 0) {
					result = result.lub(capResult);
					continue;
				}

				// Allocate the heap location
				AnalysisState<A, H, V> tmp = capResult;
				for (int i = 0; i < arrayLength; i++) {
					AccessChild access = new AccessChild(Caches.types().mkSingletonSet(contentType), hid, new Constant(GoIntType.INSTANCE, i));
					AnalysisState<A, H, V> accessState = tmp.smallStepSemantics(access, this);

					for (SymbolicExpression index : accessState.getComputedExpressions())
						for (SymbolicExpression v : params[i])
							tmp = tmp.assign((Identifier) index, v, this);

				}

				result = result.lub(tmp.smallStepSemantics(hid, this));
			}

			return result.smallStepSemantics(created, this);
		}

		// TODO: to handle the other cases (maps, array...)
		return entryState.top();
	}

	private SymbolicExpression getVariable(Global global) {
		return new Variable(Caches.types().mkSingletonSet(global.getStaticType()), global.getName());
	}	
}
