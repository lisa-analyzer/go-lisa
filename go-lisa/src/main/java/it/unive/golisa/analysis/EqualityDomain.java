package it.unive.golisa.analysis;

import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.lattices.ExpressionInverseSet;
import it.unive.lisa.lattices.Satisfiability;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.PushInv;
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
import it.unive.lisa.type.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The equality domain, tracking definite information about which variables are
 * equals to another one.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class EqualityDomain implements ValueDomain<EqualityLattice> {

	@Override
	public EqualityLattice assign(EqualityLattice state, Identifier id, ValueExpression expression, ProgramPoint pp,
			SemanticOracle oracle) throws SemanticException {
		if (expression instanceof Identifier) {
			Map<Identifier, ExpressionInverseSet> func;
			if (state.function == null)
				func = new HashMap<>();
			else
				func = new HashMap<>(state.function);

			func.put(id, new ExpressionInverseSet(Collections.singleton((Identifier) expression)));
			return new EqualityLattice(state.lattice, func);
		}

		return state.forgetIdentifier(id, pp);
	}

	@Override
	public EqualityLattice smallStepSemantics(EqualityLattice state, ValueExpression expression, ProgramPoint pp,
			SemanticOracle oracle) throws SemanticException {
		return new EqualityLattice(state.lattice, state.function);
	}

	@Override
	public EqualityLattice assume(EqualityLattice state, ValueExpression expression, ProgramPoint src,
			ProgramPoint dest,
			SemanticOracle oracle) throws SemanticException {
		Satisfiability isSat = satisfies(state, expression, src, oracle);
		if (isSat == Satisfiability.SATISFIED)
			return state;
		else if (isSat == Satisfiability.NOT_SATISFIED)
			return state.bottom();
		else
			return state;
	}

	@Override
	public Satisfiability satisfies(EqualityLattice state, ValueExpression expression, ProgramPoint pp,
			SemanticOracle oracle)
			throws SemanticException {
		if (expression instanceof UnaryExpression) {
			UnaryExpression unary = (UnaryExpression) expression;

			if (unary.getOperator() == LogicalNegation.INSTANCE)
				return satisfies(state, (ValueExpression) unary.getExpression(), pp, oracle).negate();
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
				if (getState(state, left).contains(right) || getState(state, right).contains(left))
					return Satisfiability.SATISFIED;
				return Satisfiability.UNKNOWN;
			} else if (op == ComparisonNe.INSTANCE || op == ComparisonLt.INSTANCE || op == ComparisonGt.INSTANCE) {
				if (getState(state, left).contains(right) || getState(state, right).contains(left))
					return Satisfiability.NOT_SATISFIED;
				return Satisfiability.UNKNOWN;
			} else if (op == LogicalAnd.INSTANCE)
				return satisfies(state, (ValueExpression) left, pp, oracle)
						.and(satisfies(state, (ValueExpression) right, pp, oracle));
			else if (op == LogicalOr.INSTANCE)
				return satisfies(state, (ValueExpression) left, pp, oracle)
						.or(satisfies(state, (ValueExpression) right, pp, oracle));
			else
				return Satisfiability.UNKNOWN;
		}

		return Satisfiability.UNKNOWN;
	}

	private Set<SymbolicExpression> getState(EqualityLattice state, Identifier id) {
		return state.getMap().get(id).elements;
	}

	@Override
	public EqualityLattice makeLattice() {
		return new EqualityLattice();
	}

	@Override
	public boolean canProcess(ValueExpression e, ProgramPoint pp, SemanticOracle oracle) {
		if (e instanceof PushInv)
			// the type approximation of a pushinv is bottom, so the below check
			// will always fail regardless of the kind of value we are tracking
			return e.getStaticType().isValueType();

		Set<Type> rts = null;
		try {
			rts = oracle.getRuntimeTypesOf(e, pp);
		} catch (SemanticException exc) {
			return false;
		}

		if (rts == null || rts.isEmpty())
			// if we have no runtime types, either the type domain has no type
			// information for the given expression (thus it can be anything,
			// also something that we can track) or the computation returned
			// bottom (and the whole state is likely going to go to bottom
			// anyway).
			return true;

		return rts.stream().anyMatch(Type::isValueType);
	}

}
