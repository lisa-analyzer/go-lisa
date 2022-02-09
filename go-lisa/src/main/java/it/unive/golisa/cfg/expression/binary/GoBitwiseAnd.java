package it.unive.golisa.cfg.expression.binary;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.Type;

/**
 * A Go and function function call. e1 & e2 copies a bit to the result if it
 * exists in both operands.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoBitwiseAnd extends BinaryExpression implements GoBinaryNumericalOperation {

	/**
	 * Builds a Go and expression at a given location in the program.
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
	public GoBitwiseAnd(CFG cfg, SourceCodeLocation location, Expression exp1, Expression exp2) {
		super(cfg, location, "&", exp1, exp2);
	}

	@Override
	protected <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
			InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
			SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
					throws SemanticException {
		AnalysisState<A, H, V, T> result = state.bottom();
		for (Type leftType : left.getRuntimeTypes())
			for (Type rightType : right.getRuntimeTypes())
				if ((leftType.isUntyped() || (leftType.isNumericType() && leftType.asNumericType().isIntegral())) &&
						(rightType.isUntyped()
								|| (rightType.isNumericType() && rightType.asNumericType().isIntegral()))) {
					// TODO: LiSA has not symbolic expression handling bitwise,
					// return top at the moment
					AnalysisState<A, H, V, T> tmp = state
							.smallStepSemantics(new PushAny(resultType(leftType, rightType), getLocation()), this);
					result = result.lub(tmp);
				}
		return result;
	}
}
