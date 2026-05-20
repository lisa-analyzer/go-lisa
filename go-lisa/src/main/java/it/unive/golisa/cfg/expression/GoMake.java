package it.unive.golisa.cfg.expression;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.type.composite.GoArrayType;
import it.unive.golisa.cfg.type.composite.GoChannelType;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractDomain;
import it.unive.lisa.analysis.AbstractLattice;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
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
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * A Go make expression (e.g., make([]int, 5)).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoMake extends NaryExpression {

	private final Type type;

	/**
	 * Builds the make expression.
	 * 
	 * @param cfg        the {@link CFG} where this expression lies
	 * @param location   the location where this expression is defined
	 * @param type       the type to allocate
	 * @param parameters the parameters
	 */
	public GoMake(CFG cfg, CodeLocation location, Type type, Expression[] parameters) {
		super(cfg, location, "make " + type, parameters);
		this.type = type;

		if (type instanceof GoSliceType) {
			// register the types
			Type contentType = ((GoSliceType) type).getContentType();

			// FIXME: currently, we handle just slice allocation where the
			// length is integer
			if (!(getSubExpressions()[0] instanceof GoInteger))
				return;

			int length = (int) ((GoInteger) getSubExpressions()[0]).getValue();

			GoArrayType.lookup(contentType, length);
			GoSliceType.lookup(contentType);
		}
	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractLattice<A>, D extends AbstractDomain<A>> AnalysisState<A> forwardSemanticsAux(
			InterproceduralAnalysis<A, D> interprocedural, AnalysisState<A> state,
			it.unive.lisa.lattices.ExpressionSet[] params, StatementStore<A> expressions) throws SemanticException {
		// No type information is provided and just a single type
		// is passed as argument and it should be allocated
		if (type == null)
			return state.top();

		// slice allocation
		if (type instanceof GoSliceType) {
			Type contentType = ((GoSliceType) type).getContentType();
			SourceCodeLocation sliceLocation = (SourceCodeLocation) getLocation();
			// FIXME: this is a temporary workaround. At this location, two
			// allocations are performed, need to differentiate
			SourceCodeLocation underlyingArrayLocation = new SourceCodeLocation(sliceLocation.getSourceFile(),
					sliceLocation.getLine(), sliceLocation.getCol() + 1);

			// FIXME: currently, we handle just slice allocation where the
			// length is integer
			if (!(getSubExpressions()[0] instanceof GoInteger))
				return interprocedural.getAnalysis()
						.smallStepSemantics(state.top(), new PushAny(type, getLocation()), this);

			// FIXME: currently, we handle just slice allocation where the
			// capability is integer
			if (getSubExpressions().length == 2 && !(getSubExpressions()[1] instanceof GoInteger))
				return interprocedural.getAnalysis()
						.smallStepSemantics(state.top(), new PushAny(type, getLocation()), this);

			int length = (int) ((GoInteger) getSubExpressions()[0]).getValue();
			int cap = getSubExpressions().length == 1 ? length : (int) ((GoInteger) getSubExpressions()[1]).getValue();

			GoArrayType arrayType = GoArrayType.lookup(contentType, length);
			Expression array = arrayType.defaultValue(getCFG(), underlyingArrayLocation);
			AnalysisState<A> arraySemantics = array.forwardSemantics(state, interprocedural,
					new StatementStore<>(state));

			// Allocates the slice, that is an array of three elements: pointer
			// to the underlying array, length and capability
			GoSliceType sliceType = GoSliceType.lookup(contentType);

			MemoryAllocation sliceCreated = new MemoryAllocation(sliceType, getLocation(), new Annotations());

			// Allocates the new heap allocation
			AnalysisState<A> containerState = interprocedural.getAnalysis().smallStepSemantics(arraySemantics,
					sliceCreated, this);
			var sliceExps = containerState.getExecutionExpressions();

			AnalysisState<A> result = state.bottom();

			for (SymbolicExpression sliceExp : sliceExps) {
				HeapReference sliceRef = new HeapReference(sliceType, sliceExp,
						getLocation());
				HeapDereference sliceDeref = new HeapDereference(sliceType, sliceRef,
						getLocation());

				// Allocates the len property of the slice
				Variable lenProperty = new Variable(Untyped.INSTANCE, "len",
						getLocation());
				AccessChild lenAccess = new AccessChild(GoIntType.INSTANCE, sliceDeref,
						lenProperty, getLocation());
				AnalysisState<
						A> lenState = interprocedural.getAnalysis().smallStepSemantics(containerState, lenAccess, this);

				AnalysisState<A> lenResult = state.bottom();
				for (SymbolicExpression lenId : lenState.getExecutionExpressions())
					lenResult = lenResult
							.lub(interprocedural.getAnalysis().assign(lenState, lenId,
									new Constant(GoIntType.INSTANCE, length, getLocation()), this));

				// Allocates the cap property of the slice
				Variable capProperty = new Variable(Untyped.INSTANCE, "cap",
						getLocation());
				AccessChild capAccess = new AccessChild(GoIntType.INSTANCE, sliceDeref,
						capProperty, getLocation());
				AnalysisState<
						A> capState = interprocedural.getAnalysis().smallStepSemantics(lenResult, capAccess, this);
				AnalysisState<A> capResult = state.bottom();
				for (SymbolicExpression lenId : capState.getExecutionExpressions())
					capResult = capResult
							.lub(interprocedural.getAnalysis().assign(capState, lenId,
									new Constant(GoIntType.INSTANCE, cap, getLocation()), this));

				// Allocates the ptr property of the slice
				Variable ptrProperty = new Variable(Untyped.INSTANCE, "ptr",
						getLocation());
				AccessChild ptrAccess = new AccessChild(arrayType, sliceDeref,
						ptrProperty, getLocation());
				AnalysisState<
						A> ptrState = interprocedural.getAnalysis().smallStepSemantics(capResult, ptrAccess, this);
				AnalysisState<A> ptrResult = state.bottom();
				for (SymbolicExpression ptrId : ptrState.getExecutionExpressions())
					for (SymbolicExpression arrayId : arraySemantics.getExecutionExpressions())
						ptrResult = ptrResult.lub(interprocedural.getAnalysis().assign(ptrState, ptrId, arrayId, this));

				result = result.lub(interprocedural.getAnalysis().smallStepSemantics(ptrResult, sliceRef, this));
			}

			return result;
		}

		// channel allocation
		if (type instanceof GoChannelType)
			return interprocedural.getAnalysis().smallStepSemantics(state.top(), new PushAny(type, getLocation()),
					this);

		// map allocation
		if (type instanceof GoMapType)
			return interprocedural.getAnalysis().smallStepSemantics(state, new PushAny(type, getLocation()),
					this);

		return interprocedural.getAnalysis().smallStepSemantics(state.top(), new PushAny(type, getLocation()),
				this);
	}
}
