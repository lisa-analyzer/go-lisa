package it.unive.golisa.cfg.runtime.math.rand.function;

import it.unive.golisa.cfg.type.numeric.unsigned.GoUInt64Type;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.ValueDomain;
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

/**
 * func Uint64() uint64
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class UInt64 extends NativeCFG {

	public UInt64(CodeLocation location, CompilationUnit randUnit) {
		super(new CFGDescriptor(location, randUnit, false, "Uint64", GoUInt64Type.INSTANCE),
				Uint64Impl.class);
	}

	public static class Uint64Impl extends it.unive.lisa.program.cfg.statement.NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static Uint64Impl build(CFG cfg, CodeLocation location, Expression... params) {
			return new Uint64Impl(cfg, location);
		}

		public Uint64Impl(CFG cfg, CodeLocation location) {
			super(cfg, location, "Uint64Impl", GoUInt64Type.INSTANCE);
		}

		@Override
		public <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> expressionSemantics(
						InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
						ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V> expressions)
						throws SemanticException {
			return state.top();
		}
	}
}