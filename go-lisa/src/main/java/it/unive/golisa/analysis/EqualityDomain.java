package it.unive.golisa.analysis;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.lattices.FunctionalLattice;
import it.unive.lisa.analysis.lattices.Satisfiability;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.OutOfScopeIdentifier;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonEq;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonGe;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonGt;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonLe;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonLt;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonNe;
import it.unive.lisa.symbolic.value.operator.binary.LogicalAnd;
import it.unive.lisa.symbolic.value.operator.binary.LogicalOr;
import it.unive.lisa.symbolic.value.operator.unary.LogicalNegation;
import it.unive.lisa.util.representation.StringRepresentation;
import it.unive.lisa.util.representation.StructuredRepresentation;

/**
 * The equality domain, tracking definite information about which variables are
 * equals to another one.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class EqualityDomain extends FunctionalLattice<EqualityDomain, Identifier, ExpressionInverseSet<Identifier>>
		implements ValueDomain<EqualityDomain> {

	/**
	 * Builds the domain.
	 */
	public EqualityDomain() {
		this(new ExpressionInverseSet<>(), null);
	}

	/**
	 * Builds the domain.
	 * 
	 * @param lattice  the underlying lattice
	 * @param function the function to clone
	 */
	private EqualityDomain(ExpressionInverseSet<Identifier> lattice,
			Map<Identifier, ExpressionInverseSet<Identifier>> function) {
		super(lattice, function);
	}

	@Override
	public EqualityDomain assign(Identifier id, ValueExpression expression, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
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
	public EqualityDomain smallStepSemantics(ValueExpression expression, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		return new EqualityDomain(lattice, function);
	}

	@Override
	public EqualityDomain assume(ValueExpression expression, ProgramPoint src, ProgramPoint dest, SemanticOracle oracle)
			throws SemanticException {
		Satisfiability isSat = satisfies(expression, src, oracle);
		if (isSat == Satisfiability.SATISFIED)
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
	public EqualityDomain forgetIdentifiersIf(Predicate<Identifier> test) throws SemanticException {
		if (isTop() || isBottom())
			return this;

		EqualityDomain result = new EqualityDomain(lattice, new HashMap<>(function));
		Set<Identifier> keys = result.function.keySet().stream().filter(test::test).collect(Collectors.toSet());
		keys.forEach(result.function::remove);

		return result;
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression, ProgramPoint pp, SemanticOracle oracle)
			throws SemanticException {
		if (expression instanceof UnaryExpression) {
			UnaryExpression unary = (UnaryExpression) expression;

			if (unary.getOperator() == LogicalNegation.INSTANCE)
				return satisfies((ValueExpression) unary.getExpression(), pp, oracle).negate();
			else
				return Satisfiability.UNKNOWN;
		}

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;

			if (!(binary.getLeft() instanceof Identifier) || !(binary.getRight() instanceof Identifier))
				return Satisfiability.UNKNOWN;

			Identifier left = (Identifier) binary.getLeft();
			Identifier right = (Identifier) binary.getRight();

			BinaryOperator op = binary.getOperator();
			if (op == ComparisonGe.INSTANCE || op == ComparisonEq.INSTANCE || op == ComparisonLe.INSTANCE) {
				if (getState(left).contains(right) || getState(right).contains(left))
					return Satisfiability.SATISFIED;
				return Satisfiability.UNKNOWN;
			} else if (op == ComparisonNe.INSTANCE || op == ComparisonLt.INSTANCE || op == ComparisonGt.INSTANCE) {
				if (getState(left).contains(right) || getState(right).contains(left))
					return Satisfiability.NOT_SATISFIED;
				return Satisfiability.UNKNOWN;
			} else if (op == LogicalAnd.INSTANCE)
				return satisfies((ValueExpression) left, pp, oracle)
						.and(satisfies((ValueExpression) right, pp, oracle));
			else if (op == LogicalOr.INSTANCE)
				return satisfies((ValueExpression) left, pp, oracle).or(satisfies((ValueExpression) right, pp, oracle));
			else
				return Satisfiability.UNKNOWN;
		}

		return Satisfiability.UNKNOWN;
	}

	@Override
	public StructuredRepresentation representation() {
		if (isTop())
			return Lattice.topRepresentation();

		if (isBottom())
			return Lattice.bottomRepresentation();

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

		Map<Identifier, ExpressionInverseSet<Identifier>> function = mkNewFunction(null, false);
		for (Identifier id : getKeys()) {
			Identifier lifted = lifter.apply(id);
			if (lifted != null)
				function.put(lifted, getState(id));
		}

		return new EqualityDomain(lattice, function);
	}

	@Override
	public EqualityDomain mk(ExpressionInverseSet<Identifier> lattice,
			Map<Identifier, ExpressionInverseSet<Identifier>> function) {
		return new EqualityDomain(lattice, function);
	}

	@Override
	public boolean knowsIdentifier(Identifier id) {
		return getKeys().contains(id);
	}

	@Override
	public ExpressionInverseSet<Identifier> stateOfUnknown(Identifier key) {
		return lattice.bottom();
	}

}
