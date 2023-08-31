package it.unive.golisa.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.lattices.FunctionalLattice;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.MapRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
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
import it.unive.lisa.symbolic.value.operator.binary.LogicalAnd;
import it.unive.lisa.symbolic.value.operator.binary.LogicalOr;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingAdd;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingSub;
import it.unive.lisa.symbolic.value.operator.unary.LogicalNegation;

/**
 * The strict upper bound relational abstract domain.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class StrictUpperBounds
		extends FunctionalLattice<StrictUpperBounds, Identifier, ExpressionInverseSet<Identifier>>
		implements ValueDomain<StrictUpperBounds> {

	/**
	 * Builds the string upper bounds.
	 */
	public StrictUpperBounds() {
		this(new ExpressionInverseSet<>(), null);
	}

	/**
	 * Builds the string upper bounds.
	 * 
	 * @param lattice  the underlying lattice
	 * @param function the function to clone
	 */
	private StrictUpperBounds(ExpressionInverseSet<Identifier> lattice,
			Map<Identifier, ExpressionInverseSet<Identifier>> function) {
		super(lattice, function);
	}

	@Override
	public StrictUpperBounds assign(Identifier id, ValueExpression expression, ProgramPoint pp)
			throws SemanticException {

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;

			SymbolicExpression left = binary.getLeft();
			SymbolicExpression right = binary.getRight();

			BinaryOperator op = binary.getOperator();
			if (op == NumericNonOverflowingAdd.INSTANCE) {
				if (left instanceof Identifier && !left.equals(id) && right instanceof Constant) {
					Identifier y = (Identifier) left;
					Constant cons = (Constant) right;

					if (cons.getValue() instanceof Integer) {
						Integer c = (Integer) cons.getValue();
						ExpressionInverseSet<
								Identifier> yUB = new ExpressionInverseSet<Identifier>(getState(y).elements());
						ExpressionInverseSet<
								Identifier> xUB = new ExpressionInverseSet<Identifier>(getState(id).elements());

						Map<Identifier, ExpressionInverseSet<Identifier>> func;
						if (function == null)
							func = new HashMap<>();
						else
							func = new HashMap<>(function);

						if (c < 0) {

							for (Identifier y_id : yUB)
								xUB = xUB.addExpression(y_id);
							xUB = xUB.addExpression(y);
							func.put(id, xUB);

							return new StrictUpperBounds(lattice, func).closure();
						}

						if (c > 0) {
							yUB = yUB.addExpression(id);
							func.put(y, yUB);
							StrictUpperBounds res = new StrictUpperBounds(lattice, func);
							return res.forgetIdentifier(id).closure();
						}
					}
				}
			} else if (op == NumericNonOverflowingSub.INSTANCE) {
				if (left instanceof Identifier && !left.equals(id)
						&& right instanceof Constant) {
					Identifier y = (Identifier) left;
					Constant cons = (Constant) right;

					if (cons.getValue() instanceof Integer) {
						Integer c = (Integer) cons.getValue();
						ExpressionInverseSet<
								Identifier> yUB = new ExpressionInverseSet<Identifier>(getState(y).elements());
						ExpressionInverseSet<
								Identifier> xUB = new ExpressionInverseSet<Identifier>(getState(id).elements());

						Map<Identifier, ExpressionInverseSet<Identifier>> func;
						if (function == null)
							func = new HashMap<>();
						else
							func = new HashMap<>(function);

						if (c > 0) {
							for (Identifier y_id : yUB)
								xUB = xUB.addExpression(y_id);
							xUB = xUB.addExpression(y);
							func.put(id, xUB);
							return new StrictUpperBounds(lattice, func).closure();
						}

						if (c < 0) {
							yUB = yUB.addExpression(id);
							func.put(y, yUB);
							StrictUpperBounds res = new StrictUpperBounds(lattice, func);
							return res.forgetIdentifier(id).closure();
						}
					}
				}
			}
		}

		return forgetIdentifier(id);
	}

	@Override
	public StrictUpperBounds smallStepSemantics(ValueExpression expression, ProgramPoint pp)
			throws SemanticException {
		return new StrictUpperBounds(lattice, function);
	}

	@Override
	public StrictUpperBounds assume(ValueExpression expression, ProgramPoint src, ProgramPoint dest)
			throws SemanticException {
		Satisfiability isSat = satisfies(expression, src);
		if (isSat == Satisfiability.SATISFIED)
			return this;
		else if (isSat == Satisfiability.NOT_SATISFIED)
			return bottom();
		else
			return this;
	}

	@Override
	public StrictUpperBounds forgetIdentifier(Identifier id) throws SemanticException {
		if (isTop() || isBottom())
			return this;

		StrictUpperBounds result = new StrictUpperBounds(lattice, new HashMap<>(function));
		if (result.function.containsKey(id))
			result.function.remove(id);

		return result;
	}

	@Override
	public StrictUpperBounds forgetIdentifiersIf(Predicate<Identifier> test) throws SemanticException {
		if (isTop() || isBottom())
			return this;

		StrictUpperBounds result = new StrictUpperBounds(lattice, new HashMap<>(function));
		Set<Identifier> keys = result.function.keySet().stream().filter(test::test).collect(Collectors.toSet());
		keys.forEach(result.function::remove);

		return result;
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression, ProgramPoint pp) throws SemanticException {

		if (expression instanceof UnaryExpression) {
			UnaryExpression unary = (UnaryExpression) expression;
			if (unary.getOperator() == LogicalNegation.INSTANCE)
				return satisfies((ValueExpression) unary.getExpression(), pp).negate();
			else
				return Satisfiability.UNKNOWN;
		}

		if (expression instanceof BinaryExpression) {

			if (isTop())
				return Satisfiability.UNKNOWN;
			else if (isBottom())
				return Satisfiability.BOTTOM;

			BinaryExpression binary = (BinaryExpression) expression;

			if (!(binary.getLeft() instanceof Identifier) || !(binary.getRight() instanceof Identifier))
				return Satisfiability.UNKNOWN;

			Identifier left = (Identifier) binary.getLeft();
			Identifier right = (Identifier) binary.getRight();

			BinaryOperator op = binary.getOperator();

			if (op == ComparisonEq.INSTANCE) {
				if (getState(right).contains(left) || getState(left).contains(right))
					return Satisfiability.NOT_SATISFIED;
				return Satisfiability.UNKNOWN;
			} else if (op == ComparisonGe.INSTANCE || op == ComparisonGt.INSTANCE) {
				if (getState(right).contains(left))
					return Satisfiability.SATISFIED;
				return Satisfiability.UNKNOWN;
			} else if (op == ComparisonLe.INSTANCE || op == ComparisonLt.INSTANCE) {
				if (getState(left).contains(right))
					return Satisfiability.SATISFIED;
				return Satisfiability.UNKNOWN;
			} else if (op == ComparisonEq.INSTANCE) {
				if (getState(right).contains(left) || getState(left).contains(right))
					return Satisfiability.SATISFIED;
				return Satisfiability.UNKNOWN;
			} else if (op == LogicalAnd.INSTANCE)
				return satisfies(left, pp).and(satisfies(right, pp));
			else if (op == LogicalOr.INSTANCE)
				return satisfies(left, pp).or(satisfies(right, pp));
		}

		return Satisfiability.UNKNOWN;
	}

	@Override
	public StrictUpperBounds top() {
		return new StrictUpperBounds(lattice.top(), null);
	}

	@Override
	public StrictUpperBounds bottom() {
		return new StrictUpperBounds(lattice.bottom(), null);
	}

	@Override
	public boolean isTop() {
		return lattice.isTop() && function == null;
	}

	@Override
	public boolean isBottom() {
		return lattice.isBottom() && function == null;
	}

	private StrictUpperBounds closure() {
		if (isTop() || isBottom())
			return this;

		StrictUpperBounds previous = new StrictUpperBounds(lattice, function);
		StrictUpperBounds closure = previous;

		do {
			previous = closure;
			closure = new StrictUpperBounds(lattice, function);

			for (Identifier x : getKeys())
				for (Identifier y : getKeys())
					for (Identifier z : getKeys())
						if (getState(y).contains(x) && getState(z).contains(y)) {
							Map<Identifier, ExpressionInverseSet<Identifier>> func;
							if (closure.function == null)
								func = new HashMap<>();
							else
								func = new HashMap<>(closure.function);

							func.put(z, closure.getState(z).addExpression(x));
							closure = new StrictUpperBounds(lattice, func);
						}
		} while (!previous.equals(closure));

		return previous;
	}

	@Override
	public DomainRepresentation representation() {
		return new MapRepresentation(function, StringRepresentation::new, StringRepresentation::new);
	}

	@Override
	public StrictUpperBounds pushScope(ScopeToken token) throws SemanticException {
		return liftIdentifiers(id -> new OutOfScopeIdentifier(id, token, id.getCodeLocation()));
	}

	@Override
	public StrictUpperBounds popScope(ScopeToken token) throws SemanticException {
		AtomicReference<SemanticException> holder = new AtomicReference<>();

		StrictUpperBounds result = liftIdentifiers(id -> {
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

	private StrictUpperBounds liftIdentifiers(Function<Identifier, Identifier> lifter) throws SemanticException {
		if (isBottom() || isTop())
			return this;

		Map<Identifier, ExpressionInverseSet<Identifier>> function = mkNewFunction(null, false);
		for (Identifier id : getKeys()) {
			Identifier lifted = lifter.apply(id);
			if (lifted != null)
				function.put(lifted, getState(id));
		}

		return new StrictUpperBounds(lattice, function);
	}

	@Override
	public StrictUpperBounds mk(ExpressionInverseSet<Identifier> lattice,
			Map<Identifier, ExpressionInverseSet<Identifier>> function) {
		return new StrictUpperBounds(lattice, function);
	}
}
