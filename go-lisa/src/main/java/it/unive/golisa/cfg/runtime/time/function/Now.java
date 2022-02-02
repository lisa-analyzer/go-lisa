package it.unive.golisa.cfg.runtime.time.function;

import it.unive.golisa.cfg.runtime.time.type.Duration;
import it.unive.golisa.cfg.runtime.time.type.Time;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

/**
 * func Now() Time
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Now extends NativeCFG {

	public Now(CodeLocation location, CompilationUnit timeUnit) {
		super(new CFGDescriptor(location, timeUnit, false, "Now", Duration.INSTANCE),
				NowImpl.class);
	}

	public static class NowImpl extends it.unive.lisa.program.cfg.statement.NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static NowImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new NowImpl(cfg, location);
		}

		public NowImpl(CFG cfg, CodeLocation location) {
			super(cfg, location, "NowImpl", Time.INSTANCE);
		}

		@Override
		public <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> expressionSemantics(
						InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
						ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V> expressions)
						throws SemanticException {
			return state.smallStepSemantics(
					new PushAny(Caches.types().mkSingletonSet(Time.INSTANCE), getLocation()), original);
		}
	}
}
