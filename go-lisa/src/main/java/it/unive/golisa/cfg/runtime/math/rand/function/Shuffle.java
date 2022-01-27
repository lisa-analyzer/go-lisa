package it.unive.golisa.cfg.runtime.math.rand.function;

import it.unive.golisa.cfg.type.composite.GoFunctionType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
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
import it.unive.lisa.type.VoidType;

/**
 * func Shuffle(n int, swap func(i, j int))
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Shuffle extends NativeCFG {

	public Shuffle(CodeLocation location, CompilationUnit randUnit) {
		super(new CFGDescriptor(location, randUnit, false, "Shuffle", VoidType.INSTANCE,
				new Parameter(location, "n", GoIntType.INSTANCE),
				new Parameter(location, "swap", GoFunctionType.lookup(
						new GoFunctionType(VoidType.INSTANCE, 
								new Parameter(location, "i", GoIntType.INSTANCE), 
								new Parameter(location, "j", GoIntType.INSTANCE))))),
				ShuffleImpl.class);
	}

	public static class ShuffleImpl extends BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static ShuffleImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new ShuffleImpl(cfg, location, params[0], params[1]);
		}

		public ShuffleImpl(CFG cfg, CodeLocation location, Expression expr,Expression expr2) {
			super(cfg, location, "ShuffleImpl", VoidType.INSTANCE, expr, expr2);
		}

		@Override
		protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
				InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state, SymbolicExpression left,
				SymbolicExpression right, StatementStore<A, H, V> expressions) throws SemanticException {
			return state.top();
		}
	}
}
