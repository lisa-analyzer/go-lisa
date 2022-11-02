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
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingMul;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;

/**
 * A Go multiplication expression (e.g., x * y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoMul extends it.unive.lisa.program.cfg.statement.BinaryExpression implements GoBinaryNumericalOperation {

	/**
	 * Builds the multiplication expression.
	 *
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left-hand side of this expression
	 * @param right    the right-hand side of this expression
	 */
	public GoMul(CFG cfg, SourceCodeLocation location, Expression left, Expression right) {
		super(cfg, location, "*", left, right);
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
					SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
					throws SemanticException {
		TypeSystem types = getProgram().getTypes();

		AnalysisState<A, H, V, T> result = state.bottom();
		for (Type leftType : left.getRuntimeTypes(types))
			for (Type rightType : right.getRuntimeTypes(types))
				if (leftType.isNumericType() && rightType.isNumericType())
					result = result.lub(
							state.smallStepSemantics(new BinaryExpression(resultType(leftType, rightType), left, right,
									NumericNonOverflowingMul.INSTANCE, getLocation()), this));
		return result;
	}
}
