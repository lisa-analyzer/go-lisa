package it.unive.golisa.cfg.runtime.crypto.rand.function;

import it.unive.golisa.cfg.runtime.io.type.Reader;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoTypesTuple;
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
 * func Int(rand io.Reader, max *big.Int) (n *big.Int, err error)
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Int extends NativeCFG {

	public Int(CodeLocation location, CompilationUnit randUnit) {
		super(new CFGDescriptor(location, randUnit, false, "Read",  GoTypesTuple.getTupleTypeOf(location, it.unive.golisa.cfg.runtime.math.big.type.Int.INSTANCE, GoErrorType.INSTANCE),
				new Parameter(location, "rand", Reader.INSTANCE),
				new Parameter(location, "max", it.unive.golisa.cfg.runtime.math.big.type.Int.INSTANCE)),
				IntImpl.class);
	}

	public static class IntImpl extends BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static IntImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new IntImpl(cfg, location, params[0], params[1]);
		}

		public IntImpl(CFG cfg, CodeLocation location, Expression expr, Expression expr2) {
			super(cfg, location, "IntImpl", GoTypesTuple.getTupleTypeOf(location, it.unive.golisa.cfg.runtime.math.big.type.Int.INSTANCE, GoErrorType.INSTANCE), expr, expr2);
		}

		@Override
		protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
				InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state, SymbolicExpression left,
				SymbolicExpression right, StatementStore<A, H, V> expressions) throws SemanticException {
			return state.top();
		}
	}
}