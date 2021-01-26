package it.unive.golisa.cfg.expression.binary;

import it.unive.golisa.cfg.type.GoType;
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
import it.unive.lisa.symbolic.value.PushAny;

/**
 * A Go and function function call.
 * e1 & e2 copies a bit to the result if it exists in both operands.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoBitwiseAnd extends BinaryNativeCall implements GoBinaryNumericalOperation {
	
	/**
	 * Builds a Go and expression. 
	 * The location where this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp1	left-hand side operand
	 * @param exp2 	right-hand side operand 
	 */
	public GoBitwiseAnd(CFG cfg, Expression exp1, Expression exp2) {
		super(cfg, null, -1, -1, "&", exp1, exp2);
	}
	
	/**
	 * Builds a Go and expression at a given location in the program.
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
	public GoBitwiseAnd(CFG cfg, String sourceFile, int line, int col, Expression exp1, Expression exp2) {
		super(cfg, sourceFile, line, col, "&", exp1, exp2);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V> leftState,
			SymbolicExpression left, AnalysisState<A, H, V> rightState, SymbolicExpression right)
					throws SemanticException {

		if (!left.getDynamicType().isUntyped() && (left.getDynamicType() instanceof GoType && !((GoType) left.getDynamicType()).isGoInteger()))
			return entryState.bottom();

		if (!right.getDynamicType().isUntyped() && (right.getDynamicType() instanceof GoType && !((GoType) right.getDynamicType()).isGoInteger()))
			return entryState.bottom();
		
		// TODO: LiSA has not symbolic expression handling bitwise, return top at the moment
		return rightState.smallStepSemantics(new PushAny(resultType(left, right)));	
	}
}
