package it.unive.golisa.analysis;

import java.util.HashSet;
import java.util.Set;

import it.unive.lisa.analysis.InverseSetLattice;
import it.unive.lisa.symbolic.value.ValueExpression;

public class StringUpperBound extends InverseSetLattice<StringUpperBound, ValueExpression>{

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

}
