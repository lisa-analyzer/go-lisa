package it.unive.golisa.cfg.expression.ternary;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.operator.ternary.StringSubstring;
import it.unive.lisa.type.Type;

public class GoSimpleSlice extends it.unive.lisa.program.cfg.statement.TernaryExpression {

	public GoSimpleSlice(CFG cfg, SourceCodeLocation location, Expression left, Expression middle, Expression right) {
		super(cfg, location, "slice", left, middle, right);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> ternarySemantics(
			InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state, SymbolicExpression left,
			SymbolicExpression middle, SymbolicExpression right) throws SemanticException {
		AnalysisState<A, H, V> result = state.bottom();
		for (Type leftType : left.getTypes())
			for (Type middleType : middle.getTypes())
				for (Type rightType : right.getTypes())
					if ((leftType.isStringType() || leftType.isUntyped())
							&& (middleType.isNumericType() || middleType.isUntyped())
							&& (rightType.isNumericType() || rightType.isUntyped())) {
						AnalysisState<A, H,
								V> tmp = state.smallStepSemantics(
										new TernaryExpression(Caches.types().mkSingletonSet(GoStringType.INSTANCE),
												left, middle, right, StringSubstring.INSTANCE, getLocation()),
										this);
						result = result.lub(tmp);
					}
		return result;
	}
}
