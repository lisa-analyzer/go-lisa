package it.unive.golisa.analysis.rsubs;

import it.unive.golisa.analysis.ExpressionInverseSet;
import it.unive.golisa.analysis.StringConstantPropagation;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.analysis.Lattice;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.lattices.FunctionalLattice;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.program.SyntheticLocation;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.OutOfScopeIdentifier;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonEq;
import it.unive.lisa.symbolic.value.operator.binary.LogicalAnd;
import it.unive.lisa.symbolic.value.operator.binary.LogicalOr;
import it.unive.lisa.symbolic.value.operator.binary.StringConcat;
import it.unive.lisa.symbolic.value.operator.binary.StringContains;
import it.unive.lisa.symbolic.value.operator.binary.StringEndsWith;
import it.unive.lisa.symbolic.value.operator.binary.StringEquals;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import org.apache.commons.lang3.ArrayUtils;

/**
 * The relational string abstract domain, tracking definite information about
 * which string expressions are substring of a string variable.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class RelationalSubstringDomain
extends FunctionalLattice<RelationalSubstringDomain, Identifier, ExpressionInverseSet<ValueExpression>>
implements ValueDomain<RelationalSubstringDomain> {

	/**
	 * Builds the top abstract value.
	 */
	public RelationalSubstringDomain() {
		this(new ExpressionInverseSet<ValueExpression>(), null);
	}

	private RelationalSubstringDomain(ExpressionInverseSet<ValueExpression> lattice,
			Map<Identifier, ExpressionInverseSet<ValueExpression>> function) {
		super(lattice, function);
	}

	@Override
	public RelationalSubstringDomain top() {
		return new RelationalSubstringDomain(lattice.top(), null);
	}

	@Override
	public RelationalSubstringDomain bottom() {
		return new RelationalSubstringDomain(lattice.bottom(), null);
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
	public RelationalSubstringDomain lubAux(RelationalSubstringDomain other) throws SemanticException {
		Map<Identifier, ExpressionInverseSet<ValueExpression>> func = new HashMap<>();

		for (Identifier x : getKeys())
			if (other.function.containsKey(x))
				func.put(x, getState(x).lub(other.getState(x)));

		return new RelationalSubstringDomain(lattice, func);
	}

	@Override
	public RelationalSubstringDomain glb(RelationalSubstringDomain other) throws SemanticException {
		return super.lub(other);
	}

	@Override
	public RelationalSubstringDomain assign(Identifier id, ValueExpression expression, ProgramPoint pp)
			throws SemanticException {
		Map<Identifier, ExpressionInverseSet<ValueExpression>> func;
		if (function == null)
			func = new HashMap<>();
		else
			func = new HashMap<>(function);

		if (expression instanceof PushAny)
			return new RelationalSubstringDomain(lattice, func);

		// Remove phase
		for (Identifier x : func.keySet())
			for (ValueExpression xRel : func.get(x))
				if (appersIn(xRel, id))
					func.put(x, func.get(x).removeExpression(id));

		if (!appearsAtTopLevel(expression, id))
			func.remove(id);

		// Add phase
		func.put(id, func.get(id) == null ? getRelations(expression) : func.get(id).glb(getRelations(expression)));

		// Inter-asg phase
		for (Identifier y : func.keySet())
			if (!y.equals(id) && func.get(y).contains(func.get(id)))
				func.put(y, func.get(y).addExpression(id));

		// Improvement of add phase
		for (ValueExpression idRel : func.get(id))
			if (id instanceof Identifier)
				func.put(id, func.get(id) == null ? func.get(idRel) : func.get(id).glb(func.get(idRel)));

		// Closure phase
		return new RelationalSubstringDomain(lattice, func).closure();
	}

	private RelationalSubstringDomain closure() {
		if (isTop() || isBottom())
			return new RelationalSubstringDomain(lattice, function);

		Map<Identifier, ExpressionInverseSet<ValueExpression>> clos = new HashMap<>(function);

		for (Identifier x : clos.keySet())
			for (Identifier y : clos.keySet())
				for (Identifier z : clos.keySet())
					if (clos.get(y).contains(x) && clos.get(z).contains(y))
						clos.put(z, clos.get(z).addExpression(x));
		return new RelationalSubstringDomain(lattice, clos);
	}

	@Override
	public RelationalSubstringDomain smallStepSemantics(ValueExpression expression, ProgramPoint pp)
			throws SemanticException {
		return new RelationalSubstringDomain(lattice, function);
	}

	@Override
	public RelationalSubstringDomain assume(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		// rsubs can assume contains, equals, and & or expressions (all binary
		// expressions)

		if (isBottom())
			return bottom();

		HashMap<Identifier, ExpressionInverseSet<ValueExpression>> func;
		if (function == null)
			func = new HashMap<>();
		else
			func = new HashMap<>(function);

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;
			RelationalSubstringDomain leftState = smallStepSemantics((ValueExpression) binary.getLeft(), pp);
			RelationalSubstringDomain rightState = smallStepSemantics((ValueExpression) binary.getRight(), pp);

			it.unive.lisa.symbolic.value.operator.binary.BinaryOperator op = binary.getOperator();
			if (op == ComparisonEq.INSTANCE) {
				ValueExpression left = (ValueExpression) binary.getLeft();
				ValueExpression right = (ValueExpression) binary.getRight();

				if (!left.getDynamicType().isStringType() || !right.getDynamicType().isStringType())
					return new RelationalSubstringDomain(lattice, function);

				if (left instanceof Identifier && right instanceof Identifier) {
					Identifier x = (Identifier) left;
					Identifier y = (Identifier) right;

					ExpressionInverseSet<ValueExpression> relsForX = getRelations(y);
					ExpressionInverseSet<ValueExpression> relsForY = getRelations(x);

					func.put(x, func.get(x) == null ? relsForX : func.get(x).glb(relsForX));
					func.put(y, func.get(y) == null ? relsForY : func.get(y).glb(relsForY));
					return new RelationalSubstringDomain(lattice, func);
				}

				if (left instanceof Identifier) {
					Identifier x = (Identifier) left;
					ExpressionInverseSet<ValueExpression> rels = getRelations(right);
					func.put(x, func.get(x) == null ? rels : func.get(x).glb(rels));
					return new RelationalSubstringDomain(lattice, func);
				}

				if (right instanceof Identifier) {
					Identifier x = (Identifier) right;
					ExpressionInverseSet<ValueExpression> rels = getRelations(left);
					func.put(x, func.get(x) == null ? rels : func.get(x).glb(rels));
					return new RelationalSubstringDomain(lattice, func);
				}
			} else if (op == LogicalAnd.INSTANCE)
				return leftState.lub(rightState);
			else if (op == LogicalOr.INSTANCE)
				return leftState.glb(rightState);
			else if (op == it.unive.lisa.symbolic.value.operator.binary.StringStartsWith.INSTANCE
					|| op == StringEndsWith.INSTANCE || op == StringContains.INSTANCE) {
				if (binary.getLeft() instanceof Identifier) {
					Identifier x = (Identifier) binary.getLeft();
					ExpressionInverseSet<ValueExpression> rels = getRelations((ValueExpression) binary.getRight());
					func.put(x, func.get(x) == null ? rels : func.get(x).glb(rels));
					return new RelationalSubstringDomain(lattice, func);
				}
			} else if (op == StringEquals.INSTANCE) {
				if (binary.getLeft() instanceof Identifier) {
					Identifier x = (Identifier) binary.getLeft();
					ExpressionInverseSet<ValueExpression> rels = getRelations((ValueExpression) binary.getRight());
					func.put(x, func.get(x) == null ? rels : func.get(x).glb(rels));
					return new RelationalSubstringDomain(lattice, func);
				}

				if (binary.getRight() instanceof Identifier) {
					Identifier x = (Identifier) binary.getRight();
					ExpressionInverseSet<ValueExpression> rels = getRelations((ValueExpression) binary.getLeft());
					func.put(x, func.get(x) == null ? rels : func.get(x).glb(rels));
					return new RelationalSubstringDomain(lattice, func);
				}
			}
		}

		return new RelationalSubstringDomain(lattice, function);

	}

	@Override
	public RelationalSubstringDomain forgetIdentifier(Identifier id) throws SemanticException {
		if (function == null)
			return new RelationalSubstringDomain(lattice, null);

		RelationalSubstringDomain result = new RelationalSubstringDomain(lattice, new HashMap<>(function));
		if (result.function.containsKey(id))
			result.function.remove(id);

		return result;
	}

	@Override
	public RelationalSubstringDomain forgetIdentifiersIf(Predicate<Identifier> test) throws SemanticException {
		if (isTop() || isBottom())
			return this;

		RelationalSubstringDomain result = new RelationalSubstringDomain(lattice, new HashMap<>(function));
		Set<Identifier> keys = result.function.keySet().stream().filter(test::test).collect(Collectors.toSet());
		keys.forEach(result.function::remove);

		return result;
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		// rsubs can satisfy contains, equals, and & or expressions (all binary
		// expressions)

		if (isTop())
			return Satisfiability.UNKNOWN;
		if (isBottom())
			return Satisfiability.BOTTOM;

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;

			BinaryOperator op = binary.getOperator();
			if (op == LogicalAnd.INSTANCE)
				return satisfies((ValueExpression) binary.getLeft(), pp)
						.and(satisfies((ValueExpression) binary.getRight(), pp));
			else if (op == LogicalOr.INSTANCE)

				return satisfies((ValueExpression) binary.getLeft(), pp)
						.or(satisfies((ValueExpression) binary.getRight(), pp));
			else if (op == StringContains.INSTANCE) {
				if (binary.getLeft() instanceof Identifier) {
					Identifier x = (Identifier) binary.getLeft();
					return getState(x).contains((ValueExpression) binary.getRight()) ? Satisfiability.SATISFIED
							: Satisfiability.NOT_SATISFIED;
				}
			} else if (op == StringEquals.INSTANCE) {
				if (binary.getLeft() instanceof Identifier && binary.getRight() instanceof Identifier) {
					Identifier x = (Identifier) binary.getLeft();
					Identifier y = (Identifier) binary.getRight();
					return getState(x).contains(y) && getState(y).contains(x) ? Satisfiability.SATISFIED
							: Satisfiability.NOT_SATISFIED;
				}
			}
		}

		return Satisfiability.UNKNOWN;
	}

	@Override
	public DomainRepresentation representation() {
		if (isTop())
			return Lattice.topRepresentation();

		if (isBottom())
			return Lattice.bottomRepresentation();

		StringBuilder builder = new StringBuilder();
		for (Entry<Identifier, ExpressionInverseSet<ValueExpression>> entry : function.entrySet())
			builder.append(entry.getKey()).append(": ").append(entry.getValue().toString()).append("\n");

		return new StringRepresentation(builder.toString().trim());
	}

	private ValueExpression[] flat(ValueExpression expression) {

		if (expression instanceof BinaryExpression) {
			BinaryExpression binary = (BinaryExpression) expression;

			if (binary.getOperator() == StringConcat.INSTANCE)
				return ArrayUtils.addAll(flat((ValueExpression) binary.getLeft()),
						flat((ValueExpression) binary.getRight()));

		}
		return new ValueExpression[] { expression };
	}

	private ExpressionInverseSet<ValueExpression> getRelations(ValueExpression expression) {
		ValueExpression[] exps = flat(expression);
		Set<ValueExpression> result = new HashSet<ValueExpression>();

		for (int i = 0; i < exps.length; i++) {
			ValueExpression partial = exps[i];

			if (exps[i] instanceof Constant && ((Constant) exps[i]).getValue() instanceof String)
				result.addAll(getAllSubstrings((String) ((Constant) exps[i]).getValue()));
			else
				result.add(exps[i]);

			for (int j = i + 1; j < exps.length; j++) {

				if (exps[j] instanceof Constant && ((Constant) exps[j]).getValue() instanceof String)
					result.addAll(getAllSubstrings((String) ((Constant) exps[j]).getValue()));
				else
					result.add(exps[j]);
				partial = new BinaryExpression(GoStringType.INSTANCE, partial, exps[j],
						StringConcat.INSTANCE, exps[j].getCodeLocation());
				result.add(partial);
			}
		}

		return new ExpressionInverseSet<ValueExpression>(result);
	}

	private Set<Constant> getAllSubstrings(String str) {
		HashSet<Constant> res = new HashSet<Constant>();

		for (int i = 0; i < str.length(); i++)
			for (int j = i + 1; j <= str.length(); j++)
				res.add(new Constant(GoStringType.INSTANCE, str.substring(i, j), SyntheticLocation.INSTANCE));

		return res;
	}

	private boolean appersIn(ValueExpression expression, ValueExpression search) {
		HashSet<ValueExpression> singleton = new HashSet<>();
		singleton.add(search);
		return getRelations(expression).contains(new ExpressionInverseSet<ValueExpression>(singleton));
	}

	private boolean appearsAtTopLevel(ValueExpression expression, Identifier id) {
		if (expression.equals(id))
			return true;

		if (expression instanceof BinaryExpression
				&& ((BinaryExpression) expression).getOperator() == StringConcat.INSTANCE) {
			BinaryExpression binary = (BinaryExpression) expression;
			return appearsAtTopLevel((ValueExpression) binary.getLeft(), id)
					|| appearsAtTopLevel((ValueExpression) binary.getRight(), id);
		}

		return false;
	}

	@Override
	public RelationalSubstringDomain pushScope(ScopeToken token) throws SemanticException {
		return liftIdentifiers(id -> new OutOfScopeIdentifier(id, token, id.getCodeLocation()));
	}

	@Override
	public RelationalSubstringDomain popScope(ScopeToken token) throws SemanticException {
		AtomicReference<SemanticException> holder = new AtomicReference<>();

		RelationalSubstringDomain result = liftIdentifiers(id -> {
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

	private RelationalSubstringDomain liftIdentifiers(Function<Identifier, Identifier> lifter)
			throws SemanticException {
		if (isBottom() || isTop())
			return this;

		Map<Identifier, ExpressionInverseSet<ValueExpression>> function = mkNewFunction(null, false);
		for (Identifier id : getKeys()) {
			Identifier lifted = lifter.apply(id);
			if (lifted != null)
				function.put(lifted, getState(id));
		}

		return new RelationalSubstringDomain(lattice, function);
	}

	/**
	 * Yields a new instance of this domain where the costant information given
	 * in {@code cs} is propagated.
	 * 
	 * @param cs the constant propagation instance
	 * 
	 * @return a new instance of this domain where the costant information given
	 *             in {@code cs} is propagated
	 * 
	 * @throws SemanticException if something wrong happens during the
	 *                               propagation
	 */
	public RelationalSubstringDomain propagateConstants(ValueEnvironment<StringConstantPropagation> cs)
			throws SemanticException {

		if (isTop() || isBottom() || cs.isTop() || cs.isBottom())
			return this;

		RelationalSubstringDomain result = new RelationalSubstringDomain(lattice, function);

		for (Identifier id : this.getKeys()) {
			Set<String> constants = new HashSet<>();
			ExpressionInverseSet<ValueExpression> previousRelations = result.getState(id);

			if (previousRelations.isTop() || previousRelations.isBottom())
				continue;

			for (ValueExpression exp : getState(id).elements()) {
				String string = exp.accept(new ResolverVisitor(), cs);
				if (string != null) {
					constants.add(string);
					for (String str : constants)
						previousRelations = previousRelations
						.addExpression(new Constant(exp.getDynamicType(), str, exp.getCodeLocation()));
				}
			}

			if (!constants.isEmpty())
				for (Identifier idCs : cs.getKeys())
					if (constants.contains(cs.getState(idCs).getString()) && !idCs.getName().equals(id.getName())) {
						previousRelations = previousRelations.addExpression(idCs);
						result = result.putState(id, previousRelations);
					}
		}

		return result;
	}

	@Override
	public RelationalSubstringDomain mk(ExpressionInverseSet<ValueExpression> lattice,
			Map<Identifier, ExpressionInverseSet<ValueExpression>> function) {
		return new RelationalSubstringDomain(lattice, function);
	}
}
