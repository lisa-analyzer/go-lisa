package it.unive.golisa.cfg.expression.binary;

import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.BinaryNativeCall;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.BinaryOperator;
import it.unive.lisa.util.collections.ExternalSet;

/**
 * A Go numerical sum function call (e1 + e2).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoSum extends BinaryNativeCall {

	/**
	 * Builds a Go sum expression. The location where 
	 * this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp1	left-hand side operand
	 * @param exp2 	right-hand side operand 
	 */
	public GoSum(CFG cfg, Expression exp1, Expression exp2) {
		super(cfg, null, -1, -1, "+", exp1, exp2);
	}

	@Override
	protected <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> binarySemantics(
			AnalysisState<H, V> computedState, CallGraph callGraph, SymbolicExpression left, SymbolicExpression right)
			throws SemanticException {
		
		BinaryOperator op;
		ExternalSet<Type> types;
		
		if (left.getDynamicType().isStringType() && right.getDynamicType().isStringType()) {
			op = BinaryOperator.STRING_CONCAT;
			types = Caches.types().mkSingletonSet(GoStringType.INSTANCE);
		} else if ((left.getDynamicType().isNumericType() || left.getDynamicType().isUntyped())
				&& (right.getDynamicType().isNumericType() || right.getDynamicType().isUntyped())) {
			op = BinaryOperator.NUMERIC_ADD;
			types = Caches.types().mkSingletonSet(left.getDynamicType());
		} else
			return computedState.bottom();

		return computedState
				.smallStepSemantics(new BinaryExpression(types, left, right, op));
	}

	
}
