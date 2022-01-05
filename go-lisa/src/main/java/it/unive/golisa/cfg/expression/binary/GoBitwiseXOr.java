package it.unive.golisa.cfg.expression.binary;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.call.BinaryNativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.Type;

/**
 * A Go XOR function call. e1 ^ e2 copies the bit if it is set in one operand
 * but not both.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoBitwiseXOr extends BinaryNativeCall implements GoBinaryNumericalOperation {

	/**
	 * Builds a Go XOR expression at a given location in the program.
	 * 
	 * @param cfg        the cfg that this expression belongs to
	 * @param sourceFile the source file where this expression happens. If
	 *                       unknown, use {@code null}
	 * @param line       the line number where this expression happens in the
	 *                       source file. If unknown, use {@code -1}
	 * @param col        the column where this expression happens in the source
	 *                       file. If unknown, use {@code -1}
	 * @param exp1       left-hand side operand
	 * @param exp2       right-hand side operand
	 */
	public GoBitwiseXOr(CFG cfg, SourceCodeLocation location, Expression exp1, Expression exp2) {
		super(cfg, location, "^", exp1, exp2);
	}

	@Override
	protected <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
					AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural,
					AnalysisState<A, H, V> leftState,
					SymbolicExpression left, AnalysisState<A, H, V> rightState, SymbolicExpression right)
					throws SemanticException {

		AnalysisState<A, H, V> result = entryState.bottom();
		for (Type leftType : left.getTypes())
			for (Type rightType : right.getTypes())
				if ((leftType.isUntyped() || (leftType.isNumericType() && leftType.asNumericType().isIntegral())) &&
						(rightType.isUntyped()
								|| (rightType.isNumericType() && rightType.asNumericType().isIntegral()))) {
					// TODO: LiSA has not symbolic expression handling bitwise,
					// return top at the moment
					AnalysisState<A, H, V> tmp = rightState
							.smallStepSemantics(new PushAny(resultType(left, right), getLocation()), this);
					result = result.lub(tmp);
				}
		return result;
	}
}
