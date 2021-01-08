package it.unive.golisa.analysis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import it.unive.lisa.analysis.InverseSetLattice;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;

public class StringUpperBound extends InverseSetLattice<StringUpperBound, ValueExpression> implements Iterable<ValueExpression> {

	private static final StringUpperBound TOP = new StringUpperBound();

	private static final StringUpperBound BOTTOM = new StringUpperBound(new HashSet<>(), false, true);

	private final boolean isTop, isBottom;
	
	public StringUpperBound() {
		this(new HashSet<>(), true, false);
	}
	
	public StringUpperBound(Set<ValueExpression> set) {
		this(set, false, false);
	}
	
	private StringUpperBound(Set<ValueExpression> set, boolean isTop, boolean isBottom) {
		super(set);
		this.isBottom = isBottom;
		this.isTop = isTop;
	}
		
	@Override
	public StringUpperBound top() {
		return TOP;
	}

	@Override
	public StringUpperBound bottom() {
		return BOTTOM;
	}

	@Override
	public boolean isTop() {
		return isTop;
	}

	@Override
	public boolean isBottom() {
		return isBottom;
	}
	
	@Override
	protected StringUpperBound mk(Set<ValueExpression> set) {
		return new StringUpperBound(set, false, false);
	}

	public StringUpperBound merge(StringUpperBound other) {
		if (other.isBottom())
			return BOTTOM;
		if (other.isTop())
			return this;
		if (isTop())
			return other;
			
		HashSet<ValueExpression> result = new HashSet<>(this.elements);
		result.addAll(other.elements);
		return new StringUpperBound(result);
	}
	
	public boolean contains(StringUpperBound other) {
		return this.elements.containsAll(other.elements);
	}
	
	public boolean contains(ValueExpression other) {
		return this.elements.contains(other);
	}
	
	public StringUpperBound addRelation(ValueExpression exp) {
		HashSet<ValueExpression> exps = new HashSet<>(elements);
		exps.add(exp);
		return new StringUpperBound(exps);
	}

	@Override
	public Iterator<ValueExpression> iterator() {
		return this.elements.iterator();
	}

	public StringUpperBound removeRelation(ValueExpression exp) {
		HashSet<ValueExpression> exps = new HashSet<>(elements);
		exps.remove(exp);
		return new StringUpperBound(exps);
	}
}
