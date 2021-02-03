package it.unive.golisa.cfg.expression.binary;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.BinaryNativeCall;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.BinaryOperator;

/**
 * A Go numerical division function call (e1 / e2).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoDiv extends BinaryNativeCall implements GoBinaryNumericalOperation {
	
	/**
	 * Builds a Go division expression. 
	 * The location where this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp1	left-hand side operand
	 * @param exp2 	right-hand side operand 
	 */
	public GoDiv(CFG cfg, Expression exp1, Expression exp2) {
		super(cfg, null, -1, -1, "/", exp1, exp2);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V> leftState,
			SymbolicExpression leftExp, AnalysisState<A, H, V> rightState, SymbolicExpression rightExp)
			throws SemanticException {
		if (!leftExp.getDynamicType().isNumericType() && !leftExp.getDynamicType().isUntyped())
			return entryState.bottom();
		if (!rightExp.getDynamicType().isNumericType() && !rightExp.getDynamicType().isUntyped())
			return entryState.bottom();

		return rightState
				.smallStepSemantics(new BinaryExpression(resultType(leftExp, rightExp), leftExp, rightExp,
						BinaryOperator.NUMERIC_DIV), this);
	}
}
