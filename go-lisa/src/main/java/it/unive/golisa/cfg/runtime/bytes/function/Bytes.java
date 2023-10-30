package it.unive.golisa.cfg.runtime.bytes.function;

import it.unive.golisa.cfg.runtime.bytes.type.Buffer;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt8Type;
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
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;

/**
 * func (b *Buffer) Bytes() []byte.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Bytes extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param unit     the unit to which this native cfg belongs to
	 */
	public Bytes(CodeLocation location, CompilationUnit unit) {
		super(new CodeMemberDescriptor(location, unit, true, "Bytes",
				GoSliceType.getSliceOfBytes(),
				new Parameter(location, "b", Buffer.getBufferType(unit.getProgram()))),
				BytesImpl.class);
	}

	/**
	 * The {@link Bytes} implementation.
	 * 
	 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
	 */
	public static class BytesImpl extends UnaryExpression
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
		public static BytesImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new BytesImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expr side of this pluggable statement
		 */
		public BytesImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "BytesImpl", GoSliceType.lookup(GoUInt8Type.INSTANCE), expr);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(InterproceduralAnalysis<A> arg0,
				AnalysisState<A> state, SymbolicExpression expr, StatementStore<A> arg3) throws SemanticException {
			return state.smallStepSemantics(expr, original);
		}
	}
}
