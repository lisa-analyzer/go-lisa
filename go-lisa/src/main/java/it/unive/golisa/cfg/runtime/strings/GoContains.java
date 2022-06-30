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
import it.unive.lisa.symbolic.value.operator.binary.StringContains;
import it.unive.lisa.type.Type;

public class GoContains extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location   the location where this native cfg is defined
	 * @param stringUnit the unit to which this native cfg belongs to
	 */
	public GoContains(CodeLocation location, CompilationUnit stringUnit) {
		super(new CFGDescriptor(location, stringUnit, false, "Contains", GoBoolType.INSTANCE,
				new Parameter(location, "this", GoStringType.INSTANCE),
				new Parameter(location, "other", GoStringType.INSTANCE)),
				Contains.class);
	}

	public static class Contains extends it.unive.lisa.program.cfg.statement.BinaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		public static Contains build(CFG cfg, CodeLocation location, Expression... params) {
			return new Contains(cfg, location, params[0], params[1]);
		}

		public Contains(CFG cfg, CodeLocation location, Expression exp1, Expression exp2) {
			super(cfg, location, "Contains", GoBoolType.INSTANCE, exp1, exp2);
		}

		@Override
		protected <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
						throws SemanticException {

			AnalysisState<A, H, V, T> result = state.bottom();
			for (Type leftType : left.getRuntimeTypes())
				for (Type rightType : right.getRuntimeTypes())
					if (!leftType.isStringType() && !leftType.isUntyped())
						continue;
					else if (!rightType.isStringType() && !rightType.isUntyped())
						continue;
					else
						result = result.lub(state
								.smallStepSemantics(new BinaryExpression(GoBoolType.INSTANCE,
										left, right, StringContains.INSTANCE, getLocation()), original));
			return result;
		}
	}
}
