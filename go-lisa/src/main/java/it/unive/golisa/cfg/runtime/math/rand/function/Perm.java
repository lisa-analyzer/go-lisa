package it.unive.golisa.cfg.runtime.math.rand.function;

import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
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

/**
 * func Perm(n int) []int
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Perm extends NativeCFG {

	public Perm(CodeLocation location, CompilationUnit randUnit) {
		super(new CFGDescriptor(location, randUnit, false, "Perm",
				GoSliceType.lookup(new GoSliceType(GoIntType.INSTANCE)),
				new Parameter(location, "n", GoIntType.INSTANCE)),
				PermImpl.class);
	}

	public static class PermImpl extends UnaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static PermImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new PermImpl(cfg, location, params[0]);
		}

		public PermImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "PermImpl", GoIntType.INSTANCE, expr);
		}

		@Override
		protected <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
						InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
						SymbolicExpression expr,
						StatementStore<A, H, V> expressions) throws SemanticException {
			return state.smallStepSemantics(new PushAny(Caches.types().mkSingletonSet(new GoSliceType(GoIntType.INSTANCE)), getLocation()), original);
		}
	}
}
