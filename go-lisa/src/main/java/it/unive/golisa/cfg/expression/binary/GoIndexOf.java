package it.unive.golisa.cfg.expression.binary;

import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.BinaryNativeCall;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.BinaryOperator;

public class GoIndexOf extends BinaryNativeCall{

	/**
	 * Builds a Go indexOf expression. 
	 * The location where this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp1	left-hand side operand
	 * @param exp2 	right-hand side operand 
	 */
	public GoIndexOf(CFG cfg, Expression exp1, Expression exp2) {
		this(cfg, null, -1, -1, exp1, exp2);
	}

	/**
	 * Builds a Go indexOf expression at a given location in the program.
	 * 
	 * @param cfg           the cfg that this expression belongs to
	 * @param sourceFile    the source file where this expression happens. If
	 *                      unknown, use {@code null}
	 * @param line          the line number where this expression happens in the
	 *                      source file. If unknown, use {@code -1}
	 * @param col           the column where this expression happens in the source
	 *                      file. If unknown, use {@code -1}
	 * @param exp1		    left-hand side operand
	 * @param exp2		    right-hand side operand
	 */
	public GoIndexOf(CFG cfg, String sourceFile, int line, int col, Expression exp1, Expression exp2) {
		super(cfg, sourceFile, line, col, "strings.Index", GoIntType.INSTANCE, exp1, exp2);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V> leftState,
			SymbolicExpression leftExp, AnalysisState<A, H, V> rightState, SymbolicExpression rightExp)
					throws SemanticException {
		if (!leftExp.getDynamicType().isStringType() && !leftExp.getDynamicType().isUntyped())
			return entryState.bottom();

		if (!rightExp.getDynamicType().isStringType() && !rightExp.getDynamicType().isUntyped())
			return entryState.bottom();
		
		return rightState.smallStepSemantics(new BinaryExpression(Caches.types().mkSingletonSet(GoIntType.INSTANCE), leftExp, rightExp, BinaryOperator.STRING_INDEX_OF));
	}
}
