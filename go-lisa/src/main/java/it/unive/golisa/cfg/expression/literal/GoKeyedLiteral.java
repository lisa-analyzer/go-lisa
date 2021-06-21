package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration.NumericalTyper;
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
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapAllocation;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.Untyped;

public class GoKeyedLiteral extends NativeCall {

	private final Expression[] keys;

	public GoKeyedLiteral(CFG cfg, SourceCodeLocation location, Expression[] keys, Expression[] values, GoType staticType) {
		super(cfg, location, "keyedLiteral(" + staticType + ")", staticType, values);
		this.keys = keys;
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
		HeapAllocation created = new HeapAllocation(Caches.types().mkSingletonSet(getStaticType()), getLocation());

		// Allocates the new heap allocation 
		AnalysisState<A, H, V> containerState = lastPostState.smallStepSemantics(created, this);
		ExpressionSet<SymbolicExpression> containerExps = containerState.getComputedExpressions();

		if (getStaticType() instanceof GoArrayType) {

			GoArrayType arrayType = (GoArrayType) getStaticType();
			int arrayLength = arrayType.getLength();

			for (SymbolicExpression containerExp : containerExps) {
				HeapReference reference = new HeapReference(Caches.types().mkSingletonSet(getStaticType()), containerExp, getLocation());
				HeapDereference dereference = new HeapDereference(Caches.types().mkSingletonSet(getStaticType()), reference, getLocation());

				// Assign the len property to this hid
				Variable lenProperty = new Variable(Caches.types().mkSingletonSet(Untyped.INSTANCE), "len", getLocation());
				AccessChild lenAccess = new AccessChild(Caches.types().mkSingletonSet(GoIntType.INSTANCE), dereference, lenProperty, getLocation());
				AnalysisState<A, H, V> lenState = containerState.smallStepSemantics(lenAccess, this);

				AnalysisState<A, H, V> lenResult = entryState.bottom();
				for (SymbolicExpression lenId : lenState.getComputedExpressions())
					lenResult = lenResult.lub(lenState.assign(lenId, new Constant(GoIntType.INSTANCE, arrayLength, getLocation()), this));

				// Assign the cap property to this hid
				Variable capProperty = new Variable(Caches.types().mkSingletonSet(Untyped.INSTANCE), "cap", getLocation());
				AccessChild capAccess = new AccessChild(Caches.types().mkSingletonSet(GoIntType.INSTANCE), dereference, capProperty, getLocation());
				AnalysisState<A, H, V> capState = lenResult.smallStepSemantics(capAccess, this);

				AnalysisState<A, H, V> capResult = entryState.bottom();
				for (SymbolicExpression lenId : capState.getComputedExpressions())
					capResult = capResult.lub(capState.assign(lenId, new Constant(GoIntType.INSTANCE, arrayLength, getLocation()), this));

				if (getParameters().length == 0)
					return capResult.smallStepSemantics(reference, this);

			}
		}

		if (getStaticType() instanceof GoStructType) {
			// Retrieve the struct type (that is a compilation unit)
			CompilationUnit structUnit = ((GoStructType) getStaticType()).getUnit();

			AnalysisState<A, H, V> result = entryState.bottom();

			for (SymbolicExpression containerExp : containerExps) {
				HeapReference reference = new HeapReference(Caches.types().mkSingletonSet(getStaticType()), containerExp, getLocation());
				HeapDereference dereference = new HeapDereference(Caches.types().mkSingletonSet(getStaticType()), reference, getLocation());

				if (getParameters().length == 0) {
					result = result.lub(containerState);
					continue;
				}

				AnalysisState<A, H, V> tmp = containerState;

				for (int i = 0; i < keys.length; i++) {
					Variable field = getVariable((VariableRef) keys[i]);
					AccessChild access =  new AccessChild(field.getTypes(), dereference, field, getLocation());
					AnalysisState<A, H, V> fieldState = tmp.smallStepSemantics(access, this);
					for (SymbolicExpression id : fieldState.getComputedExpressions()) 
						for (SymbolicExpression v : params[i])
							tmp = fieldState.assign(id, NumericalTyper.type(v), this);
				}

				result = result.lub(tmp.smallStepSemantics(reference, this));
			}

			return result;
		} 

		// TODO: to handle the other cases (maps...)
		return entryState.top();
	}

	private Variable getVariable(Global varRef) {
		return new Variable(Caches.types().mkSingletonSet(varRef.getStaticType()), varRef.getName(), varRef.getLocation());
	}	

	private Variable getVariable(VariableRef varRef) {
		return new Variable(Caches.types().mkSingletonSet(varRef.getStaticType()), varRef.getName(), varRef.getLocation());
	}	
}
