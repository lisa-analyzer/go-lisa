package it.unive.golisa.cfg.runtime.fmt;

import it.unive.golisa.cfg.VarArgsParameter;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.Untyped;

/**
 * The Sprintln function from fmt package.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Sprintln extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param fmtUnit  the unit to which this native cfg belongs to
	 */
	public Sprintln(CodeLocation location, CodeUnit fmtUnit) {
		super(new CodeMemberDescriptor(location, fmtUnit, false, "Sprintln", GoStringType.INSTANCE,
				new VarArgsParameter(location, "a", GoSliceType.lookup(Untyped.INSTANCE))),
				SprintImpl.class);
	}

	/**
	 * The {@link Sprintln} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class SprintImpl extends UnaryExpression implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		@Override
		protected int compareSameClassAndParams(Statement o) {
			return 0; // nothing else to compare
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
		public static SprintImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new SprintImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param arg      the expression
		 */
		public SprintImpl(CFG cfg, CodeLocation location, Expression arg) {
			super(cfg, location, "Sprintln", GoStringType.INSTANCE, arg);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				SymbolicExpression expr, StatementStore<A> expressions) throws SemanticException {
			return state.smallStepSemantics(new PushAny(GoStringType.INSTANCE, getLocation()), original);
		}
	}
}
