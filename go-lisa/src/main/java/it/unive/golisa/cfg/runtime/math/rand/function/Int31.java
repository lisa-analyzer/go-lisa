package it.unive.golisa.cfg.runtime.math.rand.function;

import it.unive.golisa.cfg.type.numeric.signed.GoInt32Type;
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
 * func Int31() int32
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Int31 extends NativeCFG {

	public Int31(CodeLocation location, CompilationUnit randUnit) {
		super(new CFGDescriptor(location, randUnit, false, "Int31", GoInt32Type.INSTANCE),
				Int31Impl.class);
	}

	public static class Int31Impl extends it.unive.lisa.program.cfg.statement.NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static Int31Impl build(CFG cfg, CodeLocation location, Expression... params) {
			return new Int31Impl(cfg, location);
		}

		public Int31Impl(CFG cfg, CodeLocation location) {
			super(cfg, location, "Int31Impl", GoInt32Type.INSTANCE);
		}

		@Override
		public <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> expressionSemantics(
						InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
						ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V> expressions)
						throws SemanticException {
			return state.smallStepSemantics(new PushAny(Caches.types().mkSingletonSet(GoInt32Type.INSTANCE), getLocation()), original);
		}
	}
}
