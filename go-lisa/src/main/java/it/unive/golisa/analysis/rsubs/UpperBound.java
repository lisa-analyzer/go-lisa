package it.unive.golisa.analysis.rsubs;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import it.unive.lisa.analysis.lattices.InverseSetLattice;
import it.unive.lisa.symbolic.SymbolicExpression;

public class UpperBound<T extends SymbolicExpression> extends InverseSetLattice<UpperBound<T>, T> {

	private final boolean isTop;
	
	public UpperBound() {
		this(Collections.emptySet(), true);
	}
	
	public UpperBound(Set<T> set) {
		this(set, false);
	}
	
	private UpperBound(boolean isTop) {
		this(Collections.emptySet(), isTop);
	}
	
	private UpperBound(Set<T> set, boolean isTop) {
		super(set);
		this.isTop = isTop;
	}
		
	@Override
	public UpperBound<T> top() {
		return new UpperBound<T>(true);
	}

	@Override
	public UpperBound<T> bottom() {
		return new UpperBound<T>();
	}

	@Override
	public boolean isTop() {
		return isTop;
	}

	@Override
	public boolean isBottom() {
		return !isTop && elements.isEmpty();
	}
	
	@Override
	protected UpperBound<T> mk(Set<T> set) {
		return new UpperBound<T>(set, false);
	}

	public UpperBound<T> merge(UpperBound<T> other) {
		if (other.isBottom())
			return bottom();
		if (other.isTop())
			return this;
		if (isTop())
			return other;
			
		HashSet<T> result = new HashSet<>(this.elements);
		result.addAll(other.elements);
		return new UpperBound<T>(result);
	}

	public boolean contains(UpperBound<T> other) {
		return this.elements.containsAll(other.elements);
	}
	
	public UpperBound<T> addExpression(T exp) {
		HashSet<T> exps = new HashSet<>(elements);
		exps.add(exp);
		return new UpperBound<T>(exps);
	}

	public UpperBound<T> removeExpression(T exp) {
		HashSet<T> exps = new HashSet<>(elements);
		exps.remove(exp);
		return new UpperBound<T>(exps);
	}
}
