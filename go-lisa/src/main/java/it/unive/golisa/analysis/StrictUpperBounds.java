package it.unive.golisa.analysis;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;

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

public class StrictUpperBounds extends FunctionalLattice<StrictUpperBounds, Identifier, ExpressionInverseSet<Identifier>> implements ValueDomain<StrictUpperBounds>{

	public StrictUpperBounds() {
		this(new ExpressionInverseSet<>(), null);
	}

	private StrictUpperBounds(ExpressionInverseSet<Identifier> lattice, Map<Identifier, ExpressionInverseSet<Identifier>> function) {
		super(lattice, function);
	}

	@Override
	public StrictUpperBounds assign(Identifier id, ValueExpression expression, ProgramPoint pp)
			throws SemanticException {

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;

			SymbolicExpression left = binary.getLeft();
			SymbolicExpression right = binary.getRight();

			switch (binary.getOperator()) {
			case NUMERIC_ADD:
				if (left instanceof Identifier && !left.equals(id) && right instanceof Constant) {
					Identifier y = (Identifier) left;
					Constant cons = (Constant) right;

					if (cons.getValue() instanceof Integer) {
						Integer c = (Integer) cons.getValue();
						ExpressionInverseSet<Identifier> yUB = new ExpressionInverseSet<Identifier>(getState(y).elements());
						ExpressionInverseSet<Identifier> xUB = new ExpressionInverseSet<Identifier>(getState(id).elements());

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
				break;
			case NUMERIC_SUB:
				if (left instanceof Identifier && !left.equals(id)
						&& right instanceof Constant) {
					Identifier y = (Identifier) left;
					Constant cons = (Constant) right;

					if (cons.getValue() instanceof Integer) {
						Integer c = (Integer) cons.getValue();
						ExpressionInverseSet<Identifier> yUB = new ExpressionInverseSet<Identifier>(getState(y).elements());
						ExpressionInverseSet<Identifier> xUB = new ExpressionInverseSet<Identifier>(getState(id).elements());

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
				break;
			default:
				break;
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
	public StrictUpperBounds assume(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		Satisfiability isSat = satisfies(expression, pp);
		if ( isSat == Satisfiability.SATISFIED)
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

			if (isTop())
				return Satisfiability.UNKNOWN;
			else if (isBottom())
				return Satisfiability.BOTTOM;

			BinaryExpression binary = (BinaryExpression) expression;

			if (!(binary.getLeft() instanceof Identifier) || !(binary.getRight() instanceof Identifier))
				return Satisfiability.UNKNOWN;

			Identifier left = (Identifier) binary.getLeft();
			Identifier right = (Identifier) binary.getRight();

			switch(binary.getOperator()) {
			case COMPARISON_EQ:
				if (getState(right).contains(left) || getState(left).contains(right))
					return Satisfiability.NOT_SATISFIED;
				return Satisfiability.UNKNOWN;	
			case COMPARISON_GE:
			case COMPARISON_GT: // x > y or x >= y
				if (getState(right).contains(left))
					return Satisfiability.SATISFIED;
				return Satisfiability.UNKNOWN;
			case COMPARISON_LE:
			case COMPARISON_LT: // x < y or x <= y
				if (getState(left).contains(right))
					return Satisfiability.SATISFIED;
				return Satisfiability.UNKNOWN;
			case COMPARISON_NE:
				if (getState(right).contains(left) || getState(left).contains(right))
					return Satisfiability.SATISFIED;
				return Satisfiability.UNKNOWN;	
			case LOGICAL_AND:
				return satisfies(left, pp).and(satisfies(right, pp));
			case LOGICAL_OR:
				return satisfies(left, pp).or(satisfies(right, pp));
			default:
				break;
			}
		}

		return Satisfiability.UNKNOWN;
	}

//	@Override
//	public String representation() {
//		if (isTop())
//			return "TOP";
//
//		if (isBottom())
//			return "BOTTOM";
//
//		StringBuilder builder = new StringBuilder();
//		for (Entry<Identifier, ExpressionInverseSet<Identifier>> entry : function.entrySet())
//			builder.append(entry.getKey()).append(" < ").append(entry.getValue().toString()).append("\n");
//
//		return builder.toString().trim();
//	}

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

		Map<Identifier, ExpressionInverseSet<Identifier>> function = mkNewFunction(null);
		for (Identifier id : getKeys()) {
			Identifier lifted = lifter.apply(id);
			if (lifted != null)
				function.put(lifted, getState(id));
		}

		return new StrictUpperBounds(lattice, function);
	}

	@Override
	protected StrictUpperBounds mk(ExpressionInverseSet<Identifier> lattice,
			Map<Identifier, ExpressionInverseSet<Identifier>> function) {
		return new StrictUpperBounds(lattice, function);
	}
}
