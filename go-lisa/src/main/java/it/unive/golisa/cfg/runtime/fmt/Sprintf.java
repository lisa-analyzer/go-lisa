package it.unive.golisa.cfg.runtime.fmt;

import it.unive.golisa.cfg.type.GoStringType;
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
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Untyped;

/**
 * The Sprintf function from fmt package.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Sprintf extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param fmtUnit  the unit to which this native cfg belongs to
	 */
	public Sprintf(CodeLocation location, CodeUnit fmtUnit) {
		super(new CodeMemberDescriptor(location, fmtUnit, false, "Sprintf", GoStringType.INSTANCE,
				new Parameter(location, "this", Untyped.INSTANCE), new Parameter(location, "that", Untyped.INSTANCE), new Parameter(location, "other", Untyped.INSTANCE)),
				SprintfImpl.class);
	}

	/**
	 * The {@link Sprintf} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class SprintfImpl extends NaryExpression implements PluggableStatement {

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
		public static SprintfImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new SprintfImpl(cfg, location, params[0], params[1], params[2]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param arg      the expression
		 */
		public SprintfImpl(CFG cfg, CodeLocation location, Expression left, Expression middle, Expression right) {
			super(cfg, location, "Sprintf", GoStringType.INSTANCE, left, middle, right);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> expressionSemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V, T> expressions)
				throws SemanticException {

			AnalysisState<A, H, V, T> result = state.bottom();
			for (SymbolicExpression left : params[0])
				for (SymbolicExpression middle : params[1])
					for (SymbolicExpression right : params[2]) {
						result = result.lub(state.smallStepSemantics(left, original).smallStepSemantics(middle, original).smallStepSemantics(right, original));
					}
			return result;

		}
	}
}
