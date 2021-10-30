package it.unive.golisa.cfg.runtime.strconv;

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
import it.unive.lisa.program.cfg.statement.call.UnaryNativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;

public class GoItoa extends NativeCFG {

	public GoItoa(CodeLocation location, CompilationUnit strconvUnit) {
		super(new CFGDescriptor(location, strconvUnit, false, "Itoa", GoStringType.INSTANCE,
				new Parameter(location, "this", GoIntType.INSTANCE)),
				Itoa.class);
	}

	public static class Itoa extends UnaryNativeCall implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static Itoa build(CFG cfg, CodeLocation location, Expression... params) {
			return new Itoa(cfg, location, params[0]);
		}

		public Itoa(CFG cfg, CodeLocation location, Expression exp1) {
			super(cfg, location, "Itoa", GoStringType.INSTANCE, exp1);
		}

		@Override
		protected <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
						AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural,
						AnalysisState<A, H, V> exprState, SymbolicExpression expr) throws SemanticException {
			if (!expr.getDynamicType().isNumericType() && !expr.getDynamicType().isUntyped())
				return entryState.bottom();

			return exprState.smallStepSemantics(
					new PushAny(Caches.types().mkSingletonSet(GoStringType.INSTANCE), getLocation()), original);

		}
	}
}
