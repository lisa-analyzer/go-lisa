package it.unive.golisa;

import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration.NumericalTyper;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.golisa.program.cfg.VarArgsParameter;
import it.unive.lisa.analysis.AbstractDomain;
import it.unive.lisa.analysis.AbstractLattice;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.lattices.ExpressionSet;
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
import it.unive.lisa.symbolic.heap.NullConstant;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;

/**
 * The Go assigning strategy.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoAssigningStrategy implements ParameterAssigningStrategy {

	/**
	 * The singleton instance of this class.
	 */
	public static final GoAssigningStrategy INSTANCE = new GoAssigningStrategy();

	private GoAssigningStrategy() {
	}

	private <A extends AbstractLattice<A>, D extends AbstractDomain<A>> AnalysisState<A> smash(InterproceduralAnalysis<A, D> interprocedural, AnalysisState<A> state,
			int i,
			ExpressionSet[] actuals,
			GoSliceType type,
			Variable symbolicVariable,
			ProgramPoint pp) throws SemanticException {
		AnalysisState<A> result = state.bottom();
		Type contentType = type.getContentType();
		int sliceLenght = actuals.length - i;
		CodeLocation location = pp.getLocation();

		// allocate the slice
		MemoryAllocation created = new MemoryAllocation(type, location, new Annotations(), false);
		AnalysisState<A> createdSt = interprocedural.getAnalysis().smallStepSemantics(state, created, pp);
		ExpressionSet createdExps = createdSt.getExecutionExpressions();

		for (SymbolicExpression cr : createdExps) {
			HeapReference reference = new HeapReference(new ReferenceType(type), cr, location);
			HeapDereference dereference = new HeapDereference(type, reference, location);

			// Assign the len property to this hid
			Variable lenProperty = new Variable(Untyped.INSTANCE, "len", location);
			AccessChild lenAccess = new AccessChild(GoIntType.INSTANCE, dereference, lenProperty, location);
			AnalysisState<A> lenState = interprocedural.getAnalysis().smallStepSemantics(createdSt, lenAccess, pp);

			AnalysisState<A> lenResult = state.bottom();
			for (SymbolicExpression lenId : lenState.getExecutionExpressions())
				lenResult = lenResult
						.lub(interprocedural.getAnalysis().assign(lenState, lenId, new Constant(GoIntType.INSTANCE, sliceLenght, location), pp));

			// Assign the cap property to this hid
			Variable capProperty = new Variable(Untyped.INSTANCE, "cap",
					location);
			AccessChild capAccess = new AccessChild(GoIntType.INSTANCE, dereference,
					capProperty, location);
			AnalysisState<A> capState = interprocedural.getAnalysis().smallStepSemantics(lenResult, capAccess, pp);

			AnalysisState<A> capResult = state.bottom();
			for (SymbolicExpression lenId : capState.getExecutionExpressions())
				capResult = capResult.lub(
						interprocedural.getAnalysis().assign(capState, lenId, new Constant(GoIntType.INSTANCE, sliceLenght, location), pp));

			// Allocate the heap location
			AnalysisState<A> tmp = capResult;
			for (; i < actuals.length; i++) {
				AccessChild access = new AccessChild(contentType, dereference,
						new Constant(GoIntType.INSTANCE, i, location), location);
				AnalysisState<A> accessState = interprocedural.getAnalysis().smallStepSemantics(tmp, access, pp);

				for (SymbolicExpression index : accessState.getExecutionExpressions())
					for (SymbolicExpression v : actuals[i]) {
						Type vtype = interprocedural.getAnalysis().getDynamicTypeOf(tmp, v, pp);
						tmp = interprocedural.getAnalysis().assign(tmp, index, NumericalTyper.type(v, vtype), pp);
					}
			}

			result = result.lub(interprocedural.getAnalysis().smallStepSemantics(tmp, reference, pp));
		}

		return result;
	}

	@Override
	public <A extends AbstractLattice<A>, D extends AbstractDomain<A>> Pair<AnalysisState<A>, ExpressionSet[]> prepare(
			Call call, AnalysisState<A> callState, InterproceduralAnalysis<A, D> interprocedural,
			StatementStore<A> expressions, Parameter[] formals, ExpressionSet[] actuals) throws SemanticException {
		boolean hasVarargs = formals.length > 0 && formals[formals.length - 1] instanceof VarArgsParameter;
		int i = 0;

		// if it is an instance call, we need check the first parameter
		// that corresponds to the callee of the instance call
		if (call.getCallType() == CallType.INSTANCE) {
			Parameter fCallee = formals[0];
			ExpressionSet aCallee = actuals[0];
			AnalysisState<A> prepared = callState.bottom();
			for (SymbolicExpression exp : aCallee) {
				HeapReference ref = new HeapReference(new ReferenceType(fCallee.getStaticType()), exp,
						call.getLocation());
				AnalysisState<A> refState = interprocedural.getAnalysis().smallStepSemantics(callState, ref, call);
				for (SymbolicExpression e : refState.getExecutionExpressions()) {
					Variable fId = new Variable(new ReferenceType(fCallee.getStaticType()), fCallee.getName(),
							fCallee.getAnnotations(), fCallee.getLocation());
					prepared = prepared.lub(interprocedural.getAnalysis().assign(callState, fId, e, call));
				}
			}

			i++;
			callState = prepared;
		}

		// prepare the state for the call: assign the value to each
		// parameter
		AnalysisState<A> prepared = callState;
		for (; i < formals.length; i++)
			if (i == formals.length - 1 && hasVarargs)
				if (i == actuals.length)
					// no values passed for the varargs parameter
					return Pair.of(interprocedural.getAnalysis().assign(prepared,
							formals[i].toSymbolicVariable(),
							new NullConstant(call.getLocation()),
							call),
							ArrayUtils.add(actuals, new ExpressionSet(formals[i].toSymbolicVariable())));
				else
					return Pair.of(
							smash(interprocedural, prepared, i, actuals, (GoSliceType) formals[i].getStaticType(),
									formals[i].toSymbolicVariable(), call),
							ArrayUtils.add(actuals, new ExpressionSet(formals[i].toSymbolicVariable())));
			else {
				AnalysisState<A> temp = prepared.bottom();
				for (SymbolicExpression exp : actuals[i])
					if (formals[i].getStaticType().isInMemoryType()) {
						Variable fId = new Variable(new ReferenceType(formals[i].getStaticType()), formals[i].getName(),
								formals[i].getAnnotations(), formals[i].getLocation());
						temp = temp.lub(interprocedural.getAnalysis().assign(prepared, fId, exp, call));
					} else
						temp = temp.lub(interprocedural.getAnalysis().assign(prepared, formals[i].toSymbolicVariable(), exp, call));
				prepared = temp;
			}

		return Pair.of(prepared, actuals);
	}

}
