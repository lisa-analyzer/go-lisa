package it.unive.golisa.analysis;

import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import it.unive.golisa.analysis.tarsis.TarsisIntv;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.PairRepresentation;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;

public class Pentagons implements ValueDomain<Pentagons> {

	protected ValueEnvironment<TarsisIntv> left;

	protected StrictUpperBounds right;
	
	public Pentagons() {
		this(new ValueEnvironment<TarsisIntv>(new TarsisIntv()), new StrictUpperBounds());
	}

	private Pentagons(ValueEnvironment<TarsisIntv> left, StrictUpperBounds right) {
		this.left = left;
		this.right = right;
	}

	@Override
	public Pentagons assign(Identifier id, ValueExpression expression, ProgramPoint pp)
			throws SemanticException {
		ValueEnvironment<TarsisIntv> TarsisIntvs = left.assign(id, expression, pp);
		StrictUpperBounds upperBounds = right.assign(id, expression, pp);
		return new Pentagons(TarsisIntvs, upperBounds).refine();
	}

	@Override
	public Pentagons smallStepSemantics(ValueExpression expression, ProgramPoint pp)
			throws SemanticException {
		ValueEnvironment<TarsisIntv> TarsisIntvs = left.smallStepSemantics(expression, pp);
		StrictUpperBounds upperBounds = right.smallStepSemantics(expression, pp);
		return new Pentagons(TarsisIntvs, upperBounds).refine();
	}
	
	@Override
	public Pentagons assume(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		ValueEnvironment<TarsisIntv> newLeft = left.assume(expression, pp);
		StrictUpperBounds newRight = right.assume(expression, pp);
		return new Pentagons(newLeft, newRight);
	}

	@Override
	public Pentagons forgetIdentifier(Identifier id) throws SemanticException {
		ValueEnvironment<TarsisIntv> newLeft = left.forgetIdentifier(id);
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
		ValueEnvironment<TarsisIntv> env = new ValueEnvironment<TarsisIntv>(new TarsisIntv());
		for (Entry<Identifier, ExpressionInverseSet<Identifier>> entry : right.getMap().entrySet()) {
			Identifier id = entry.getKey();
			TarsisIntv idTarsisIntv = left.getState(id);

			for (Identifier upperBound : entry.getValue()) {
				TarsisIntv boundTarsisIntv = new TarsisIntv(null, left.getState(upperBound).getHigh());
				left.putState(id, idTarsisIntv.glb(boundTarsisIntv));
				refined.add(id);
			}
		}

		for (Identifier id : left.getKeys())
			if (!refined.contains(id))
				env.putState(id, left.getState(id));

		return new Pentagons(env, right);
	}

	@Override
	public Pentagons pushScope(ScopeToken token) throws SemanticException {
		return new Pentagons(left.pushScope(token), right.pushScope(token));
	}

	@Override
	public Pentagons popScope(ScopeToken token) throws SemanticException {
		return new Pentagons(left.popScope(token), right.popScope(token));
	}
}
