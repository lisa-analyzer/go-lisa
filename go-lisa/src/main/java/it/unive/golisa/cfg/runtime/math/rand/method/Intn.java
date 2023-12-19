package it.unive.golisa.cfg.runtime.math.rand.method;

import it.unive.golisa.cfg.runtime.math.rand.type.Rand;
import it.unive.golisa.cfg.type.numeric.signed.GoInt64Type;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
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

/**
 * func (r *Rand) Intn(n int) int.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Intn extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param randUnit the unit to which this native cfg belongs to
	 */
	public Intn(CodeLocation location, CompilationUnit randUnit) {
		super(new CodeMemberDescriptor(location, randUnit, true, "Int63n", GoInt64Type.INSTANCE,
				new Parameter(location, "this", Rand.getRandType(randUnit.getProgram())),
				new Parameter(location, "n", GoInt64Type.INSTANCE)),
				IntnImpl.class);
	}

	/**
	 * The Intn implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class IntnImpl extends BinaryExpression
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
		public static IntnImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new IntnImpl(cfg, location, params[0], params[1]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left-hand side of this pugglable statement
		 * @param right    the right-hand side of this pugglable statement
		 */
		public IntnImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "Int63nImpl", GoInt64Type.INSTANCE, left, right);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A> expressions)
				throws SemanticException {
			return state.smallStepSemantics(new PushAny(GoIntType.INSTANCE, getLocation()), original);
		}
	}
}
