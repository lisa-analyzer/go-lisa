package it.unive.golisa.cfg.runtime.time.method;

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
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

public class Unix extends NativeCFG {

	public Unix(CodeLocation location, CompilationUnit timeUnit) {
		super(new CFGDescriptor(location, timeUnit, true, "Unix",
				GoInt64Type.INSTANCE,
				new Parameter(location, "this", Time.INSTANCE)),
				UnixImpl.class);
	}

	public static class UnixImpl extends UnaryExpression implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static UnixImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new UnixImpl(cfg, location, params);
		}

		public UnixImpl(CFG cfg, CodeLocation location, Expression[] params) {
			super(cfg, location, "UnixImpl", GoInt64Type.INSTANCE, params[0]);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
			return state.smallStepSemantics(
					new PushAny(GoInt64Type.INSTANCE, getLocation()), original);
		}
	}
}
