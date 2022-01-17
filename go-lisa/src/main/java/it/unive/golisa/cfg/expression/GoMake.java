package it.unive.golisa.cfg.expression;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.type.GoType;
import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.golisa.cfg.type.composite.GoChannelType;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
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
import it.unive.lisa.type.Untyped;

public class GoMake extends NaryExpression {

	private final GoType type;

	public GoMake(CFG cfg, CodeLocation location, GoType type, Expression[] parameters) {
		super(cfg, location, "make " + type, parameters);
		this.type = type;
	}

	@Override
	public <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> expressionSemantics(
					InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
					ExpressionSet<SymbolicExpression>[] params) throws SemanticException {
		// No type information is provided and just a single type
		// is passed as argument and it should be allocated
		if (type == null)
			return state.top();

		/**
		 * Slice allocation
		 */
		if (type instanceof GoSliceType) {
			GoType contentType = (GoType) ((GoSliceType) type).getContentType();
			SourceCodeLocation sliceLocation = (SourceCodeLocation) getLocation();
			// FIXME: this is a temporary workaround. At this location, two
			// allocations are performed, need to differentiate
			SourceCodeLocation underlyingArrayLocation = new SourceCodeLocation(sliceLocation.getSourceFile(),
					sliceLocation.getLine(), sliceLocation.getCol() + 1);

			// FIXME: currently, we handle just slice allocation where the
			// length is integer
			if (!(getSubExpressions()[0] instanceof GoInteger))
				return state.top()
						.smallStepSemantics(new PushAny(Caches.types().mkSingletonSet(type), getLocation()), this);

			// FIXME: currently, we handle just slice allocation where the
			// capability is integer
			if (getSubExpressions().length == 2 && !(getSubExpressions()[1] instanceof GoInteger))
				return state.top()
						.smallStepSemantics(new PushAny(Caches.types().mkSingletonSet(type), getLocation()), this);

			int length = (int) ((GoInteger) getSubExpressions()[0]).getValue();
			int cap = getSubExpressions().length == 1 ? length : (int) ((GoInteger) getSubExpressions()[1]).getValue();

			GoArrayType arrayType = GoArrayType.lookup(new GoArrayType(contentType, length));
			Expression array = arrayType.defaultValue(getCFG(), underlyingArrayLocation);
			AnalysisState<A, H, V> arraySemantics = array.semantics(state, interprocedural,
					new StatementStore<>(state));

			// Allocates the slice, that is an array of three elements: pointer
			// to the underlying array, length and capability
			GoSliceType sliceType = GoSliceType.lookup(new GoSliceType(contentType));

			HeapAllocation sliceCreated = new HeapAllocation(Caches.types().mkSingletonSet(sliceType), getLocation());

			// Allocates the new heap allocation
			AnalysisState<A, H, V> containerState = arraySemantics.smallStepSemantics(sliceCreated, this);
			ExpressionSet<SymbolicExpression> sliceExps = containerState.getComputedExpressions();

			AnalysisState<A, H, V> result = state.bottom();

			for (SymbolicExpression sliceExp : sliceExps) {
				HeapReference sliceRef = new HeapReference(Caches.types().mkSingletonSet(sliceType), sliceExp,
						getLocation());
				HeapDereference sliceDeref = new HeapDereference(Caches.types().mkSingletonSet(sliceType), sliceRef,
						getLocation());

				// Allocates the len property of the slice
				Variable lenProperty = new Variable(Caches.types().mkSingletonSet(Untyped.INSTANCE), "len",
						getLocation());
				AccessChild lenAccess = new AccessChild(Caches.types().mkSingletonSet(GoIntType.INSTANCE), sliceDeref,
						lenProperty, getLocation());
				AnalysisState<A, H, V> lenState = containerState.smallStepSemantics(lenAccess, this);

				AnalysisState<A, H, V> lenResult = state.bottom();
				for (SymbolicExpression lenId : lenState.getComputedExpressions())
					lenResult = lenResult
							.lub(lenState.assign(lenId, new Constant(GoIntType.INSTANCE, length, getLocation()), this));

				// Allocates the cap property of the slice
				Variable capProperty = new Variable(Caches.types().mkSingletonSet(Untyped.INSTANCE), "cap",
						getLocation());
				AccessChild capAccess = new AccessChild(Caches.types().mkSingletonSet(GoIntType.INSTANCE), sliceDeref,
						capProperty, getLocation());
				AnalysisState<A, H, V> capState = lenResult.smallStepSemantics(capAccess, this);

				AnalysisState<A, H, V> capResult = state.bottom();
				for (SymbolicExpression lenId : capState.getComputedExpressions())
					capResult = capResult
							.lub(capState.assign(lenId, new Constant(GoIntType.INSTANCE, cap, getLocation()), this));

				// Allocates the ptr property of the slice
				Variable ptrProperty = new Variable(Caches.types().mkSingletonSet(Untyped.INSTANCE), "ptr",
						getLocation());
				AccessChild ptrAccess = new AccessChild(Caches.types().mkSingletonSet(arrayType), sliceDeref,
						ptrProperty, getLocation());
				AnalysisState<A, H, V> ptrState = capResult.smallStepSemantics(ptrAccess, this);

				AnalysisState<A, H, V> ptrResult = state.bottom();
				for (SymbolicExpression ptrId : ptrState.getComputedExpressions())
					for (SymbolicExpression arrayId : arraySemantics.getComputedExpressions())
						ptrResult = ptrResult.lub(ptrState.assign(ptrId, arrayId, this));

				result = result.lub(ptrResult.smallStepSemantics(sliceRef, this));
			}

			return result;
		}

		/**
		 * Channel allocation
		 */
		if (type instanceof GoChannelType)
			return state.top().smallStepSemantics(new PushAny(Caches.types().mkSingletonSet(type), getLocation()),
					this);

		/**
		 * Map allocation
		 */
		if (type instanceof GoMapType)
			return state.top().smallStepSemantics(new PushAny(Caches.types().mkSingletonSet(type), getLocation()),
					this);

		return state.top().smallStepSemantics(new PushAny(Caches.types().mkSingletonSet(type), getLocation()),
				this);
	}

}
