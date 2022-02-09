package it.unive.golisa.cfg.runtime.strconv;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
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
import it.unive.lisa.symbolic.value.PushAny;

public class GoAtoi extends NativeCFG {

	public GoAtoi(CodeLocation location, CompilationUnit strconvUnit) {
		super(new CFGDescriptor(location, strconvUnit, false, "Atoi", GoIntType.INSTANCE,
				new Parameter(location, "this", GoStringType.INSTANCE)),
				Atoi.class);
	}

	public static class Atoi extends UnaryExpression implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static Atoi build(CFG cfg, CodeLocation location, Expression... params) {
			return new Atoi(cfg, location, params[0]);
		}

		public Atoi(CFG cfg, CodeLocation location, Expression exp1) {
			super(cfg, location, "Atoi", GoIntType.INSTANCE, exp1);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> unarySemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						SymbolicExpression expr, StatementStore<A, H, V, T> expressions) throws SemanticException {
			if (!expr.getDynamicType().isStringType() && !expr.getDynamicType().isUntyped())
				return state.bottom();
			return state.smallStepSemantics(new PushAny(GoIntType.INSTANCE, getLocation()), original);
		}
	}
}
