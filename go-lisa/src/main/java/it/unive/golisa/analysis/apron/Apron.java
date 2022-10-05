package it.unive.golisa.analysis.apron;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.function.Predicate;

import apron.Abstract1;
import apron.ApronException;
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
import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.representation.DomainRepresentation;
import it.unive.lisa.analysis.representation.StringRepresentation;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
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
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingAdd;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingDiv;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingMod;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingMul;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingSub;
import it.unive.lisa.symbolic.value.operator.binary.StringConcat;
import it.unive.lisa.symbolic.value.operator.binary.TypeCast;
import it.unive.lisa.symbolic.value.operator.binary.TypeConv;
import it.unive.lisa.symbolic.value.operator.unary.LogicalNegation;
import it.unive.lisa.symbolic.value.operator.unary.UnaryOperator;

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
		case Box: manager= new apron.Box(); break;
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
				return this;//forgetAbstractionOf(newState, id, pp);

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
		Apron ge = result.assume(new BinaryExpression(GoBoolType.INSTANCE, id, zero, ComparisonGe.INSTANCE, pp.getLocation()), pp);
		Apron le = result.assume(new BinaryExpression(GoBoolType.INSTANCE, id, zero, ComparisonLe.INSTANCE, pp.getLocation()), pp);
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
			return null;
			//			UnaryExpression un = (UnaryExpression) exp;
			//			UnaryOperator op = un.getOperator();
			//			if (op == LogicalNegation.INSTANCE)
			//				break;
			//			else if (op == NumericNegation.INSTANCE)
			//				break;
			//			else if (op == StringLength.INSTANCE)
			//				break;
			//			else if (op == TypeOf.INSTANCE)
			//				break;
			//			else
			//				break;
		}

		if (exp instanceof BinaryExpression) {			
			BinaryExpression bin = (BinaryExpression) exp;
			BinaryOperator op = bin.getOperator();
			if (op == TypeCast.INSTANCE || op == TypeConv.INSTANCE) {
				if (!exp.getRuntimeTypes().isEmpty())
					return toApronExpression(bin.getLeft());
			} else {
				Texpr1Node rewrittenLeft =  toApronExpression(bin.getLeft());
				if (rewrittenLeft == null)
					return null;

				Texpr1Node rewrittenRight = toApronExpression(bin.getRight());
				if (rewrittenRight == null)
					return null;

				if (op == ComparisonLt.INSTANCE)
					return new Texpr1BinNode(Tcons1.SUP, rewrittenRight, rewrittenLeft);			

				if (op == ComparisonLe.INSTANCE)
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
		if (op  == it.unive.lisa.symbolic.value.operator.binary.StringConcat.INSTANCE
				|| op == NumericNonOverflowingAdd.INSTANCE
				|| op == NumericNonOverflowingMul.INSTANCE
				|| op == NumericNonOverflowingDiv.INSTANCE
				|| op == NumericNonOverflowingSub.INSTANCE
				|| op == NumericNonOverflowingMod.INSTANCE
				|| op == ComparisonEq.INSTANCE
				|| op == ComparisonNe.INSTANCE
				|| op == ComparisonGe.INSTANCE
				|| op == ComparisonGt.INSTANCE)
			return true;

		return false;
	}

	private int toApronOperator(BinaryOperator op) {
		if (op == StringConcat.INSTANCE || op == NumericNonOverflowingAdd.INSTANCE)
			return Texpr1BinNode.OP_ADD;
		else if (op == NumericNonOverflowingMul.INSTANCE)
			return Texpr1BinNode.OP_MUL;
		else if (op == NumericNonOverflowingSub.INSTANCE)
			return Texpr1BinNode.OP_SUB;
		else if (op == NumericNonOverflowingDiv.INSTANCE)
			return Texpr1BinNode.OP_DIV;
		else if (op == NumericNonOverflowingMod.INSTANCE)
			return Texpr1BinNode.OP_MOD;
		else if (op == ComparisonEq.INSTANCE)
			return Tcons1.EQ;
		else if (op == ComparisonNe.INSTANCE)
			return Tcons1.DISEQ;
		else if (op == ComparisonGe.INSTANCE)
			return Tcons1.SUPEQ;
		else if (op == ComparisonGt.INSTANCE)
			return Tcons1.SUP;

		throw new UnsupportedOperationException("Operator " + op + " not yet supported by Apron interface");

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
			UnaryOperator op = un.getOperator();

			if (op == LogicalNegation.INSTANCE) {
				ValueExpression inner = (ValueExpression) un.getExpression();
				if (inner instanceof UnaryExpression && ((UnaryExpression) inner).getOperator() == LogicalNegation.INSTANCE)
					return assume(((ValueExpression) ((UnaryExpression) inner).getExpression()).removeNegations(), pp);


				// It is possible that the expression cannot be rewritten (e.g.,
				// !true, !var_id) hence we recursively call assume iff something changed
				ValueExpression rewritten = un.removeNegations();
				if (rewritten != un)
					return assume(rewritten, pp);
				else
					return this;
			} else
				return this;
		}

		if (expression instanceof BinaryExpression) {
			BinaryExpression bin = (BinaryExpression) expression;
			BinaryOperator op = bin.getOperator();
			Apron left, right;

			if (op == ComparisonEq.INSTANCE
					|| op == ComparisonGe.INSTANCE
					|| op == ComparisonGt.INSTANCE
					|| op == ComparisonLe.INSTANCE
					|| op == ComparisonLt.INSTANCE
					|| op == ComparisonNe.INSTANCE) {

				try {
					return new Apron(state.meetCopy(manager, toApronComparison(bin)));
				} catch (ApronException e) {
					throw new UnsupportedOperationException("Apron library crashed", e);
				} catch (UnsupportedOperationException e) {
					// if a sub-expression of expression cannot be 
					// translated by Apron, then top is returned.
					return this;
				}
			} else if (op == LogicalAnd.INSTANCE) {
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
			} else if (op == LogicalOr.INSTANCE) {
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
			} else
				return this;
		}

		return this;
	}

	private Tcons1 toApronComparison(BinaryExpression exp) throws ApronException {
		// Apron supports only "exp <comparison> 0", so we need to move everything on the left node 
		SymbolicExpression combinedExpr = new BinaryExpression(exp.getStaticType(), exp.getLeft(), exp.getRight(), NumericNonOverflowingSub.INSTANCE, exp.getCodeLocation()); 
		BinaryOperator op = exp.getOperator();
		if (op == ComparisonGt.INSTANCE
				|| op == ComparisonGe.INSTANCE
				|| op == ComparisonNe.INSTANCE
				|| op == ComparisonEq.INSTANCE) {
			Texpr1Node apronExpression = toApronExpression(combinedExpr);
			if (apronExpression != null)
				return new Tcons1(state.getEnvironment(), toApronOperator(exp.getOperator()), apronExpression);
			else
				throw new UnsupportedOperationException("Comparison operator "+exp.getOperator()+" not yet supported"); 		
		} else if (op == ComparisonLe.INSTANCE)
			return toApronComparison(new BinaryExpression(exp.getStaticType(), exp.getRight(), exp.getLeft(), ComparisonGe.INSTANCE, exp.getCodeLocation()));
		else if (op == ComparisonLt.INSTANCE)
			return toApronComparison(new BinaryExpression(exp.getStaticType(), exp.getRight(), exp.getLeft(), ComparisonGt.INSTANCE, exp.getCodeLocation()));
		else
			throw new UnsupportedOperationException("Comparison operator "+exp.getOperator()+" not yet supported"); 		
	}

	@Override
	public Satisfiability satisfies(ValueExpression expression, ProgramPoint pp) throws SemanticException {
		try {

			if (state.getEnvironment().equals(new apron.Environment()))
				return Satisfiability.BOTTOM;

			if (expression instanceof UnaryExpression) {
				UnaryExpression un = (UnaryExpression) expression;
				UnaryOperator op = un.getOperator();

				if (op == LogicalNegation.INSTANCE) {
					Satisfiability isSAT = satisfies((ValueExpression) un.getExpression(), pp);
					if (isSAT == Satisfiability.SATISFIED)
						return Satisfiability.NOT_SATISFIED;
					else if (isSAT == Satisfiability.NOT_SATISFIED)
						return Satisfiability.SATISFIED;
					else
						return Satisfiability.UNKNOWN;
				} else
					return Satisfiability.UNKNOWN;
			}


			if (expression instanceof BinaryExpression) {
				BinaryExpression bin = (BinaryExpression) expression;
				BinaryExpression neg;
				BinaryOperator op = bin.getOperator();
				// FIXME: it seems there's a bug with manager.wasExact
				if (op == ComparisonEq.INSTANCE) {
					if (state.satisfy(manager, toApronComparison(bin)))
						return Satisfiability.SATISFIED;
					else {
						neg = new BinaryExpression(bin.getStaticType(), bin.getLeft(), bin.getRight(), ComparisonNe.INSTANCE, bin.getCodeLocation());

						if (state.satisfy(manager, toApronComparison(neg)))
							return Satisfiability.NOT_SATISFIED;

						return Satisfiability.UNKNOWN;
					}
				} else if (op == ComparisonGe.INSTANCE) {
					if (state.satisfy(manager, toApronComparison(bin)))
						return Satisfiability.SATISFIED;
					else {
						neg = new BinaryExpression(bin.getStaticType(), bin.getLeft(), bin.getRight(), ComparisonLt.INSTANCE, bin.getCodeLocation());

						if (state.satisfy(manager, toApronComparison(neg)))
							return Satisfiability.NOT_SATISFIED;

						return Satisfiability.UNKNOWN;
					}
				} else if (op == ComparisonGt.INSTANCE) {
					if (state.satisfy(manager, toApronComparison(bin)))
						return Satisfiability.SATISFIED;
					else {
						neg = new BinaryExpression(bin.getStaticType(), bin.getLeft(), bin.getRight(), ComparisonLe.INSTANCE, bin.getCodeLocation());

						if (state.satisfy(manager, toApronComparison(neg)))
							return Satisfiability.NOT_SATISFIED;

						return Satisfiability.UNKNOWN;
					}
				} else if (op == ComparisonLe.INSTANCE) {
					if (state.satisfy(manager, toApronComparison(bin)))
						return Satisfiability.SATISFIED;
					else {
						neg = new BinaryExpression(bin.getStaticType(), bin.getLeft(), bin.getRight(), ComparisonGt.INSTANCE, bin.getCodeLocation());

						if (state.satisfy(manager, toApronComparison(neg)))
							return Satisfiability.NOT_SATISFIED;

						return Satisfiability.UNKNOWN;
					}
				} else if (op == ComparisonLt.INSTANCE) {
					if (state.satisfy(manager, toApronComparison(bin)))
						return Satisfiability.SATISFIED;
					else {
						neg = new BinaryExpression(bin.getStaticType(), bin.getLeft(), bin.getRight(), ComparisonGe.INSTANCE, bin.getCodeLocation());

						if (state.satisfy(manager, toApronComparison(neg)))
							return Satisfiability.NOT_SATISFIED;

						return Satisfiability.UNKNOWN;
					}
				} else if (op == ComparisonNe.INSTANCE) {
					if (state.satisfy(manager, toApronComparison(bin)))
						return Satisfiability.SATISFIED;
					else {
						neg = new BinaryExpression(bin.getStaticType(), bin.getLeft(), bin.getRight(), ComparisonEq.INSTANCE, bin.getCodeLocation());

						if (state.satisfy(manager, toApronComparison(neg)))
							return Satisfiability.NOT_SATISFIED;

						return Satisfiability.UNKNOWN;
					}

				} else if (op == LogicalAnd.INSTANCE)
					return satisfies((ValueExpression) bin.getLeft(), pp).and(satisfies((ValueExpression) bin.getRight(), pp));
				else if (op == LogicalOr.INSTANCE)
					return satisfies((ValueExpression) bin.getLeft(), pp).or(satisfies((ValueExpression) bin.getRight(), pp));
				else
					return Satisfiability.UNKNOWN;
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

	@Override
	public Apron forgetIdentifiersIf(Predicate<Identifier> test) throws SemanticException {
		// FIXME
		return this;
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
}
