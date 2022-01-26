package it.unive.golisa.cfg.runtime.math.rand.method;

import it.unive.golisa.cfg.runtime.math.rand.type.Rand;
import it.unive.golisa.cfg.type.numeric.signed.GoInt32Type;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;

import it.unive.lisa.symbolic.SymbolicExpression;

/**
 * func (r *Rand) Int31n(n int32) int32
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Int31n extends NativeCFG {

	public Int31n(CodeLocation location, CompilationUnit randUnit) {
		super(new CFGDescriptor(location, randUnit, false, "Int31n", GoInt32Type.INSTANCE,
				new Parameter(location, "n", GoInt32Type.INSTANCE), new Parameter(location, "this", Rand.INSTANCE)),
				Int31nImpl.class);
	}

	public static class Int31nImpl extends BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static Int31nImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new Int31nImpl(cfg, location, params[0], params[1]);
		}

		public Int31nImpl(CFG cfg, CodeLocation location, Expression expr, Expression expr2) {
			super(cfg, location, "IntnImpl", GoInt32Type.INSTANCE, expr, expr2);
		}


		@Override
		protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
				InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state, SymbolicExpression left,
				SymbolicExpression right, StatementStore<A, H, V> expressions) throws SemanticException {
			return state.top();
		}
	}
}
