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
import it.unive.lisa.program.cfg.statement.call.TernaryNativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.TernaryOperator;
import it.unive.lisa.type.Type;

public class GoSimpleSlice extends TernaryNativeCall {

	public GoSimpleSlice(CFG cfg, SourceCodeLocation location, Expression left, Expression middle, Expression right) {
		super(cfg, location, "slice", left, middle, right);
	}

	@Override
	protected <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> ternarySemantics(
					AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural,
					AnalysisState<A, H, V> leftState, SymbolicExpression left,
					AnalysisState<A, H, V> middleState, SymbolicExpression middle,
					AnalysisState<A, H, V> rightState, SymbolicExpression right) throws SemanticException {

		AnalysisState<A, H, V> result = entryState.bottom();
		for (Type leftType : left.getTypes())
			for (Type middleType : middle.getTypes())
				for (Type rightType : right.getTypes())
					if ((leftType.isStringType() || leftType.isUntyped())
							&& (middleType.isNumericType() || middleType.isUntyped())
							&& (rightType.isNumericType() || rightType.isUntyped())) {
						AnalysisState<A, H,
								V> tmp = rightState.smallStepSemantics(
										new TernaryExpression(Caches.types().mkSingletonSet(GoStringType.INSTANCE),
												left, middle, right, TernaryOperator.STRING_SUBSTRING, getLocation()),
										this);
						result = result.lub(tmp);
					}
		return result;
//		
//		if (!left.getDynamicType().isStringType() && ! left.getDynamicType().isUntyped())
//			return entryState.bottom();
//
//		if (!middle.getDynamicType().isNumericType() && ! middle.getDynamicType().isUntyped())
//			return entryState.bottom();
//
//		if (!right.getDynamicType().isNumericType() && ! right.getDynamicType().isUntyped())
//			return entryState.bottom();

	}
}
