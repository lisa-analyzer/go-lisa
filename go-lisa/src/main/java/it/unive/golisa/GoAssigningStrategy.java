package it.unive.golisa;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.program.language.parameterassignment.ParameterAssigningStrategy;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.ReferenceType;

public class GoAssigningStrategy implements ParameterAssigningStrategy  {

	/**
	 * The singleton instance of this class.
	 */
	public static final GoAssigningStrategy INSTANCE = new GoAssigningStrategy();

	private GoAssigningStrategy() {
	}

	@Override
	public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> Pair<AnalysisState<A, H, V, T>, ExpressionSet<SymbolicExpression>[]> prepare(
			Call call, AnalysisState<A, H, V, T> callState, InterproceduralAnalysis<A, H, V, T> interprocedural,
			StatementStore<A, H, V, T> expressions, Parameter[] formals, ExpressionSet<SymbolicExpression>[] actuals)
					throws SemanticException {
		// if it is an instance call, we need check the first parameter
		// that corresponds to the callee of the instance call
		if (call.getCallType() == CallType.INSTANCE) {
			Parameter fCallee = formals[0];
			ExpressionSet<SymbolicExpression> aCallee = actuals[0];
			AnalysisState<A, H, V, T> prepared = callState.bottom();
			for (SymbolicExpression exp : aCallee) {
				HeapReference ref = new HeapReference(new ReferenceType(fCallee.getStaticType()), exp, call.getLocation());
				AnalysisState<A, H, V, T> refState = callState.smallStepSemantics(ref, call);
				for (SymbolicExpression e : refState.getComputedExpressions()) {
					Variable fId = new Variable(new ReferenceType(fCallee.getStaticType()), fCallee.getName(), fCallee.getAnnotations(), fCallee.getLocation());
					prepared = prepared.lub(callState.assign(fId, e, call));
				}
			}

			for (int i = 1; i < formals.length; i++) {
				AnalysisState<A, H, V, T> temp = prepared.bottom();
				for (SymbolicExpression exp : actuals[i])
					if (formals[i].getStaticType().isInMemoryType()) {
						Variable fId = new Variable(new ReferenceType(formals[i].getStaticType()), formals[i].getName(), formals[i].getAnnotations(), formals[i].getLocation());
						temp = temp.lub(prepared.assign(fId, exp, call));
					} else
						temp = temp.lub(prepared.assign(formals[i].toSymbolicVariable(), exp, call));				prepared = temp;
			}

			return Pair.of(prepared, actuals);
		} else {
			// prepare the state for the call: assign the value to each parameter
			AnalysisState<A, H, V, T> prepared = callState;
			for (int i = 0; i < formals.length; i++) {
				AnalysisState<A, H, V, T> temp = prepared.bottom();
				for (SymbolicExpression exp : actuals[i])
					if (formals[i].getStaticType().isInMemoryType()) {
						Variable fId = new Variable(new ReferenceType(formals[i].getStaticType()), formals[i].getName(), formals[i].getAnnotations(), formals[i].getLocation());
						temp = temp.lub(prepared.assign(fId, exp, call));
					} else
						temp = temp.lub(prepared.assign(formals[i].toSymbolicVariable(), exp, call));
				prepared = temp;
			}

			return Pair.of(prepared, actuals);
		}
	}

}
