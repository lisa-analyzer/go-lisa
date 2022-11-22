package it.unive.golisa.cfg.runtime.google.uuid.function;

import it.unive.golisa.analysis.taint.Clean;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Untyped;

/**
 * 
 */
public class NewUUID extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param fmtUnit  the unit to which this native cfg belongs to
	 */
	public NewUUID(CodeLocation location, CodeUnit fmtUnit) {
		super(new CodeMemberDescriptor(location, fmtUnit, false, "NewUUID", Untyped.INSTANCE),
				NewUUIDImpl.class);
	}

	/**
	 * The {@link NewUUID} implementation.
	 * 
	 */
	public static class NewUUIDImpl extends NaryExpression implements PluggableStatement {

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
		public static NewUUIDImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new NewUUIDImpl(cfg, location);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param arg      the expression
		 */
		public NewUUIDImpl(CFG cfg, CodeLocation location) {
			super(cfg, location, "NewUUID", Untyped.INSTANCE);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> expressionSemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V, T> expressions)
				throws SemanticException {
			
			return state.smallStepSemantics(new Clean(getStaticType(), getLocation()), original);
		}

	}
}
