package it.unive.golisa.cfg.runtime.math.rand.function;

import it.unive.golisa.cfg.type.composite.GoFunctionType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
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
import it.unive.lisa.symbolic.value.Skip;
import it.unive.lisa.type.VoidType;

/**
 * func Shuffle(n int, swap func(i, j int)).
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Shuffle extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param randUnit the unit to which this native cfg belongs to
	 */
	public Shuffle(CodeLocation location, CodeUnit randUnit) {
		super(new CodeMemberDescriptor(location, randUnit, false, "Shuffle", VoidType.INSTANCE,
				new Parameter(location, "n", GoIntType.INSTANCE),
				new Parameter(location, "swap", GoFunctionType.lookup(VoidType.INSTANCE,
						new Parameter(location, "i", GoIntType.INSTANCE),
						new Parameter(location, "j", GoIntType.INSTANCE)))),
				ShuffleImpl.class);
	}

	/**
	 * The Shuffle implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class ShuffleImpl extends BinaryExpression
			implements PluggableStatement {

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
		public static ShuffleImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ShuffleImpl(cfg, location, params[0], params[1]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left-hand side expression
		 * @param right    the left-hand side expression
		 */
		public ShuffleImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "ShuffleImpl", VoidType.INSTANCE, left, right);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(
						InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
						SymbolicExpression left, SymbolicExpression right, StatementStore<A> expressions)
						throws SemanticException {
			return state.smallStepSemantics(new Skip(getLocation()), original);
		}
	}
}
