package it.unive.golisa.cfg.runtime.strings;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.TernaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.operator.ternary.StringReplace;
import it.unive.lisa.type.Type;

public class Replace extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location   the location where this native cfg is defined
	 * @param stringUnit the unit to which this native cfg belongs to
	 */
	public Replace(CodeLocation location, CompilationUnit stringUnit) {
		super(new CFGDescriptor(location, stringUnit, false, "Replace", GoStringType.INSTANCE,
				new Parameter(location, "this", GoStringType.INSTANCE),
				new Parameter(location, "that", GoStringType.INSTANCE),
				new Parameter(location, "other", GoStringType.INSTANCE)),
				ReplaceImpl.class);
	}

	/**
	 * The Replace implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class ReplaceImpl extends TernaryExpression implements PluggableStatement {

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
		public static ReplaceImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ReplaceImpl(cfg, location, params[0], params[1], params[2]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param left     the left-hand side of this pluggable statement
		 * @param middle   the middle-hand side of this pluggable statement
		 * @param right    the right-hand side of this pluggable statement
		 */
		public ReplaceImpl(CFG cfg, CodeLocation location, Expression left, Expression middle, Expression right) {
			super(cfg, location, "Replace", left, middle, right);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> ternarySemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						SymbolicExpression left, SymbolicExpression middle, SymbolicExpression right,
						StatementStore<A, H, V, T> expressions) throws SemanticException {

			AnalysisState<A, H, V, T> result = state.bottom();
			for (Type leftType : left.getRuntimeTypes())
				for (Type middleType : middle.getRuntimeTypes())
					for (Type rightType : right.getRuntimeTypes())
						if (!leftType.isStringType() && !leftType.isUntyped())
							continue;
						else if (!middleType.isStringType() && !middleType.isUntyped())
							continue;
						else if (!rightType.isStringType() && !rightType.isUntyped())
							continue;
						else
							result = result.lub(state
									.smallStepSemantics(new it.unive.lisa.symbolic.value.TernaryExpression(
											GoStringType.INSTANCE,
											left, middle, right, StringReplace.INSTANCE, getLocation()), original));
			return result;
		}
	}
}
