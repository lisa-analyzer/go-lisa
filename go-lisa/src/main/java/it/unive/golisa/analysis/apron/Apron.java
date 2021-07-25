package it.unive.golisa.analysis.apron;

import java.math.BigInteger;
import java.util.Arrays;

import apron.Abstract1;
import apron.ApronException;
import apron.Box;
import apron.Coeff;
import apron.Environment;
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
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;

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
		case Box: manager=new Box(); break;
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

			return new Apron(newState.assignCopy(manager, variable, new Texpr1Intern(newState.getEnvironment(), toApronExpression(expression)), null));
		} catch (ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		}
	}

	private Texpr1Node toApronExpression(SymbolicExpression exp) {
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
				coeff = new MpfrScalar();

			return new Texpr1CstNode(coeff);
		}

		if (exp instanceof BinaryExpression) {			
			BinaryExpression bin = (BinaryExpression) exp;

			switch(bin.getOperator()) {
			case TYPE_CAST:
			case TYPE_CONV:
				if (!exp.getTypes().isEmpty())
					return toApronExpression(bin.getLeft());
			default:
				return new Texpr1BinNode(toApronOperator(bin.getOperator()), toApronExpression(bin.getLeft()), toApronExpression(bin.getRight()));			
			}
		}

		throw new UnsupportedOperationException("Expression "+ exp.getClass().getTypeName() + " not yet supported by Apron");
	}

	private int toApronOperator(BinaryOperator op) {
		switch(op) {
		case NUMERIC_ADD: return Texpr1BinNode.OP_ADD;
		case NUMERIC_MUL: return Texpr1BinNode.OP_MUL;
		case NUMERIC_SUB: return Texpr1BinNode.OP_SUB;
		case NUMERIC_DIV: return Texpr1BinNode.OP_DIV;
		case NUMERIC_MOD: return Texpr1BinNode.OP_MOD;

		case COMPARISON_EQ: return Tcons1.EQ;
		case COMPARISON_NE: return Tcons1.DISEQ;
		case COMPARISON_GE: return Tcons1.SUPEQ;
		case COMPARISON_GT: return Tcons1.SUP;
		default: throw new UnsupportedOperationException("Operator "+op+" not yet supported by Apron interface");
		}
	}

	@Override
	public Apron smallStepSemantics(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		return new Apron(state);
	}

	@Override
	public Apron assume(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		try {
			if (state.isBottom(manager))
				return bottom();
		} catch (ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		}

		if (expression instanceof UnaryExpression) {
			UnaryExpression un = (UnaryExpression) expression;

			switch (un.getOperator()) {
			case LOGICAL_NOT:
				return assume(expression.removeNegations(), pp);
			default:
				return top();
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
				}
			case LOGICAL_AND:
				left = assume((ValueExpression) bin.getLeft(), pp); 
				right = assume((ValueExpression) bin.getRight(), pp);
				try {
					return new Apron(left.state.meetCopy(manager, right.state));
				} catch (ApronException e) {
					throw new UnsupportedOperationException("Apron library crashed", e);
				}
			case LOGICAL_OR:
				left = assume((ValueExpression) bin.getLeft(), pp); 
				right = assume((ValueExpression) bin.getRight(), pp);
				try {
					return new Apron(left.state.joinCopy(manager, right.state));
				} catch (ApronException e) {
					throw new UnsupportedOperationException("Apron library crashed", e);
				}
			default:
				return top();
			}
		}	

		return top();
	}

	private Tcons1 toApronComparison(BinaryExpression exp) {
		// Apron supports only "exp <comparison> 0", so we need to move everything on the left node 
		SymbolicExpression combinedExpr = new BinaryExpression(exp.getTypes(), exp.getLeft(), exp.getRight(), BinaryOperator.NUMERIC_SUB, exp.getCodeLocation()); 

		switch(exp.getOperator()) {
		case COMPARISON_GT:
		case COMPARISON_GE:
		case COMPARISON_NE:
		case COMPARISON_EQ:
			return new Tcons1(state.getEnvironment(), toApronOperator(exp.getOperator()), toApronExpression(combinedExpr));
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
		try {
			return new Apron(state.forgetCopy(manager, toApronVar(id), false));
		} catch (ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		}
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		try {
			if (state.isBottom(manager))
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
//					if (state.satisfy(manager, toApronComparison(bin)))
//						return Satisfiability.SATISFIED;
//					else if (manager.wasExact()) 
//						return Satisfiability.NOT_SATISFIED;
//					else 
//						return Satisfiability.UNKNOWN;
				case LOGICAL_AND:
					return satisfies((ValueExpression) bin.getLeft(), pp).and(satisfies((ValueExpression) bin.getRight(), pp));
				case LOGICAL_OR:
					return satisfies((ValueExpression) bin.getLeft(), pp).or(satisfies((ValueExpression) bin.getRight(), pp));
				default:
					return Satisfiability.UNKNOWN;
				}
			}

			return Satisfiability.UNKNOWN;
		}
		catch(ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
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
		try {
			if (other.state.isBottom(manager))
				return new Apron(state);
			if (this.state.isBottom(manager))
				return other;
			if (state.isTop(manager) || other.state.isTop(manager))
				return top();

			return new Apron(state.joinCopy(manager, other.state));
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
			if (other.state.isBottom(manager))
				return false;
			return state.isIncluded(manager, other.state);
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
		try {
			return state.isBottom(manager);
		} catch (ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		}
	}

	@Override
	public boolean isTop() {
		try {
			return state.isTop(manager);
		} catch (ApronException e) {
			throw new UnsupportedOperationException("Apron library crashed", e);
		}
	}

	private Var toApronVar(Identifier id) {
		String n = id.getName();
		return new StringVar(n);
	}	

	public boolean containsIdentifier(Identifier id) {
		return Arrays.asList(state.getEnvironment().getVars()).contains(toApronVar(id));
	}
}
