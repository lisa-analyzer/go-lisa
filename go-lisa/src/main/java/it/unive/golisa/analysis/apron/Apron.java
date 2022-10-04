package it.unive.golisa.analysis.apron;

import java.math.BigInteger;
import java.util.Arrays;

import javax.swing.Box;

import apron.Abstract1;
import apron.ApronException;
import apron.Coeff;
import apron.Manager;
import apron.MpfrScalar;
import apron.MpqScalar;
import apron.Octagon;
import apron.Polka;
import apron.PolkaEq;
import apron.PplGrid;
import apron.PplPoly;
import apron.StringVar;
import apron.Tcons1;
import apron.Texpr1BinNode;
import apron.Texpr1CstNode;
import apron.Texpr1Intern;
import apron.Texpr1Node;
import apron.Texpr1VarNode;
import apron.Var;
import gmp.Mpfr;
import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;

public class Apron implements ValueDomain<Apron> {

	private static Manager manager;

	final Abstract1 state;

	public enum ApronDomain {
		/**
		 * Intervals
		 */
		Box, 

		/**
		 * Octagons
		 */
		Octagon, 

		/**
		 * Convex polyhedra
		 */
		Polka, 

		/**
		 * Linear equalities
		 */
		PolkaEq, 

		/**
		 * Reduced product of the Polka convex polyhedra and PplGrid the linear congruence equalities domains
		 * Compile Apron with the specific flag for PPL set to 1 in order to use such domain.
		 */
		PolkaGrid, 

		/**
		 * Parma Polyhedra Library linear congruence equalities domain
		 * Compile Apron with the specific flag for PPL set to 1 in order to use such domain.
		 */
		PplGrid, 

		/**
		 * The Parma Polyhedra libraryconvex polyhedra domain
		 * Compile Apron with the specific flag for PPL set to 1 in order to use such domain.
		 */
		PplPoly;
	}

	public static void setManager(ApronDomain numericalDomain) {
		switch(numericalDomain) {
		case Box: manager= new Box(); break;
		case Octagon: manager=new Octagon(); break;
		case Polka: manager=new Polka(false); break;
		case PolkaEq: manager=new PolkaEq(); break;
		case PplGrid: manager=new PplGrid(); break;
		case PplPoly: manager=new PplPoly(false); break;
		default: throw new UnsupportedOperationException("Numerical domain "+numericalDomain+" unknown in Apron");
		}
	}

	public Apron() {
		try {
			String[] vars = {"<ret>"}; // Variable needed to represent the returned value 
			state = new Abstract1(manager, new apron.Environment(new String[0], vars));
		}
		catch(ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		}
	}

	Apron(Abstract1 state) {
		this.state = state;
	}

	@Override
	public Apron assign(Identifier id, ValueExpression expression, ProgramPoint pp) throws SemanticException {

		try {
			Environment env = state.getEnvironment();
			Var variable = toApronVar(id);
			Abstract1 newState;
			if (!containsIdentifier(id)) {
				Var[] vars = {variable};
				env = env.add(new Var[0], vars);
				newState = state.changeEnvironmentCopy(manager, env, false);
			} else
				newState = state;

			Texpr1Node apronExpression = toApronExpression(expression);

			// we are not able to translate expression
			// hence, we treat it as "don't know"
			if (apronExpression == null)
				return forgetAbstractionOf(newState, id, pp);

			Var[] vars = apronExpression.getVars();

			for (int i = 0; i < vars.length; i++)
				if (!newState.getEnvironment().hasVar(vars[i])) {
					Var[] vars1 = {vars[i]};
					env = newState.getEnvironment().add(new Var[0], vars1);
					newState = newState.changeEnvironmentCopy(manager, env, false);
				}

			return new Apron(newState.assignCopy(manager, variable, new Texpr1Intern(newState.getEnvironment(), apronExpression), null));

		} catch (ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		}
	}


	private Apron forgetAbstractionOf(Abstract1 newState, Identifier id, ProgramPoint pp) throws SemanticException {
		Apron result = new Apron(newState);
		result = result.forgetIdentifier(id);
		Constant zero = new Constant(GoIntType.INSTANCE, 0, pp.getLocation());
		Apron ge = result.assume(new BinaryExpression(Caches.types().mkSingletonSet(GoBoolType.INSTANCE), id, zero, BinaryOperator.COMPARISON_GE, pp.getLocation()), pp);
		Apron le = result.assume(new BinaryExpression(Caches.types().mkSingletonSet(GoBoolType.INSTANCE), id, zero, BinaryOperator.COMPARISON_LE, pp.getLocation()), pp);
		return ge.lub(le);
	}

	private Texpr1Node toApronExpression(SymbolicExpression exp) throws ApronException {
		if (exp instanceof Identifier)  
			return new Texpr1VarNode(((Identifier) exp).getName());

		if (exp instanceof Constant) {
			Constant c = (Constant) exp;
			Coeff coeff;

			if (c.getValue() instanceof Integer) 
				coeff = new MpqScalar((int) c.getValue());
			else if (c.getValue() instanceof Float)
				coeff = new MpfrScalar((double) c.getValue(), Mpfr.getDefaultPrec());
			else if (c.getValue() instanceof Long)
				coeff = new MpfrScalar((long) c.getValue(), Mpfr.getDefaultPrec());
			else if (c.getValue() instanceof BigInteger)
				coeff = new MpfrScalar(new Mpfr(((BigInteger) c.getValue()), Mpfr.RNDN));
			else 
				return null;


			return new Texpr1CstNode(coeff);
		}

		if (exp instanceof UnaryExpression) {
			UnaryExpression un = (UnaryExpression) exp;

			switch(un.getOperator()) {
			case LOGICAL_NOT:
				break;
			case NUMERIC_NEG:
				break;
			case STRING_LENGTH:
				break;
			case TYPEOF:
				break;
			default:
				break;

			}
		}

		if (exp instanceof BinaryExpression) {			
			BinaryExpression bin = (BinaryExpression) exp;

			switch(bin.getOperator()) {
			case TYPE_CAST:
			case TYPE_CONV:
				if (!exp.getTypes().isEmpty())
					return toApronExpression(bin.getLeft());
			default:
				Texpr1Node rewrittenLeft =  toApronExpression(bin.getLeft());
				if (rewrittenLeft == null)
					return null;

				Texpr1Node rewrittenRight = toApronExpression(bin.getRight());
				if (rewrittenRight == null)
					return null;

				if (bin.getOperator() == BinaryOperator.COMPARISON_LT)
					return new Texpr1BinNode(Tcons1.SUP, rewrittenRight, rewrittenLeft);			

				if (bin.getOperator() == BinaryOperator.COMPARISON_LE)
					return new Texpr1BinNode(Tcons1.SUPEQ, rewrittenRight, rewrittenLeft);			

				if (!canBeConvertedToApronOperator(bin.getOperator()))
					// we are not able to translate the expression
					return null;

				return new Texpr1BinNode(toApronOperator(bin.getOperator()), rewrittenLeft, rewrittenRight);			
			}
		}

		// we are not able to translate the expression
		return null;
	}


	private boolean canBeConvertedToApronOperator(BinaryOperator op) {
		switch(op) {
		case STRING_CONCAT:
		case NUMERIC_NON_OVERFLOWING_ADD:
		case NUMERIC_NON_OVERFLOWING_MUL: 
		case NUMERIC_NON_OVERFLOWING_SUB:
		case NUMERIC_NON_OVERFLOWING_DIV: 
		case NUMERIC_NON_OVERFLOWING_MOD: 
		case COMPARISON_EQ: 
		case COMPARISON_NE: 
		case COMPARISON_GE: 
		case COMPARISON_GT: 
			return true;
		default:
			return false;
		}
	}
	
	private int toApronOperator(BinaryOperator op) {
		switch(op) {
		case STRING_CONCAT:
		case NUMERIC_NON_OVERFLOWING_ADD: return Texpr1BinNode.OP_ADD;
		case NUMERIC_NON_OVERFLOWING_MUL: return Texpr1BinNode.OP_MUL;
		case NUMERIC_NON_OVERFLOWING_SUB: return Texpr1BinNode.OP_SUB;
		case NUMERIC_NON_OVERFLOWING_DIV: return Texpr1BinNode.OP_DIV;
		case NUMERIC_NON_OVERFLOWING_MOD: return Texpr1BinNode.OP_MOD;

		case COMPARISON_EQ: return Tcons1.EQ;
		case COMPARISON_NE: return Tcons1.DISEQ;
		case COMPARISON_GE: return Tcons1.SUPEQ;
		case COMPARISON_GT: return Tcons1.SUP;

		default: throw new UnsupportedOperationException("Operator "+op+" not yet supported by Apron interface");
		}
	}

	@Override
	public Apron smallStepSemantics(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		// the small-step semantics does not alter the state, but it should
		// add to the environment the identifiers produced by expression in order to be 
		// tracked by Apron

		if (expression instanceof Identifier) {
			Identifier id = (Identifier) expression;
			Environment env = state.getEnvironment();
			Var variable = toApronVar(id);
			if (!containsIdentifier(id)) {
				Var[] vars = {variable};
				env = env.add(new Var[0], vars);
				try {
					return new Apron(state.changeEnvironmentCopy(manager, env, state.isBottom(manager)));
				} catch (ApronException e) {
					throw new UnsupportedOperationException("Apron library crashed", e);
				}
			} else
				return new Apron(state);
		}

		if (expression instanceof UnaryExpression) {
			UnaryExpression un = (UnaryExpression) expression;
			return smallStepSemantics((ValueExpression) un.getExpression(), pp);
		}

		if (expression instanceof BinaryExpression) {
			BinaryExpression bin = (BinaryExpression) expression;
			Apron left = smallStepSemantics((ValueExpression) bin.getLeft(), pp);
			Apron right = smallStepSemantics((ValueExpression) bin.getLeft(), pp);
			return left.lub(right);
		}

		return new Apron(state);
	}

	@Override
	public Apron assume(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		try {
			if (state.isBottom(manager))
				return this;
		} catch (ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		}

		if (expression instanceof UnaryExpression) {
			UnaryExpression un = (UnaryExpression) expression;

			switch (un.getOperator()) {
			case LOGICAL_NOT:
				ValueExpression inner = (ValueExpression) un.getExpression();
				if (inner instanceof UnaryExpression && ((UnaryExpression) inner).getOperator() == UnaryOperator.LOGICAL_NOT)
					return assume(((ValueExpression) ((UnaryExpression) inner).getExpression()).removeNegations(), pp);


				// It is possible that the expression cannot be rewritten (e.g.,
				// !true, !var_id) hence we recursively call assume iff something changed
				ValueExpression rewritten = un.removeNegations();
				if (rewritten != un)
					return assume(rewritten, pp);
				else
					return this;
			default:
				return this;
			}
		}

		if (expression instanceof BinaryExpression) {
			BinaryExpression bin = (BinaryExpression) expression;
			Apron left, right;

			switch(bin.getOperator()) {
			case COMPARISON_EQ:
			case COMPARISON_GE:
			case COMPARISON_GT:
			case COMPARISON_LE:
			case COMPARISON_LT:
			case COMPARISON_NE:
				try {
					return new Apron(state.meetCopy(manager, toApronComparison(bin)));
				} catch (ApronException e) {
					throw new UnsupportedOperationException("Apron library crashed", e);
				} catch (UnsupportedOperationException e) {
					// if a sub-expression of expression cannot be 
					// translated by Apron, then top is returned.
					return this;
				}
			case LOGICAL_AND:
				left = assume((ValueExpression) bin.getLeft(), pp); 
				right = assume((ValueExpression) bin.getRight(), pp);
				try {
					return new Apron(left.state.meetCopy(manager, right.state));
				} catch (ApronException e) {
					throw new UnsupportedOperationException("Apron library crashed", e);
				} catch (UnsupportedOperationException e) {
					// if a sub-expression of expression cannot be 
					// translated by Apron, then top is returned.
					return this;
				}
			case LOGICAL_OR:
				left = assume((ValueExpression) bin.getLeft(), pp); 
				right = assume((ValueExpression) bin.getRight(), pp);
				try {
					return new Apron(left.state.joinCopy(manager, right.state));
				} catch (ApronException e) {
					throw new UnsupportedOperationException("Apron library crashed", e);
				} catch (UnsupportedOperationException e) {
					// if a sub-expression of expression cannot be 
					// translated by Apron, then top is returned.
					return this;
				}
			default:
				return this;
			}
		}	

		return this;
	}

	private Tcons1 toApronComparison(BinaryExpression exp) throws ApronException {
		// Apron supports only "exp <comparison> 0", so we need to move everything on the left node 
		SymbolicExpression combinedExpr = new BinaryExpression(exp.getTypes(), exp.getLeft(), exp.getRight(), BinaryOperator.NUMERIC_NON_OVERFLOWING_SUB, exp.getCodeLocation()); 

		switch(exp.getOperator()) {
		case COMPARISON_GT:
		case COMPARISON_GE:
		case COMPARISON_NE:
		case COMPARISON_EQ:
			Texpr1Node apronExpression = toApronExpression(combinedExpr);
			if (apronExpression != null)
				return new Tcons1(state.getEnvironment(), toApronOperator(exp.getOperator()), apronExpression);
			else
				throw new UnsupportedOperationException("Comparison operator "+exp.getOperator()+" not yet supported"); 		
		case COMPARISON_LE:
			return toApronComparison(new BinaryExpression(exp.getTypes(), exp.getRight(), exp.getLeft(), BinaryOperator.COMPARISON_GE, exp.getCodeLocation()));
		case COMPARISON_LT:
			return toApronComparison(new BinaryExpression(exp.getTypes(), exp.getRight(), exp.getLeft(), BinaryOperator.COMPARISON_GT, exp.getCodeLocation()));
		default:
			throw new UnsupportedOperationException("Comparison operator "+exp.getOperator()+" not yet supported"); 		
		}		
	}

	@Override
	public Apron forgetIdentifier(Identifier id) throws SemanticException {
		if (!containsIdentifier(id))
			return this;

		try {
			return new Apron(state.forgetCopy(manager, toApronVar(id), false));
		} catch (ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		}
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		try {

			if (state.getEnvironment().equals(new apron.Environment()))
				return Satisfiability.BOTTOM;

			if (expression instanceof UnaryExpression) {
				UnaryExpression un = (UnaryExpression) expression;

				switch(un.getOperator()) {
				case LOGICAL_NOT:
					Satisfiability isSAT = satisfies((ValueExpression) un.getExpression(), pp);
					if (isSAT == Satisfiability.SATISFIED)
						return Satisfiability.NOT_SATISFIED;
					else if (isSAT == Satisfiability.NOT_SATISFIED)
						return Satisfiability.SATISFIED;
					else
						return Satisfiability.UNKNOWN;
				default:
					return Satisfiability.UNKNOWN;
				}
			}

			if (expression instanceof BinaryExpression) {
				BinaryExpression bin = (BinaryExpression) expression;
				BinaryExpression neg;

				// FIXME: it seems there's a bug with manager.wasExact
				switch(bin.getOperator()) {
				case COMPARISON_EQ:
					if (state.satisfy(manager, toApronComparison(bin)))
						return Satisfiability.SATISFIED;
					else {
						neg = new BinaryExpression(bin.getTypes(), bin.getLeft(), bin.getRight(), BinaryOperator.COMPARISON_NE, bin.getCodeLocation());

						if (state.satisfy(manager, toApronComparison(neg)))
							return Satisfiability.NOT_SATISFIED;

						return Satisfiability.UNKNOWN;
					}
				case COMPARISON_GE:
					if (state.satisfy(manager, toApronComparison(bin)))
						return Satisfiability.SATISFIED;
					else {
						neg = new BinaryExpression(bin.getTypes(), bin.getLeft(), bin.getRight(), BinaryOperator.COMPARISON_LT, bin.getCodeLocation());

						if (state.satisfy(manager, toApronComparison(neg)))
							return Satisfiability.NOT_SATISFIED;

						return Satisfiability.UNKNOWN;
					}
				case COMPARISON_GT:
					if (state.satisfy(manager, toApronComparison(bin)))
						return Satisfiability.SATISFIED;
					else {
						neg = new BinaryExpression(bin.getTypes(), bin.getLeft(), bin.getRight(), BinaryOperator.COMPARISON_LE, bin.getCodeLocation());

						if (state.satisfy(manager, toApronComparison(neg)))
							return Satisfiability.NOT_SATISFIED;

						return Satisfiability.UNKNOWN;
					}
				case COMPARISON_LE:
					if (state.satisfy(manager, toApronComparison(bin)))
						return Satisfiability.SATISFIED;
					else {
						neg = new BinaryExpression(bin.getTypes(), bin.getLeft(), bin.getRight(), BinaryOperator.COMPARISON_GT, bin.getCodeLocation());

						if (state.satisfy(manager, toApronComparison(neg)))
							return Satisfiability.NOT_SATISFIED;

						return Satisfiability.UNKNOWN;
					}
				case COMPARISON_LT:
					if (state.satisfy(manager, toApronComparison(bin)))
						return Satisfiability.SATISFIED;
					else {
						neg = new BinaryExpression(bin.getTypes(), bin.getLeft(), bin.getRight(), BinaryOperator.COMPARISON_GE, bin.getCodeLocation());

						if (state.satisfy(manager, toApronComparison(neg)))
							return Satisfiability.NOT_SATISFIED;

						return Satisfiability.UNKNOWN;
					}
				case COMPARISON_NE:
					if (state.satisfy(manager, toApronComparison(bin)))
						return Satisfiability.SATISFIED;
					else {
						neg = new BinaryExpression(bin.getTypes(), bin.getLeft(), bin.getRight(), BinaryOperator.COMPARISON_EQ, bin.getCodeLocation());

						if (state.satisfy(manager, toApronComparison(neg)))
							return Satisfiability.NOT_SATISFIED;

						return Satisfiability.UNKNOWN;
					}

				case LOGICAL_AND:
					return satisfies((ValueExpression) bin.getLeft(), pp).and(satisfies((ValueExpression) bin.getRight(), pp));
				case LOGICAL_OR:
					return satisfies((ValueExpression) bin.getLeft(), pp).or(satisfies((ValueExpression) bin.getRight(), pp));
				default:
					return Satisfiability.UNKNOWN;
				}
			}

			return Satisfiability.UNKNOWN;
		} catch (ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		} catch (UnsupportedOperationException e) {
			// if a sub-expression of expression cannot be 
			// translated by Apron, then Unknown is returned.
			return Satisfiability.UNKNOWN;
		}
	}

	@Override
	public Apron pushScope(ScopeToken token) throws SemanticException {
		// TODO: pushScope
		return new Apron(state);
	}

	@Override
	public Apron popScope(ScopeToken token) throws SemanticException {
		// TODO: popScope
		return new Apron(state);
	}

	@Override
	public DomainRepresentation representation() {
		return new StringRepresentation(state.toString());
	}

	@Override
	public Apron lub(Apron other) throws SemanticException {

		// we compute the least environment extending the this and other environment
		Environment lubEnv = state.getEnvironment().lce(other.state.getEnvironment());
		try {
			Abstract1 unifiedThis = state.changeEnvironmentCopy(manager, lubEnv, state.isBottom(manager));
			if (other.state.isBottom(manager)) 
				return new Apron(unifiedThis);

			Abstract1 unifiedOther = other.state.changeEnvironmentCopy(manager, lubEnv, other.state.isBottom(manager));
			if (this.state.isBottom(manager)) 
				return new Apron(unifiedOther);

			if (state.isTop(manager) || other.state.isTop(manager))
				return new Apron(unifiedOther);

			return new Apron(unifiedThis.joinCopy(manager, unifiedOther));
		} catch (ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		}
	}

	public Apron glb(Apron other) throws SemanticException {
		try {
			// we compute the least environment extending the this and other environment
			Environment lubEnv = state.getEnvironment().lce(other.state.getEnvironment());
			Abstract1 unifiedThis = state.changeEnvironmentCopy(manager, lubEnv, state.isBottom(manager));
			Abstract1 unifiedOther = other.state.changeEnvironmentCopy(manager, lubEnv, other.state.isBottom(manager));

			return new Apron(unifiedThis.meetCopy(manager, unifiedOther));
		} catch (ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		}
	}


	@Override
	public Apron widening(Apron other) throws SemanticException {
		try {
			if(other.state.isBottom(manager))
				return new Apron(state);
			if(this.state.isBottom(manager))
				return other;
			return new Apron(state.widening(manager, other.state));
		} catch (ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		}
	}

	@Override
	public boolean lessOrEqual(Apron other) throws SemanticException {
		try {
			if (state.isBottom(manager))
				return true;
			else if (other.state.isBottom(manager))
				return false;
			else if (other.state.isTop(manager))
				return true;
			else if (state.isTop(manager))
				return false;

			// we first need to  uniform the environments
			Environment unifiedEnv = state.getEnvironment().lce(other.state.getEnvironment());

			Abstract1 unifiedOther = other.state.changeEnvironmentCopy(manager, unifiedEnv, false);
			Abstract1 unifiedThis = state.changeEnvironmentCopy(manager, unifiedEnv, false);

			return unifiedThis.isIncluded(manager, unifiedOther);
		} catch (ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		}
	}

	@Override
	public Apron top() {
		try {
			return new Apron(new Abstract1(manager, new apron.Environment()));
		} catch (ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		}
	}

	@Override
	public Apron bottom() {
		try {
			return new Apron(new Abstract1(manager, new apron.Environment(), true));
		} catch (ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		}
	}

	@Override
	public boolean isBottom() {
		return equals(bottom());
	}

	@Override
	public boolean isTop() {
		return equals(top());
	}

	private Var toApronVar(Identifier id) {
		String n = id.getName();
		return new StringVar(n);
	}	

	public boolean containsIdentifier(Identifier id) {
		return Arrays.asList(state.getEnvironment().getVars()).contains(toApronVar(id));
	}

	public  Abstract1 getApronState() {
		return state;
	}
}
