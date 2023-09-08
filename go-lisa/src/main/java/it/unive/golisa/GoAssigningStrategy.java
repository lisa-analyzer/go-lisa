package it.unive.golisa;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.cfg.VarArgsParameter;
import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration.NumericalTyper;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.program.language.parameterassignment.ParameterAssigningStrategy;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.NullConstant;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

public class GoAssigningStrategy implements ParameterAssigningStrategy {

	/**
	 * The singleton instance of this class.
	 */
	public static final GoAssigningStrategy INSTANCE = new GoAssigningStrategy();

	private GoAssigningStrategy() {
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> Pair<AnalysisState<A, H, V, T>, ExpressionSet<SymbolicExpression>[]> prepare(
					Call call,
					AnalysisState<A, H, V, T> callState,
					InterproceduralAnalysis<A, H, V, T> interprocedural,
					StatementStore<A, H, V, T> expressions,
					Parameter[] formals,
					ExpressionSet<SymbolicExpression>[] actuals)
					throws SemanticException {
		boolean hasVarargs = formals.length > 0 && formals[formals.length - 1] instanceof VarArgsParameter;
		int i = 0;

		// if it is an instance call, we need check the first parameter
		// that corresponds to the callee of the instance call
		if (call.getCallType() == CallType.INSTANCE) {
			Parameter fCallee = formals[0];
			ExpressionSet<SymbolicExpression> aCallee = actuals[0];
			AnalysisState<A, H, V, T> prepared = callState.bottom();
			for (SymbolicExpression exp : aCallee) {
				HeapReference ref = new HeapReference(new ReferenceType(fCallee.getStaticType()), exp,
						call.getLocation());
				AnalysisState<A, H, V, T> refState = callState.smallStepSemantics(ref, call);
				for (SymbolicExpression e : refState.getComputedExpressions()) {
					Variable fId = new Variable(new ReferenceType(fCallee.getStaticType()), fCallee.getName(),
							fCallee.getAnnotations(), fCallee.getLocation());
					prepared = prepared.lub(callState.assign(fId, e, call));
				}
			}

			i++;
		}

		// prepare the state for the call: assign the value to each
		// parameter
		AnalysisState<A, H, V, T> prepared = callState;
		for (; i < formals.length; i++)
			if (i == formals.length - 1 && hasVarargs)
				if (i == actuals.length)
					// no values passed for the varargs parameter
					return Pair.of(prepared.assign(
							formals[i].toSymbolicVariable(),
							new NullConstant(call.getLocation()),
							call),
							ArrayUtils.add(actuals, new ExpressionSet<>(formals[i].toSymbolicVariable())));
				else
					return Pair.of(
							smash(prepared, i, actuals, (GoSliceType) formals[i].getStaticType(),
									formals[i].toSymbolicVariable(), call),
							ArrayUtils.add(actuals, new ExpressionSet<>(formals[i].toSymbolicVariable())));
			else {
				AnalysisState<A, H, V, T> temp = prepared.bottom();
				for (SymbolicExpression exp : actuals[i])
					if (formals[i].getStaticType().isInMemoryType()) {
						Variable fId = new Variable(new ReferenceType(formals[i].getStaticType()), formals[i].getName(),
								formals[i].getAnnotations(), formals[i].getLocation());
						temp = temp.lub(prepared.assign(fId, exp, call));
					} else
						temp = temp.lub(prepared.assign(formals[i].toSymbolicVariable(), exp, call));
				prepared = temp;
			}

		return Pair.of(prepared, actuals);
	}

	private <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> smash(AnalysisState<A, H, V, T> state,
					int i,
					ExpressionSet<SymbolicExpression>[] actuals,
					GoSliceType type,
					Variable symbolicVariable,
					ProgramPoint pp) throws SemanticException {
		AnalysisState<A, H, V, T> result = state.bottom();
		Type contentType = type.getContentType();
		int sliceLenght = actuals.length - i;
		CodeLocation location = pp.getLocation();

		// allocate the slice
		MemoryAllocation created = new MemoryAllocation(type, location, new Annotations(),false);
		AnalysisState<A, H, V, T> createdSt = state.smallStepSemantics(created, pp);
		ExpressionSet<SymbolicExpression> createdExps = createdSt.getComputedExpressions();

		for (SymbolicExpression cr : createdExps) {
			HeapReference reference = new HeapReference(new ReferenceType(type), cr, location);
			HeapDereference dereference = new HeapDereference(type, reference, location);

			// Assign the len property to this hid
			Variable lenProperty = new Variable(Untyped.INSTANCE, "len", location);
			AccessChild lenAccess = new AccessChild(GoIntType.INSTANCE, dereference, lenProperty, location);
			AnalysisState<A, H, V, T> lenState = createdSt.smallStepSemantics(lenAccess, pp);

			AnalysisState<A, H, V, T> lenResult = state.bottom();
			for (SymbolicExpression lenId : lenState.getComputedExpressions())
				lenResult = lenResult
						.lub(lenState.assign(lenId, new Constant(GoIntType.INSTANCE, sliceLenght, location), pp));

			// Assign the cap property to this hid
			Variable capProperty = new Variable(Untyped.INSTANCE, "cap",
					location);
			AccessChild capAccess = new AccessChild(GoIntType.INSTANCE, dereference,
					capProperty, location);
			AnalysisState<A, H, V, T> capState = lenResult.smallStepSemantics(capAccess, pp);

			AnalysisState<A, H, V, T> capResult = state.bottom();
			for (SymbolicExpression lenId : capState.getComputedExpressions())
				capResult = capResult.lub(
						capState.assign(lenId, new Constant(GoIntType.INSTANCE, sliceLenght, location), pp));

			// Allocate the heap location
			AnalysisState<A, H, V, T> tmp = capResult;
			for (; i < sliceLenght; i++) {
				AccessChild access = new AccessChild(contentType, dereference,
						new Constant(GoIntType.INSTANCE, i, location), location);
				AnalysisState<A, H, V, T> accessState = tmp.smallStepSemantics(access, pp);

				for (SymbolicExpression index : accessState.getComputedExpressions())
					for (SymbolicExpression v : actuals[i])
						tmp = tmp.assign(index, NumericalTyper.type(v), pp);
			}

			result = result.lub(tmp.smallStepSemantics(reference, pp));
		}

		return result;
	}

}
