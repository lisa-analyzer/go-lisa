package it.unive.golisa.analysis;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import it.unive.lisa.analysis.lattices.InverseSetLattice;
import it.unive.lisa.symbolic.SymbolicExpression;

public class ExpressionInverseSet<T extends SymbolicExpression> extends InverseSetLattice<ExpressionInverseSet<T>, T> {

	private final boolean isTop;
	
	public ExpressionInverseSet() {
		this(Collections.emptySet(), true);
	}
	
	public ExpressionInverseSet(Set<T> set) {
		this(set, false);
	}
	
	private ExpressionInverseSet(boolean isTop) {
		this(Collections.emptySet(), isTop);
	}
	
	private ExpressionInverseSet(Set<T> set, boolean isTop) {
		super(set);
		this.isTop = isTop;
	}
		
	@Override
	public ExpressionInverseSet<T> top() {
		return new ExpressionInverseSet<T>(true);
	}

	@Override
	public ExpressionInverseSet<T> bottom() {
		return new ExpressionInverseSet<T>(false);
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
	protected ExpressionInverseSet<T> mk(Set<T> set) {
		return new ExpressionInverseSet<T>(set);
	}

	public boolean contains(ExpressionInverseSet<T> other) {
		return this.elements.containsAll(other.elements);
	}
	
	public ExpressionInverseSet<T> addExpression(T exp) {
		HashSet<T> exps = new HashSet<>(elements);
		exps.add(exp);
		return new ExpressionInverseSet<T>(exps);
	}
	
	public ExpressionInverseSet<T> removeExpression(T exp) {
		HashSet<T> exps = new HashSet<>(elements);
		exps.remove(exp);
		return new ExpressionInverseSet<T>(exps);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + (isTop ? 1231 : 1237);
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		ExpressionInverseSet<T> other = (ExpressionInverseSet<T>) obj;
		if (isTop != other.isTop)
			return false;
		return true;
	}
}
