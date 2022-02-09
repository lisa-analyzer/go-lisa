package it.unive.golisa.cfg.runtime.math.rand.function;

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
import it.unive.lisa.symbolic.value.Skip;
import it.unive.lisa.type.VoidType;

/**
 * func Seed(seed int64)
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Seed extends NativeCFG {

	public Seed(CodeLocation location, CompilationUnit randUnit) {
		super(new CFGDescriptor(location, randUnit, false, "Seed", VoidType.INSTANCE,
				new Parameter(location, "seed", GoInt64Type.INSTANCE)),
				SeedImpl.class);
	}

	public static class SeedImpl extends UnaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static SeedImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new SeedImpl(cfg, location, params[0]);
		}

		public SeedImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "SeedImpl", VoidType.INSTANCE, expr);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
			return state.smallStepSemantics(new Skip(getLocation()), original);
		}
	}
}
