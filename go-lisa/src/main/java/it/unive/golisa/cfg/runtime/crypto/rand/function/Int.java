package it.unive.golisa.cfg.runtime.crypto.rand.function;

import it.unive.golisa.cfg.runtime.io.type.Reader;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
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

/**
 * func Int(rand io.Reader, max *big.Int) (n *big.Int, err error).
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Int extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param randUnit the unit to which this native cfg belongs to
	 */
	public Int(CodeLocation location, CodeUnit randUnit) {
		super(new CodeMemberDescriptor(location, randUnit, false, "Int",
				GoTupleType.getTupleTypeOf(location,
						it.unive.golisa.cfg.runtime.math.big.type.Int.getIntType(randUnit.getProgram()),
						GoErrorType.INSTANCE),
				new Parameter(location, "rand", Reader.getReaderType(randUnit.getProgram())),
				new Parameter(location, "max",
						it.unive.golisa.cfg.runtime.math.big.type.Int.getIntType(randUnit.getProgram()))),
				IntImpl.class);
	}

	/**
	 * The {@link Int} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class IntImpl extends BinaryExpression
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
		public static IntImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new IntImpl(cfg, location, params[0], params[1]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left-hand side of this pluggable statement
		 * @param right    the right-hand side of this pluggable statement
		 */
		public IntImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "IntImpl", GoTupleType.getTupleTypeOf(location,
					it.unive.golisa.cfg.runtime.math.big.type.Int.getIntType(null), GoErrorType.INSTANCE), left, right);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A> expressions)
				throws SemanticException {
			return state.top();
		}
	}
}
