package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration.NumericalTyper;
import it.unive.golisa.cfg.type.composite.GoArrayType;
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
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
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

public class GoNonKeyedLiteral extends NaryExpression {

	public GoNonKeyedLiteral(CFG cfg, SourceCodeLocation location, Expression[] value, Type staticType) {
		super(cfg, location, "nonKeyedLit(" + staticType + ")", staticType, value);
	}

	private SymbolicExpression getVariable(Global global) {
		return new Variable(global.getStaticType(), global.getName(),
				global.getLocation());
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

		if (getStaticType() instanceof GoStructType) {
			// Retrieve the struct type (that is a compilation unit)
			CompilationUnit structUnit = ((GoStructType) type).getUnit();

			AnalysisState<A, H, V, T> result = state.bottom();

			for (SymbolicExpression containerExp : containerExps) {
				HeapReference reference = new HeapReference(new ReferenceType(type), containerExp, getLocation());
				HeapDereference dereference = new HeapDereference(type, reference, getLocation());

				if (getSubExpressions().length == 0) {
					result = result.lub(containerState);
					continue;
				}

				int i = 0;
				AnalysisState<A, H, V, T> tmp = containerState;

				for (Global field : structUnit.getInstanceGlobals(true)) {
					AccessChild access = new AccessChild(field.getStaticType(), dereference, getVariable(field),
							getLocation());
					AnalysisState<A, H, V, T> fieldState = tmp.smallStepSemantics(access, this);
					for (SymbolicExpression id : fieldState.getComputedExpressions())
						if (i < params.length)
							for (SymbolicExpression v : params[i])
								tmp = fieldState.assign(id, NumericalTyper.type(v), this);
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
			AnalysisState<A, H, V, T> result = state.bottom();

			GoArrayType arrayType = (GoArrayType) getStaticType();
			Type contentType = arrayType.getContenType();
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

				if (getSubExpressions().length == 0) {
					result = result.lub(capResult);
					continue;
				}

				// Allocate the heap location
				AnalysisState<A, H, V, T> tmp = capResult;
				for (int i = 0; i < arrayLength; i++) {
					AccessChild access = new AccessChild(contentType, dereference,
							new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
					AnalysisState<A, H, V, T> accessState = tmp.smallStepSemantics(access, this);

					for (SymbolicExpression index : accessState.getComputedExpressions())
						for (SymbolicExpression v : params[i])
							tmp = tmp.assign(index, NumericalTyper.type(v), this);
				}
				
				result = result.lub(tmp.smallStepSemantics(reference, this));
			}

			return result;
		} else if (getStaticType() instanceof GoSliceType) {
			AnalysisState<A, H, V, T> result = state.bottom();

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
				AnalysisState<A, H, V, T> lenState = containerState.smallStepSemantics(lenAccess, this);

				AnalysisState<A, H, V, T> lenResult = state.bottom();
				for (SymbolicExpression lenId : lenState.getComputedExpressions())
					lenResult = lenResult.lub(
							lenState.assign(lenId, new Constant(GoIntType.INSTANCE, sliceLenght, getLocation()), this));

				// Assign the cap property to this hid
				Variable capProperty = new Variable(Untyped.INSTANCE, "cap",
						getLocation());
				AccessChild capAccess = new AccessChild(GoIntType.INSTANCE, dereference,
						capProperty, getLocation());
				AnalysisState<A, H, V, T> capState = lenResult.smallStepSemantics(capAccess, this);

				AnalysisState<A, H, V, T> capResult = state.bottom();
				for (SymbolicExpression lenId : capState.getComputedExpressions())
					capResult = capResult.lub(
							capState.assign(lenId, new Constant(GoIntType.INSTANCE, sliceLenght, getLocation()), this));

				if (getSubExpressions().length == 0) {
					result = result.lub(capResult);
					continue;
				}

				// Allocate the heap location
				AnalysisState<A, H, V, T> tmp = capResult;
				for (int i = 0; i < sliceLenght; i++) {
					AccessChild access = new AccessChild(contentType, dereference,
							new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
					AnalysisState<A, H, V, T> accessState = tmp.smallStepSemantics(access, this);

					for (SymbolicExpression index : accessState.getComputedExpressions())
						for (SymbolicExpression v : params[i])
							tmp = tmp.assign(index, NumericalTyper.type(v), this);
				}

				result = result.lub(tmp.smallStepSemantics(reference, this));
			}

			return result;
		}

		// TODO: to handle the other cases (maps...)
		return state.top().smallStepSemantics(new PushAny(type, getLocation()), this);

	}
}
