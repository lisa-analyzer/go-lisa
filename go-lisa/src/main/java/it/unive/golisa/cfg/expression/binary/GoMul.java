package it.unive.golisa.cfg.expression.binary;

import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.BinaryNativeCall;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.BinaryOperator;

/**
 * A Go numerical multiplication function call (e1 * e2).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoMul extends BinaryNativeCall {

	/**
	 * Builds a Go multiplication expression. 
	 * The location where this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp1	left-hand side operand
	 * @param exp2 	right-hand side operand 
	 */
	public GoMul(CFG cfg, Expression exp1, Expression exp2) {
		super(cfg, null, -1, -1, "*", exp1, exp2);
	}

	@Override
	protected <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> binarySemantics(
			AnalysisState<H, V> computedState, CallGraph callGraph, SymbolicExpression left, SymbolicExpression right)
					throws SemanticException {
		if (!left.getDynamicType().isNumericType() && !left.getDynamicType().isUntyped())
			return computedState.bottom();
		if (!right.getDynamicType().isNumericType() && !right.getDynamicType().isUntyped())
			return computedState.bottom();

		return computedState
				.smallStepSemantics(new BinaryExpression(Caches.types().mkSingletonSet(left.getDynamicType()), left, right,
						BinaryOperator.NUMERIC_MUL));
	}
}
