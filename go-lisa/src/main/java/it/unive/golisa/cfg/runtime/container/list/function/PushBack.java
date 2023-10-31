package it.unive.golisa.cfg.runtime.container.list.function;

import it.unive.golisa.cfg.runtime.container.list.type.List;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.ProgramUnit;
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
 * func (l *List) PushBack(v any) *Element.
 *
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class PushBack extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param listUnit the unit to which this native cfg belongs to
	 */
	public PushBack(CodeLocation location, ProgramUnit listUnit) {
		super(new CodeMemberDescriptor(location, listUnit, false, "PushBack", Untyped.INSTANCE,
				new Parameter(location, "l", List.getListType(listUnit.getProgram())),
				new Parameter(location, "v", Untyped.INSTANCE)),
				PushBackImpl.class);
	}

	/**
	 * The {@link PushBack} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class PushBackImpl extends BinaryExpression
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
		public static PushBackImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new PushBackImpl(cfg, location, params[0], params[1]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left argument
		 * @param right    the right argument
		 */
		public PushBackImpl(CFG cfg, CodeLocation location, Expression left, Expression right) {
			super(cfg, location, "PushBackImpl", List.INSTANCE, left, right);
		}

		@Override
		public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state, SymbolicExpression left,
				SymbolicExpression right, StatementStore<A> expressions) throws SemanticException {
			return state.smallStepSemantics(new PushAny(Untyped.INSTANCE, getLocation()), original);
		}
	}
}
