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
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingSub;
import it.unive.lisa.type.Type;

/**
 * A Go numerical subtraction function call (e1 - e2).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoSubtraction extends it.unive.lisa.program.cfg.statement.BinaryExpression
implements GoBinaryNumericalOperation {

	public GoSubtraction(CFG cfg, SourceCodeLocation location, Expression exp1, Expression exp2) {
		super(cfg, location, "-", exp1, exp2);
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
			for (Type rightType : right.getRuntimeTypes()) {
				if (leftType.isNumericType() || rightType.isNumericType()) 
					result = result.lub(state.smallStepSemantics(
							new BinaryExpression(resultType(leftType, rightType), left, right, NumericNonOverflowingSub.INSTANCE, getLocation()), this));
			}

		return result;
	}
}
