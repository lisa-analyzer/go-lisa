package it.unive.golisa.cfg.runtime.strings;

import it.unive.golisa.cfg.runtime.strings.GoLen.Len;
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
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

public class ToLower extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location    the location where this native cfg is defined
	 * @param strconvUnit the unit to which this native cfg belongs to
	 */
	public ToLower(CodeLocation location, CompilationUnit stringUnit) {
		super(new CFGDescriptor(location, stringUnit, false, "ToLower", GoStringType.INSTANCE,
				new Parameter(location, "this", GoStringType.INSTANCE)),
				Len.class);
	}

	public static class ToLowerImpl extends it.unive.lisa.program.cfg.statement.UnaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static ToLowerImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ToLowerImpl(cfg, location, params[0]);
		}

		public ToLowerImpl(CFG cfg, CodeLocation location, Expression arg) {
			super(cfg, location, "ToLower", GoStringType.INSTANCE, arg);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
			return state.smallStepSemantics(new PushAny(GoStringType.INSTANCE, getLocation()), original);
		}
	}
}
