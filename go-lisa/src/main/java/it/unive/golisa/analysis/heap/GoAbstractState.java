package it.unive.golisa.analysis.heap;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.BaseLattice;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.ObjectRepresentation;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.HeapLocation;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.MemoryPointer;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.collections.externalSet.ExternalSet;
import java.util.Map;
import java.util.function.Predicate;
import org.apache.commons.lang3.tuple.Pair;

/**
 * An abstract state of the analysis for Go, composed by a value state modeling
 * values of program variables and memory locations. The heap state is a
 * {@link GoPointBasedHeap}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 * 
 * @param <V> the type of {@link ValueDomain} embedded in this state
 * @param <T> the type of {@link TypeDomain} embedded in this state
 */
public class GoAbstractState<V extends ValueDomain<V>,
		T extends TypeDomain<T>>
		extends BaseLattice<GoAbstractState<V, T>>
		implements AbstractState<GoAbstractState<V, T>, GoPointBasedHeap, V, T> {

	/**
	 * The domain containing information regarding heap structures
	 */
	private final GoPointBasedHeap heapState;

	/**
	 * The domain containing information regarding values of program variables
	 * and concretized memory locations
	 */
	private final V valueState;

	/**
	 * The domain containing runtime types information regarding runtime types
	 * of program variables and concretized memory locations
	 */
	private final T typeState;

	/**
	 * Builds a new abstract state.
	 * 
	 * @param heapState  the domain containing information regarding heap
	 *                       structures
	 * @param valueState the domain containing information regarding values of
	 *                       program variables and concretized memory locations
	 * @param typeState  the domain containing information regarding runtime
	 *                       types of program variables and concretized memory
	 *                       locations
	 */
	public GoAbstractState(GoPointBasedHeap heapState, V valueState, T typeState) {
		this.heapState = heapState;
		this.valueState = valueState;
		this.typeState = typeState;
	}

	@Override
	public GoPointBasedHeap getHeapState() {
		return heapState;
	}

	@Override
	public V getValueState() {
		return valueState;
	}

	@Override
	public T getTypeState() {
		return typeState;
	}

	@Override
	public GoAbstractState<V, T> assign(Identifier id, SymbolicExpression expression, ProgramPoint pp)
			throws SemanticException {
		GoPointBasedHeap heap = heapState.assign(id, expression, pp);
		ExpressionSet<ValueExpression> exprs = heap.rewrite(expression, pp);

		V value = valueState;
		T type = typeState;

		for (Pair<HeapLocation, HeapLocation> p : heap.getDecouples()) {
			type = type.assign(p.getLeft(), p.getRight(), pp);

			ExternalSet<Type> rt = type.getInferredRuntimeTypes();
			p.getLeft().setRuntimeTypes(rt);
			p.getRight().setRuntimeTypes(rt);
			value = value.assign(p.getLeft(), p.getRight(), pp);
		}

		heap.getDecouples().clear();

		if (heap.getSubstitution() != null && !heap.getSubstitution().isEmpty()) {
			type = type.applySubstitution(heap.getSubstitution(), pp);
			value = value.applySubstitution(heap.getSubstitution(), pp);
		}

		for (ValueExpression expr : exprs) {
			type = type.assign(id, expr, pp);

			ExternalSet<Type> rt = type.getInferredRuntimeTypes();
			id.setRuntimeTypes(rt);
			expr.setRuntimeTypes(rt);

			if (expr instanceof MemoryPointer)
				value = value.assign(id, ((MemoryPointer) expr).getReferencedLocation(), pp);
			else
				value = value.assign(id, expr, pp);
		}

		return new GoAbstractState<>(heap, value, type);
	}

	@Override
	public GoAbstractState<V, T> smallStepSemantics(SymbolicExpression expression, ProgramPoint pp)
			throws SemanticException {
		GoPointBasedHeap heap = heapState.smallStepSemantics(expression, pp);
		ExpressionSet<ValueExpression> exprs = heap.rewrite(expression, pp);

		T type = typeState;
		V value = valueState;
		if (heap.getSubstitution() != null && !heap.getSubstitution().isEmpty()) {
			type = type.applySubstitution(heap.getSubstitution(), pp);
			value = value.applySubstitution(heap.getSubstitution(), pp);
		}

		for (ValueExpression expr : exprs) {
			type = type.smallStepSemantics(expr, pp);

			ExternalSet<Type> rt = type.getInferredRuntimeTypes();
			expr.setRuntimeTypes(rt);

			if (expr instanceof MemoryPointer)
				value = value.smallStepSemantics(((MemoryPointer) expr).getReferencedLocation(), pp);
			else
				value = value.smallStepSemantics(expr, pp);

		}

		return new GoAbstractState<>(heap, value, type);
	}

	@Override
	public GoAbstractState<V, T> assume(SymbolicExpression expression, ProgramPoint pp)
			throws SemanticException {
		GoPointBasedHeap heap = heapState.assume(expression, pp);
		ExpressionSet<ValueExpression> exprs = heap.rewrite(expression, pp);

		T type = typeState;
		V value = valueState;
		if (heap.getSubstitution() != null && !heap.getSubstitution().isEmpty()) {
			type = type.applySubstitution(heap.getSubstitution(), pp);
			value = value.applySubstitution(heap.getSubstitution(), pp);
		}

		for (ValueExpression expr : exprs) {
			T tmp = type.smallStepSemantics(expr, pp);
			ExternalSet<Type> rt = tmp.getInferredRuntimeTypes();
			expr.setRuntimeTypes(rt);

			type = type.assume(expr, pp);
			value = value.assume(expr, pp);
		}

		return new GoAbstractState<>(heap, value, type);
	}

	@Override
	public Satisfiability satisfies(SymbolicExpression expression, ProgramPoint pp) throws SemanticException {
		ExpressionSet<ValueExpression> rewritten = heapState.rewrite(expression, pp);
		Satisfiability typeResult = Satisfiability.BOTTOM;
		Satisfiability valueResult = Satisfiability.BOTTOM;
		for (ValueExpression expr : rewritten) {
			T tmp = typeState.smallStepSemantics(expr, pp);
			ExternalSet<Type> rt = tmp.getInferredRuntimeTypes();
			expr.setRuntimeTypes(rt);

			typeResult = typeResult.lub(typeState.satisfies(expr, pp));
			valueResult = valueResult.lub(valueState.satisfies(expr, pp));
		}
		return heapState.satisfies(expression, pp).glb(typeResult).glb(valueResult);
	}

	@Override
	public GoAbstractState<V, T> pushScope(ScopeToken scope) throws SemanticException {
		return new GoAbstractState<>(
				heapState.pushScope(scope),
				valueState.pushScope(scope),
				typeState.pushScope(scope));
	}

	@Override
	public GoAbstractState<V, T> popScope(ScopeToken scope) throws SemanticException {
		return new GoAbstractState<>(
				heapState.popScope(scope),
				valueState.popScope(scope),
				typeState.popScope(scope));
	}

	@Override
	public GoAbstractState<V, T> lubAux(GoAbstractState<V, T> other) throws SemanticException {
		return new GoAbstractState<>(
				heapState.lub(other.heapState),
				valueState.lub(other.valueState),
				typeState.lub(other.typeState));
	}

	@Override
	public GoAbstractState<V, T> wideningAux(GoAbstractState<V, T> other) throws SemanticException {
		return new GoAbstractState<>(
				heapState.widening(other.heapState),
				valueState.widening(other.valueState),
				typeState.widening(other.typeState));
	}

	@Override
	public boolean lessOrEqualAux(GoAbstractState<V, T> other) throws SemanticException {
		return heapState.lessOrEqual(other.heapState)
				&& valueState.lessOrEqual(other.valueState)
				&& typeState.lessOrEqual(other.typeState);
	}

	@Override
	public GoAbstractState<V, T> top() {
		return new GoAbstractState<>(heapState.top(), valueState.top(), typeState.top());
	}

	@Override
	public GoAbstractState<V, T> bottom() {
		return new GoAbstractState<>(heapState.bottom(), valueState.bottom(), typeState.bottom());
	}

	@Override
	public boolean isTop() {
		return heapState.isTop() && valueState.isTop() && typeState.isTop();
	}

	@Override
	public boolean isBottom() {
		return heapState.isBottom() && valueState.isBottom() && typeState.isBottom();
	}

	@Override
	public GoAbstractState<V, T> forgetIdentifier(Identifier id) throws SemanticException {
		return new GoAbstractState<>(
				heapState.forgetIdentifier(id),
				valueState.forgetIdentifier(id),
				typeState.forgetIdentifier(id));
	}

	@Override
	public GoAbstractState<V, T> forgetIdentifiersIf(Predicate<Identifier> test) throws SemanticException {
		return new GoAbstractState<>(
				heapState.forgetIdentifiersIf(test),
				valueState.forgetIdentifiersIf(test),
				typeState.forgetIdentifiersIf(test));
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((heapState == null) ? 0 : heapState.hashCode());
		result = prime * result + ((valueState == null) ? 0 : valueState.hashCode());
		result = prime * result + ((typeState == null) ? 0 : typeState.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoAbstractState<?, ?> other = (GoAbstractState<?, ?>) obj;
		if (heapState == null) {
			if (other.heapState != null)
				return false;
		} else if (!heapState.equals(other.heapState))
			return false;
		if (valueState == null) {
			if (other.valueState != null)
				return false;
		} else if (!valueState.equals(other.valueState))
			return false;
		if (typeState == null) {
			if (other.typeState != null)
				return false;
		} else if (!typeState.equals(other.typeState))
			return false;
		return true;
	}

	@Override
	public DomainRepresentation representation() {
		DomainRepresentation h = heapState.representation();
		DomainRepresentation t = typeState.representation();
		DomainRepresentation v = valueState.representation();
		return new ObjectRepresentation(Map.of(
				HEAP_REPRESENTATION_KEY, h,
				TYPE_REPRESENTATION_KEY, t,
				VALUE_REPRESENTATION_KEY, v));
	}

	@Override
	public String toString() {
		return representation().toString();
	}

	@Override
	@SuppressWarnings("unchecked")
	public <D> D getDomainInstance(Class<D> domain) {
		if (domain.isAssignableFrom(getClass()))
			return (D) this;

		D di = heapState.getDomainInstance(domain);
		if (di != null)
			return di;

		di = typeState.getDomainInstance(domain);
		if (di != null)
			return di;

		return valueState.getDomainInstance(domain);
	}
}
