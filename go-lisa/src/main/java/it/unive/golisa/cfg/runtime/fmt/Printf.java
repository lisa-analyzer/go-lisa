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
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.Untyped;

/**
 * The Printf function from fmt package.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Printf extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param fmtUnit  the unit to which this native cfg belongs to
	 */
	public Printf(CodeLocation location, CodeUnit fmtUnit) {
		super(new CodeMemberDescriptor(location, fmtUnit, false, "Printf", GoStringType.INSTANCE,
				new Parameter(location, "format", GoStringType.INSTANCE),
				new VarArgsParameter(location, "a", GoSliceType.lookup(Untyped.INSTANCE))),
				SprintfImpl.class);
	}

	/**
	 * The {@link Printf} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class SprintfImpl extends BinaryExpression implements PluggableStatement {

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
		public static SprintfImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new SprintfImpl(cfg, location, params[0], params[1]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left expression
		 * @param right    the right expression
		 */
		public SprintfImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "Printf", GoStringType.INSTANCE, left, right);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(
				InterproceduralAnalysis<A> interprocedural,
				AnalysisState<A> state,
				SymbolicExpression left,
				SymbolicExpression right,
				StatementStore<A> expressions) throws SemanticException {
			return state.smallStepSemantics(new PushAny(GoStringType.INSTANCE, getLocation()), original);
		}
	}
}
