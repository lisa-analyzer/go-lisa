package it.unive.golisa.cfg.runtime.strings;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
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
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.operator.unary.StringLength;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.collections.externalSet.ExternalSet;

public class GoLen extends NativeCFG {

	public GoLen(CodeLocation location, CompilationUnit stringUnit) {
		super(new CFGDescriptor(location, stringUnit, false, "Len", GoIntType.INSTANCE,
				new Parameter(location, "this", GoStringType.INSTANCE)),
				Len.class);
	}

	public static class Len extends it.unive.lisa.program.cfg.statement.UnaryExpression implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static Len build(CFG cfg, CodeLocation location, Expression... params) {
			return new Len(cfg, location, params[0]);
		}

		public Len(CFG cfg, CodeLocation location, Expression arg) {
			super(cfg, location, "Len", GoIntType.INSTANCE, arg);
		}

		@Override
		protected <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
						InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
						SymbolicExpression expr)
						throws SemanticException {
			ExternalSet<Type> intType = Caches.types().mkSingletonSet(GoIntType.INSTANCE);
			return state.smallStepSemantics(
					new UnaryExpression(intType, expr, StringLength.INSTANCE, getLocation()), original);

		}
	}
}
