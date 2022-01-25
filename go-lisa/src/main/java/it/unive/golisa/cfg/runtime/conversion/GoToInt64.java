package it.unive.golisa.cfg.runtime.conversion;

import it.unive.golisa.cfg.type.numeric.signed.GoInt64Type;
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
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.operator.binary.TypeConv;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeTokenType;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.collections.externalSet.ExternalSet;

public class GoToInt64 extends NativeCFG {

	public GoToInt64(CodeLocation location, CompilationUnit pkgUnit) {
		super(new CFGDescriptor(location, pkgUnit, false, "int64", GoInt64Type.INSTANCE,
				new Parameter(location, "this", Untyped.INSTANCE)),
				ToInt64.class);
	}

	public static class ToInt64 extends UnaryExpression implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static ToInt64 build(CFG cfg, CodeLocation location, Expression... params) {
			return new ToInt64(cfg, location, params[0]);
		}

		public ToInt64(CFG cfg, CodeLocation location, Expression arg) {
			super(cfg, location, "int64", GoInt64Type.INSTANCE, arg);
		}

		@Override
		protected <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
						InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
						SymbolicExpression expr, StatementStore<A, H, V> expressions)
						throws SemanticException {
			ExternalSet<Type> castType = Caches.types().mkSingletonSet(GoInt64Type.INSTANCE);
			Constant typeCast = new Constant(new TypeTokenType(castType), GoInt64Type.INSTANCE, getLocation());
			return state.smallStepSemantics(
					new BinaryExpression(castType, expr, typeCast, TypeConv.INSTANCE, original.getLocation()),
					original);
		}
	}
}
