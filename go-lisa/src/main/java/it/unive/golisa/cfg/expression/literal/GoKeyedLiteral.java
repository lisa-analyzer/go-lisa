package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.VariableScopingCFG;
import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration.NumericalTyper;
import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoStructType;
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
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.VariableTableEntry;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapAllocation;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * A Go keyed literal (e.g., {x: 1, y: 2, z: 3}).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoKeyedLiteral extends NaryExpression {

	private final Expression[] keys;

	/**
	 * Builds the keyed literal.
	 * 
	 * @param cfg        the {@link CFG} where this expression lies
	 * @param location   the location where this expression is defined
	 * @param keys       the keys of the keyed literal
	 * @param values     the expressions of the keyed literal
	 * @param staticType the static type of this keyed literal
	 */
	public GoKeyedLiteral(CFG cfg, SourceCodeLocation location, Expression[] keys, Expression[] values,
			Type staticType) {
		super(cfg, location, "keyedLiteral(" + staticType + ")", staticType, values);
		this.keys = keys;
	}

	private Variable getVariable(Global varRef) {
		VariableTableEntry varTableEntry = ((VariableScopingCFG) getCFG())
				.getVariableTableEntryIfExist(varRef.getName(), varRef.getLocation());

		Variable id;

		if (varTableEntry == null)
			id = new Variable(varRef.getStaticType(), varRef.getName(),
					varRef.getLocation());
		else
			id = new Variable(varRef.getStaticType(), varRef.getName(), varTableEntry.getAnnotations(),
					varRef.getLocation());

		return id;
	}

	private Variable getVariable(VariableRef varRef) {
		VariableTableEntry varTableEntry = ((VariableScopingCFG) getCFG())
				.getVariableTableEntryIfExist(varRef.getName(), varRef.getLocation());

		Variable id;

		if (varTableEntry == null)
			id = new Variable(varRef.getStaticType(), varRef.getName(),
					varRef.getLocation());
		else
			id = new Variable(varRef.getStaticType(), varRef.getName(), varTableEntry.getAnnotations(),
					varRef.getLocation());

		return id;
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> expressionSemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
					ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V, T> expressions)
					throws SemanticException {
		Type type = getStaticType();
		HeapAllocation created = new HeapAllocation(type, getLocation());

		// Allocates the new heap allocation
		AnalysisState<A, H, V, T> containerState = state.smallStepSemantics(created, this);
		ExpressionSet<SymbolicExpression> containerExps = containerState.getComputedExpressions();

		/*
		 * Array allocation
		 */
		if (getStaticType() instanceof GoArrayType) {

			GoArrayType arrayType = (GoArrayType) getStaticType();
			int arrayLength = arrayType.getLength();

			for (SymbolicExpression containerExp : containerExps) {
				HeapReference reference = new HeapReference(new ReferenceType(type), containerExp, getLocation());
				HeapDereference dereference = new HeapDereference(type, reference, getLocation());

				// Assign the len property to this hid
				Variable lenProperty = new Variable(Untyped.INSTANCE, "len",
						getLocation());
				AccessChild lenAccess = new AccessChild(GoIntType.INSTANCE, dereference,
						lenProperty, getLocation());
				AnalysisState<A, H, V, T> lenState = containerState.smallStepSemantics(lenAccess, this);

				AnalysisState<A, H, V, T> lenResult = state.bottom();
				for (SymbolicExpression lenId : lenState.getComputedExpressions())
					lenResult = lenResult.lub(
							lenState.assign(lenId, new Constant(GoIntType.INSTANCE, arrayLength, getLocation()), this));

				// Assign the cap property to this hid
				Variable capProperty = new Variable(Untyped.INSTANCE, "cap",
						getLocation());
				AccessChild capAccess = new AccessChild(GoIntType.INSTANCE, dereference,
						capProperty, getLocation());
				AnalysisState<A, H, V, T> capState = lenResult.smallStepSemantics(capAccess, this);

				AnalysisState<A, H, V, T> capResult = state.bottom();
				for (SymbolicExpression lenId : capState.getComputedExpressions())
					capResult = capResult.lub(
							capState.assign(lenId, new Constant(GoIntType.INSTANCE, arrayLength, getLocation()), this));

				if (getSubExpressions().length == 0)
					return capResult.smallStepSemantics(reference, this);

			}
		}

		/*
		 * GoSlice allocation
		 */

		if (getStaticType() instanceof GoSliceType) {

			GoSliceType sliceType = (GoSliceType) getStaticType();
			int arrayLength = 0;

			for (SymbolicExpression containerExp : containerExps) {
				HeapReference reference = new HeapReference(new ReferenceType(type), containerExp, getLocation());
				HeapDereference dereference = new HeapDereference(type, reference, getLocation());

				// Assign the len property to this hid
				Variable lenProperty = new Variable(Untyped.INSTANCE, "len",
						getLocation());
				AccessChild lenAccess = new AccessChild(GoIntType.INSTANCE, dereference,
						lenProperty, getLocation());
				AnalysisState<A, H, V, T> lenState = containerState.smallStepSemantics(lenAccess, this);

				AnalysisState<A, H, V, T> lenResult = state.bottom();
				for (SymbolicExpression lenId : lenState.getComputedExpressions())
					lenResult = lenResult.lub(
							lenState.assign(lenId, new Constant(GoIntType.INSTANCE, arrayLength, getLocation()), this));

				// Assign the cap property to this hid
				Variable capProperty = new Variable(Untyped.INSTANCE, "cap",
						getLocation());
				AccessChild capAccess = new AccessChild(GoIntType.INSTANCE, dereference,
						capProperty, getLocation());
				AnalysisState<A, H, V, T> capState = lenResult.smallStepSemantics(capAccess, this);

				AnalysisState<A, H, V, T> capResult = state.bottom();
				for (SymbolicExpression lenId : capState.getComputedExpressions())
					capResult = capResult.lub(
							capState.assign(lenId, new Constant(GoIntType.INSTANCE, arrayLength, getLocation()), this));

				if (getSubExpressions().length == 0)
					return capResult.smallStepSemantics(reference, this);
			}
		}

		/*
		 * Struct allocation
		 */
		if (getStaticType() instanceof GoStructType) {
			// Retrieve the struct type (that is a compilation unit)
			CompilationUnit structUnit = ((GoStructType) getStaticType()).getUnit();

			AnalysisState<A, H, V, T> result = state.bottom();

			for (SymbolicExpression containerExp : containerExps) {
				HeapReference reference = new HeapReference(type, containerExp, getLocation());
				HeapDereference dereference = new HeapDereference(type, reference, getLocation());

				if (getSubExpressions().length == 0) {
					result = result.lub(containerState);
					continue;
				}

				AnalysisState<A, H, V, T> tmp = containerState;

				for (int i = 0; i < keys.length; i++) {
					Variable field = getVariable((VariableRef) keys[i]);
					AccessChild access = new AccessChild(field.getStaticType(), dereference, field, getLocation());
					AnalysisState<A, H, V, T> fieldState = tmp.smallStepSemantics(access, this);
					for (SymbolicExpression id : fieldState.getComputedExpressions())
						for (SymbolicExpression v : params[i])
							tmp = fieldState.assign(id, NumericalTyper.type(v), this);
				}

				result = result.lub(tmp.smallStepSemantics(reference, this));
			}

			return result;
		}

		/*
		 * Map allocation
		 */
		if (getStaticType() instanceof GoMapType) {
			GoMapType mapType = (GoMapType) getStaticType();
			Type keyType = mapType.getKeyType();
			Type contentType = mapType.getElementType();

			AnalysisState<A, H, V, T> result = state.bottom();

			for (SymbolicExpression containerExp : containerExps) {
				HeapReference reference = new HeapReference(new ReferenceType(type), containerExp, getLocation());
				HeapDereference dereference = new HeapDereference(type, reference, getLocation());

				AnalysisState<A, H, V, T> tmp = containerState;

				for (int i = 0; i < keys.length; i++) {
					Variable keyProperty = new Variable(keyType, keys[i].toString(),
							getLocation());
					AccessChild keyAcces = new AccessChild(contentType, dereference,
							keyProperty, getLocation());

					AnalysisState<A, H, V, T> keyState = tmp.smallStepSemantics(keyAcces, this);

					AnalysisState<A, H, V, T> keyResult = state.bottom();
					for (SymbolicExpression keyId : keyState.getComputedExpressions())
						for (SymbolicExpression expr : params[i])
							keyResult = keyResult.lub(
									keyState.assign(keyId, expr, this));

					tmp = keyResult;
				}

				result = result.lub(tmp.smallStepSemantics(reference, this));

			}

			return result;
		}

		// TODO: to handle the other cases (maps...)

		if (type == Untyped.INSTANCE) {
			if (params.length > 0) {
				AnalysisState<A, H, V, T> result = state.bottom();
				for (ExpressionSet<SymbolicExpression> p : params) {
					for (SymbolicExpression e : p) {
						state.lub(state.smallStepSemantics(e, this));
					}
				}

				return result;
			} else {
				return state.smallStepSemantics(new Constant(Untyped.INSTANCE, "KEYED_LITERAL", getLocation()),
						getEvaluationPredecessor());
			}
		}

		return state.top().smallStepSemantics(new PushAny(type, getLocation()), this);

	}
}
