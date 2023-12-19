package it.unive.golisa.cfg.runtime.path.filepath.function;

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
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func Join(elem ...string) string.
 * 
 * @link https://pkg.go.dev/path/filepath#Join
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Join extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location     the location where this native cfg is defined
	 * @param pathfileUnit the unit to which this native cfg belongs to
	 */
	public Join(CodeLocation location, CodeUnit pathfileUnit) {
		super(new CodeMemberDescriptor(location, pathfileUnit, false, "Dir",
				GoStringType.INSTANCE,
				new Parameter(location, "elem", GoSliceType.lookup(GoStringType.INSTANCE))),
				DirImpl.class);
	}

	/**
	 * The Join implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class DirImpl extends UnaryExpression
			implements PluggableStatement {

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
		public static DirImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new DirImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expression
		 */
		public DirImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "DirImpl", GoStringType.INSTANCE, expr);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				SymbolicExpression expr, StatementStore<A> expressions) throws SemanticException {
			return state.smallStepSemantics(new PushAny(GoStringType.INSTANCE, getLocation()), original);
		}
	}
}