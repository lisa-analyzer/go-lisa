package it.unive.golisa.cfg.runtime.conversion;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.program.cfg.statement.evaluation.RightToLeftEvaluation;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeTokenType;
import it.unive.lisa.type.Untyped;

/**
 * String casting.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoToString extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param pkgUnit  the unit to which this native cfg belongs to
	 */
	public GoToString(CodeLocation location, Unit pkgUnit) {
		super(new CodeMemberDescriptor(location, pkgUnit, false, "string", GoStringType.INSTANCE,
				new Parameter(location, "this", Untyped.INSTANCE)),
				ToString.class);
	}

	/**
	 * The GoToString implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class ToString extends UnaryExpression implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 * 
		 * @return the pluggable statement
		 */
		public static ToString build(CFG cfg, CodeLocation location, Expression... params) {
			return new ToString(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expression
		 */
		public ToString(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "string", RightToLeftEvaluation.INSTANCE, GoStringType.INSTANCE, expr);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				SymbolicExpression expr, StatementStore<A> expressions) throws SemanticException {
			Set<Type> castType = Collections.singleton(GoStringType.INSTANCE);
			Constant typeCast = new Constant(new TypeTokenType(castType), GoStringType.INSTANCE, getLocation());
			return state.smallStepSemantics(
					new BinaryExpression(GoStringType.INSTANCE, expr, typeCast, GoConv.INSTANCE,
							original.getLocation()),
					original);
		}
	}
}