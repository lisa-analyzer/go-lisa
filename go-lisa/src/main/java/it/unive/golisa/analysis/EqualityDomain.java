package it.unive.golisa.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.lattices.FunctionalLattice;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.OutOfScopeIdentifier;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;

public class EqualityDomain extends FunctionalLattice<EqualityDomain, Identifier, ExpressionInverseSet<Identifier>> implements ValueDomain<EqualityDomain> {

	public EqualityDomain() {
		this(new ExpressionInverseSet<>(), null);
	}

	private EqualityDomain(ExpressionInverseSet<Identifier> lattice, Map<Identifier, ExpressionInverseSet<Identifier>> function) {
		super(lattice, function);
	}

	@Override
	public EqualityDomain assign(Identifier id, ValueExpression expression, ProgramPoint pp) throws SemanticException {
		if (expression instanceof Identifier) {
			Map<Identifier, ExpressionInverseSet<Identifier>> func;
			if (function == null)
				func = new HashMap<>();
			else
				func = new HashMap<>(function);

			func.put(id, new ExpressionInverseSet<>(Collections.singleton((Identifier) expression)));
			return new EqualityDomain(lattice, func);
		}

		return forgetIdentifier(id);
	}

	@Override
	public EqualityDomain smallStepSemantics(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		return new EqualityDomain(lattice, function);
	}

	@Override
	public EqualityDomain assume(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		Satisfiability isSat = satisfies(expression, pp);
		if ( isSat == Satisfiability.SATISFIED)
			return this;
		else if (isSat == Satisfiability.NOT_SATISFIED)
			return bottom();
		else
			return this;
	}

	@Override
	public EqualityDomain forgetIdentifier(Identifier id) throws SemanticException {
		if (isTop() || isBottom())
			return this;

		EqualityDomain result = new EqualityDomain(lattice, new HashMap<>(function));
		if (result.function.containsKey(id))
			result.function.remove(id);

		return result;
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		if (expression instanceof UnaryExpression) {
			UnaryExpression unary = (UnaryExpression) expression;

			switch(unary.getOperator()) {
			case LOGICAL_NOT:
				return satisfies((ValueExpression) unary.getExpression(), pp).negate();
			default:
				return Satisfiability.UNKNOWN;
			}
		}

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;

			if (!(binary.getLeft() instanceof Identifier) || !(binary.getRight() instanceof Identifier))
				return Satisfiability.UNKNOWN;

			Identifier left = (Identifier) binary.getLeft();
			Identifier right = (Identifier) binary.getRight();

			switch(binary.getOperator()) {
			case COMPARISON_GE:
			case COMPARISON_EQ:
			case COMPARISON_LE:
				if (getState(left).contains(right) || getState(right).contains(left))
					return Satisfiability.SATISFIED;
				return Satisfiability.UNKNOWN;
			case COMPARISON_NE:
			case COMPARISON_LT:
			case COMPARISON_GT:
				if (getState(left).contains(right) || getState(right).contains(left))
					return Satisfiability.NOT_SATISFIED;
				return Satisfiability.UNKNOWN;			
			case LOGICAL_AND:
				return satisfies((ValueExpression) left, pp).and(satisfies((ValueExpression) right, pp));
			case LOGICAL_OR:
				return satisfies((ValueExpression) left, pp).or(satisfies((ValueExpression) right, pp));
			default:
				return Satisfiability.UNKNOWN;
			}
		}
		return Satisfiability.UNKNOWN;
	}

	@Override
	public DomainRepresentation representation() {
		if (isTop())
			return Lattice.TOP_REPR;

		if (isBottom())
			return Lattice.BOTTOM_REPR;

		StringBuilder builder = new StringBuilder();
		for (Entry<Identifier, ExpressionInverseSet<Identifier>> entry : function.entrySet())
			builder.append(entry.getKey()).append(" == ").append(entry.getValue().toString()).append("\n");

		return new StringRepresentation(builder.toString().trim());
	}

	@Override
	public EqualityDomain top() {
		return new EqualityDomain(lattice.top(), null);
	}

	@Override
	public EqualityDomain bottom() {
		return new EqualityDomain(lattice.bottom(), null);
	}

	@Override
	public boolean isTop() {
		return lattice.isTop() && function == null;
	}

	@Override
	public boolean isBottom() {
		return lattice.isBottom() && function == null;
	}

	@Override
	public EqualityDomain pushScope(ScopeToken token) throws SemanticException {
		return liftIdentifiers(id -> new OutOfScopeIdentifier(id, token, id.getCodeLocation()));
	}

	@Override
	public EqualityDomain popScope(ScopeToken token) throws SemanticException {
		AtomicReference<SemanticException> holder = new AtomicReference<>();

		EqualityDomain result = liftIdentifiers(id -> {
			if (id instanceof OutOfScopeIdentifier)
				try {
					return (Identifier) id.popScope(token);
				} catch (SemanticException e) {
					holder.set(e);
				}
			return null;
		});

		if (holder.get() != null)
			throw new SemanticException("Popping the scope '" + token + "' raised an error", holder.get());

		return result;
	}	
	
	private EqualityDomain liftIdentifiers(Function<Identifier, Identifier> lifter) throws SemanticException {
		if (isBottom() || isTop())
			return this;

		Map<Identifier, ExpressionInverseSet<Identifier>> function = mkNewFunction(null);
		for (Identifier id : getKeys()) {
			Identifier lifted = lifter.apply(id);
			if (lifted != null)
				function.put(lifted, getState(id));
		}

		return new EqualityDomain(lattice, function);
	}

	@Override
	protected EqualityDomain mk(ExpressionInverseSet<Identifier> lattice,
			Map<Identifier, ExpressionInverseSet<Identifier>> function) {
		return new EqualityDomain(lattice, function);
	}
}
