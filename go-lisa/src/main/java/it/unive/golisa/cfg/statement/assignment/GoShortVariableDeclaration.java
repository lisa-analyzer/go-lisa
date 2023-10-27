package it.unive.golisa.cfg.statement.assignment;

import it.unive.golisa.cfg.type.numeric.floating.GoFloat32Type;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.golisa.cfg.type.untyped.GoUntypedFloat;
import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.program.cfg.statement.evaluation.RightToLeftEvaluation;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.binary.TypeConv;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeTokenType;
import java.util.Collections;

/**
 * A Go short variable declaration statement.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoShortVariableDeclaration extends it.unive.lisa.program.cfg.statement.BinaryExpression {

	/**
	 * Builds a Go variable declaration with initialization, assigning
	 * {@code expression} to {@code target}.
	 * 
	 * @param cfg        the cfg that this declaration belongs to
	 * @param location   the location where this statement is defined
	 * @param var        the declared variable
	 * @param expression the expression to assign to {@code var}
	 */
	public GoShortVariableDeclaration(CFG cfg, CodeLocation location, VariableRef var, Expression expression) {
		super(cfg, location, ":=", RightToLeftEvaluation.INSTANCE, var, expression);
	}

	@Override
	public String toString() {
		return getLeft() + " := " + getRight();
	}

	/**
	 * Numerical typer class.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class NumericalTyper {

		/**
		 * Types an expression (if it is untyped (int or float), this method
		 * returns it as typed).
		 * 
		 * @param exp  the expression to type
		 * @param type the dynamic type of the expression
		 * 
		 * @return the typed expression
		 */
		public static SymbolicExpression type(SymbolicExpression exp, Type type) {
			if (type instanceof GoUntypedInt) {
				Constant typeCast = new Constant(new TypeTokenType(Collections.singleton(GoIntType.INSTANCE)),
						GoIntType.INSTANCE, exp.getCodeLocation());
				return new BinaryExpression(GoIntType.INSTANCE, exp, typeCast, TypeConv.INSTANCE,
						exp.getCodeLocation());

			} else if (type instanceof GoUntypedFloat) {
				Constant typeCast = new Constant(
						new TypeTokenType(Collections.singleton(GoFloat32Type.INSTANCE)),
						GoFloat32Type.INSTANCE,
						exp.getCodeLocation());
				return new BinaryExpression(GoFloat32Type.INSTANCE, exp, typeCast, TypeConv.INSTANCE,
						exp.getCodeLocation());
			} else
				return exp;
		}
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(
			InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
			SymbolicExpression left, SymbolicExpression right, StatementStore<A> expressions)
			throws SemanticException {
		// e.g., _ := f(), we just return right state
		if (GoLangUtils.refersToBlankIdentifier(getLeft()))
			return state;
		Type rtype = state.getState().getDynamicTypeOf(right, this, state.getState());
		AnalysisState<A> result = state.assign(left, NumericalTyper.type(right, rtype), this);
		if (!getRight().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getRight().getMetaVariables());
		if (!getLeft().getMetaVariables().isEmpty())
			result = result.forgetIdentifiers(getLeft().getMetaVariables());
		return result;
	}
}
