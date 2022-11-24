package it.unive.golisa.cfg.runtime.time.function;

import it.unive.golisa.cfg.runtime.time.type.Time;
import it.unive.golisa.cfg.type.numeric.signed.GoInt64Type;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.ProgramUnit;
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
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func Unix(sec int64, nsec int64) Time
 * 
 * @link https://pkg.go.dev/time#Unix
 * 
 */
public class Unix extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param timeUnit the unit to which this native cfg belongs to
	 */
	public Unix(CodeLocation location, ProgramUnit timeUnit) {
		super(new CodeMemberDescriptor(location, timeUnit, false, "Unix",
				Time.getTimeType(timeUnit.getProgram()),
				new Parameter(location, "sec", GoInt64Type.INSTANCE),
				new Parameter(location, "nsec", GoInt64Type.INSTANCE)),
				UnixImpl.class);
	}

	/**
	 * The {@link Unix} implementation.
	 * 
	 */
	public static class UnixImpl extends BinaryExpression implements PluggableStatement {

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
		public static UnixImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new UnixImpl(cfg, location, params);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 */
		public UnixImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "UnixImpl", Time.INSTANCE, params[0], params[1]);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
				throws SemanticException {
			
			return state.smallStepSemantics(left, original).lub(state.smallStepSemantics(right, original));
		}


	}
}
