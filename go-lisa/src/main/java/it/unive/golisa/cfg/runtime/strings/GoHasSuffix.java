package it.unive.golisa.cfg.runtime.strings;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.golisa.cfg.type.GoStringType;
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
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.StringEndsWith;

public class GoHasSuffix extends NativeCFG {

	public GoHasSuffix(CodeLocation location, CompilationUnit stringUnit) {
		super(new CFGDescriptor(location, stringUnit, false, "HasSuffix", GoBoolType.INSTANCE,
				new Parameter(location, "this", GoStringType.INSTANCE),
				new Parameter(location, "other", GoStringType.INSTANCE)),
				HasSuffix.class);
	}

	public static class HasSuffix extends it.unive.lisa.program.cfg.statement.BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static HasSuffix build(CFG cfg, CodeLocation location, Expression... params) {
			return new HasSuffix(cfg, location, params[0], params[1]);
		}

		public HasSuffix(CFG cfg, CodeLocation location, Expression exp1, Expression exp2) {
			super(cfg, location, "HasSuffix", GoBoolType.INSTANCE, exp1, exp2);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
				throws SemanticException {
			if (!left.getDynamicType().isStringType() && !left.getDynamicType().isUntyped())
				return state.bottom();

			if (!right.getDynamicType().isStringType() && !right.getDynamicType().isUntyped())
				return state.bottom();

			return state
					.smallStepSemantics(new BinaryExpression(GoBoolType.INSTANCE,
							left, right, StringEndsWith.INSTANCE, getLocation()), original);

	
		}
	}
}
