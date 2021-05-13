package it.unive.golisa.analysis;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.impl.numeric.Interval;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.PairRepresentation;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;

public class Pentagons implements ValueDomain<Pentagons> {

	protected ValueEnvironment<Interval> left;

	protected StrictUpperBounds right;
	
	public Pentagons() {
		this(new ValueEnvironment<Interval>(new Interval()), new StrictUpperBounds());
	}

	private Pentagons(ValueEnvironment<Interval> left, StrictUpperBounds right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public Pentagons assign(Identifier id, ValueExpression expression, ProgramPoint pp)
			throws SemanticException {
		ValueEnvironment<Interval> intervals = left.assign(id, expression, pp);
		StrictUpperBounds upperBounds = right.assign(id, expression, pp);
		return new Pentagons(intervals, upperBounds).refine();
	}

	@Override
	public Pentagons smallStepSemantics(ValueExpression expression, ProgramPoint pp)
			throws SemanticException {
		ValueEnvironment<Interval> intervals = left.smallStepSemantics(expression, pp);
		StrictUpperBounds upperBounds = right.smallStepSemantics(expression, pp);
		return new Pentagons(intervals, upperBounds).refine();
	}
	
	@Override
	public Pentagons assume(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		ValueEnvironment<Interval> newLeft = left.assume(expression, pp);
		StrictUpperBounds newRight = right.assume(expression, pp);
		return new Pentagons(newLeft, newRight);
	}

	@Override
	public Pentagons forgetIdentifier(Identifier id) throws SemanticException {
		ValueEnvironment<Interval> newLeft = left.forgetIdentifier(id);
		StrictUpperBounds newRight = right.forgetIdentifier(id);
		return new Pentagons(newLeft, newRight);
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		return left.satisfies(expression, pp).and(right.satisfies(expression, pp));
	}

	@Override
	public String toString() {
		return representation().toString();
	}

	@Override
	public DomainRepresentation representation() {
		return new PairRepresentation(left.representation(), right.representation());
	}

	@Override
	public Pentagons lub(Pentagons other) throws SemanticException {
		return new Pentagons(left.lub(other.left), right.lub(other.right));
	}

	@Override
	public Pentagons widening(Pentagons other) throws SemanticException {
		return new Pentagons(left.widening(other.left), right.widening(other.right));
	}

	@Override
	public boolean lessOrEqual(Pentagons other) throws SemanticException {
		return left.lessOrEqual(other.left) && right.lessOrEqual(other.right);
	}

	@Override
	public Pentagons top() {
		return new Pentagons(left.top(), right.top());
	}

	@Override
	public Pentagons bottom() {
		return new Pentagons(left.bottom(), right.bottom());
	}

	private Pentagons refine() throws SemanticException {

		if (left.isTop() || right.isTop())
			return this;
		
		Set<Identifier> refined = new HashSet<>();
		Map<Identifier, Interval> newFunc = new HashMap<Identifier, Interval>();
		for (Entry<Identifier, ExpressionInverseSet<Identifier>> entry : right.getMap().entrySet()) {
			Identifier id = entry.getKey();
			Interval idInterval = left.getState(id);

			for (Identifier upperBound : entry.getValue()) {
				Interval boundInterval = new Interval(null, left.getState(upperBound).getHigh());
				newFunc.put(id, idInterval.glb(boundInterval));
				refined.add(id);
			}
		}
		
		for (Identifier id : left.getKeys())
			if (!refined.contains(id))
				newFunc.put(id, left.getState(id));

		return new Pentagons(new ValueEnvironment<Interval>(new Interval(), newFunc), right);
	}

}
