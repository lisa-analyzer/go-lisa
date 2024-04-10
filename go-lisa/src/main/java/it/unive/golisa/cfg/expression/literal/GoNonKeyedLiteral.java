package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.VariableScopingCFG;
import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration.NumericalTyper;
import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoStructType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.VariableTableEntry;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * A Go non-keyed literal (e.g., {1, 2, 3}).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoNonKeyedLiteral extends NaryExpression {

	private final boolean isStackAllocated;

	/**
	 * Builds the non-keyed literal.
	 * 
	 * @param cfg        the {@link CFG} where this expression lies
	 * @param location   the location where this expression is defined
	 * @param values     the expressions of the non-keyed literal
	 * @param staticType the static type of this non-keyed literal
	 */
	public GoNonKeyedLiteral(CFG cfg, CodeLocation location, Expression[] values, Type staticType) {
		this(cfg, location, values, staticType, true);

	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	/**
	 * Builds the non-keyed literal.
	 * 
	 * @param cfg              the {@link CFG} where this expression lies
	 * @param location         the location where this expression is defined
	 * @param values           the expressions of the non-keyed literal
	 * @param staticType       the static type of this non-keyed literal
	 * @param isStackAllocated whether this expression is allocated in the stack
	 */
	public GoNonKeyedLiteral(CFG cfg, CodeLocation location, Expression[] values, Type staticType,
			boolean isStackAllocated) {
		super(cfg, location, "nonKeyedLit(" + staticType + ")", staticType, values);
		this.isStackAllocated = isStackAllocated;
	}

	private SymbolicExpression getVariable(Global global) {
		VariableTableEntry varTableEntry = ((VariableScopingCFG) getCFG())
				.getVariableTableEntryIfExist(global.getName(), global.getLocation());

		Variable id;

		if (varTableEntry == null)
			id = new Variable(global.getStaticType(), global.getName(),
					global.getLocation());
		else
			id = new Variable(global.getStaticType(), global.getName(), varTableEntry.getAnnotations(),
					global.getLocation());

		return id;
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> forwardSemanticsAux(InterproceduralAnalysis<A> interprocedural,
			AnalysisState<A> state, ExpressionSet[] params, StatementStore<A> expressions) throws SemanticException {
		Type type = getStaticType();
		MemoryAllocation created = new MemoryAllocation(type, getLocation(), new Annotations(), isStackAllocated);

		// Allocates the new heap allocation
		AnalysisState<A> containerState = state.smallStepSemantics(created, this);
		ExpressionSet containerExps = containerState.getComputedExpressions();

		if (getStaticType() instanceof GoStructType) {
			// Retrieve the struct type (that is a compilation unit)
			CompilationUnit structUnit = ((GoStructType) type).getUnit();

			AnalysisState<A> result = state.bottom();

			for (SymbolicExpression containerExp : containerExps) {
				HeapReference reference = new HeapReference(new ReferenceType(type), containerExp, getLocation());
				HeapDereference dereference = new HeapDereference(type, reference, getLocation());

				if (getSubExpressions().length == 0) {
					result = result.lub(containerState);
					continue;
				}

				int i = 0;
				AnalysisState<A> tmp = containerState;

				for (Global field : structUnit.getInstanceGlobals(true)) {
					AccessChild access = new AccessChild(field.getStaticType(), dereference, getVariable(field),
							getLocation());
					AnalysisState<A> fieldState = tmp.smallStepSemantics(access, this);
					for (SymbolicExpression id : fieldState.getComputedExpressions())
						if (i < params.length)
							for (SymbolicExpression v : params[i]) {
								Type vtype = tmp.getState().getDynamicTypeOf(v, this, tmp.getState());
								tmp = fieldState.assign(id, NumericalTyper.type(v, vtype), this);
							}
						else
							tmp = fieldState.assign(id, new PushAny(Untyped.INSTANCE, getLocation()),
									this);

					i++;
				}

				result = result.lub(tmp.smallStepSemantics(reference, this));
			}

			return result;
		}

		if (getStaticType() instanceof GoArrayType) {
			AnalysisState<A> result = state.bottom();

			GoArrayType arrayType = (GoArrayType) getStaticType();
			Type contentType = arrayType.getContenType();
			int arrayLength = arrayType.getLength();

			for (SymbolicExpression containerExp : containerExps) {
				HeapReference reference = new HeapReference(new ReferenceType(arrayType), containerExp, getLocation());
				HeapDereference dereference = new HeapDereference(arrayType, reference, getLocation());
				// Assign the len property to this hid
				Variable lenProperty = new Variable(Untyped.INSTANCE, "len",
						getLocation());
				AccessChild lenAccess = new AccessChild(GoIntType.INSTANCE, dereference,
						lenProperty, getLocation());
				AnalysisState<A> lenState = containerState.smallStepSemantics(lenAccess, this);

				AnalysisState<A> lenResult = state.bottom();
				for (SymbolicExpression lenId : lenState.getComputedExpressions())
					lenResult = lenResult.lub(
							lenState.assign(lenId, new Constant(GoIntType.INSTANCE, arrayLength, getLocation()), this));

				// Assign the cap property to this hid
				Variable capProperty = new Variable(Untyped.INSTANCE, "cap",
						getLocation());
				AccessChild capAccess = new AccessChild(GoIntType.INSTANCE, dereference,
						capProperty, getLocation());
				AnalysisState<A> capState = lenResult.smallStepSemantics(capAccess, this);

				AnalysisState<A> capResult = state.bottom();
				for (SymbolicExpression lenId : capState.getComputedExpressions())
					capResult = capResult.lub(
							capState.assign(lenId, new Constant(GoIntType.INSTANCE, arrayLength, getLocation()), this));

				if (getSubExpressions().length == 0) {
					result = result.lub(capResult);
					continue;
				}

				// Allocate the heap location
				AnalysisState<A> tmp = capResult;
				for (int i = 0; i < arrayLength; i++) {
					AccessChild access = new AccessChild(contentType, dereference,
							new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
					AnalysisState<A> accessState = tmp.smallStepSemantics(access, this);

					for (SymbolicExpression index : accessState.getComputedExpressions())
						for (SymbolicExpression v : params[i]) {
							Type vtype = tmp.getState().getDynamicTypeOf(v, this, tmp.getState());
							tmp = tmp.assign(index, NumericalTyper.type(v, vtype), this);
						}
				}

				result = result.lub(tmp.smallStepSemantics(reference, this));
			}

			return result;
		} else if (getStaticType() instanceof GoSliceType) {
			AnalysisState<A> result = state.bottom();

			GoSliceType sliceType = (GoSliceType) getStaticType();
			Type contentType = sliceType.getContentType();
			int sliceLenght = getSubExpressions().length;

			for (SymbolicExpression containerExp : containerExps) {
				HeapReference reference = new HeapReference(new ReferenceType(type), containerExp, getLocation());
				HeapDereference dereference = new HeapDereference(type, reference, getLocation());

				// Assign the len property to this hid
				Variable lenProperty = new Variable(Untyped.INSTANCE, "len",
						getLocation());
				AccessChild lenAccess = new AccessChild(GoIntType.INSTANCE, dereference,
						lenProperty, getLocation());
				AnalysisState<A> lenState = containerState.smallStepSemantics(lenAccess, this);

				AnalysisState<A> lenResult = state.bottom();
				for (SymbolicExpression lenId : lenState.getComputedExpressions())
					lenResult = lenResult.lub(
							lenState.assign(lenId, new Constant(GoIntType.INSTANCE, sliceLenght, getLocation()), this));

				// Assign the cap property to this hid
				Variable capProperty = new Variable(Untyped.INSTANCE, "cap",
						getLocation());
				AccessChild capAccess = new AccessChild(GoIntType.INSTANCE, dereference,
						capProperty, getLocation());
				AnalysisState<A> capState = lenResult.smallStepSemantics(capAccess, this);

				AnalysisState<A> capResult = state.bottom();
				for (SymbolicExpression lenId : capState.getComputedExpressions())
					capResult = capResult.lub(
							capState.assign(lenId, new Constant(GoIntType.INSTANCE, sliceLenght, getLocation()), this));

				if (getSubExpressions().length == 0) {
					result = result.lub(capResult);
					continue;
				}

				// Allocate the heap location
				AnalysisState<A> tmp = capResult;
				for (int i = 0; i < sliceLenght; i++) {
					AccessChild access = new AccessChild(contentType, dereference,
							new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
					AnalysisState<A> accessState = tmp.smallStepSemantics(access, this);

					for (SymbolicExpression index : accessState.getComputedExpressions())
						for (SymbolicExpression v : params[i]) {
							Type vtype = tmp.getState().getDynamicTypeOf(v, this, tmp.getState());
							tmp = tmp.assign(index, NumericalTyper.type(v, vtype), this);
						}
				}

				result = result.lub(tmp.smallStepSemantics(reference, this));
			}

			return result;
		}

		// TODO: to handle the other cases (maps...)
		return state.top().smallStepSemantics(new PushAny(type, getLocation()), this);
	}
}
