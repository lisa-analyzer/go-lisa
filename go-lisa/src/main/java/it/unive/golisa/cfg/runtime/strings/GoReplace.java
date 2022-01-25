package it.unive.golisa.cfg.runtime.strings;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.GoStringType;
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
import it.unive.lisa.program.cfg.statement.TernaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.operator.ternary.StringReplace;

public class GoReplace extends NativeCFG {

	public GoReplace(CodeLocation location, CompilationUnit stringUnit) {
		super(new CFGDescriptor(location, stringUnit, false, stringUnit.getName() + ".Replace", GoBoolType.INSTANCE,
				new Parameter(location, "this", GoStringType.INSTANCE),
				new Parameter(location, "that", GoIntType.INSTANCE),
				new Parameter(location, "other", GoIntType.INSTANCE)),
				Replace.class);
	}

	public static class Replace extends TernaryExpression implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static Replace build(CFG cfg, CodeLocation location, Expression... params) {
			return new Replace(cfg, location, params[0], params[1], params[2]);
		}

		public Replace(CFG cfg, CodeLocation location, Expression left, Expression middle, Expression right) {
			super(cfg, location, "Replace", left, middle, right);
		}

		@Override
		protected <A extends AbstractState<A, H, V>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>> AnalysisState<A, H, V> ternarySemantics(
						InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
						SymbolicExpression left,
						SymbolicExpression middle, SymbolicExpression right, StatementStore<A, H, V> expressions) throws SemanticException {
			if (!left.getDynamicType().isStringType() && !left.getDynamicType().isUntyped())
				return state.bottom();

			if (!middle.getDynamicType().isStringType() && !middle.getDynamicType().isUntyped())
				return state.bottom();

			if (!right.getDynamicType().isStringType() && !right.getDynamicType().isUntyped())
				return state.bottom();

			return state
					.smallStepSemantics(new it.unive.lisa.symbolic.value.TernaryExpression(
							Caches.types().mkSingletonSet(GoStringType.INSTANCE),
							left, middle, right, StringReplace.INSTANCE, getLocation()), original);

		}
	}
}
