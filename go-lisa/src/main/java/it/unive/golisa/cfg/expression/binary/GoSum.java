package it.unive.golisa.cfg.expression.binary;

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
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingAdd;
import it.unive.lisa.symbolic.value.operator.binary.StringConcat;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.collections.externalSet.ExternalSet;

/**
 * A Go numerical sum function call (e1 + e2).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoSum extends it.unive.lisa.program.cfg.statement.BinaryExpression implements GoBinaryNumericalOperation {

	public GoSum(CFG cfg, SourceCodeLocation location, Expression exp1, Expression exp2) {
		super(cfg, location, "+", exp1, exp2);
	}

	@Override
	protected <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> binarySemantics(
					InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
					SymbolicExpression left,
					SymbolicExpression right) throws SemanticException {
		BinaryOperator op;
		ExternalSet<Type> types;

		AnalysisState<A, H, V> result = state.bottom();

		for (Type leftType : left.getTypes())
			for (Type rightType : right.getTypes()) {
				if (leftType.isStringType() && rightType.isStringType()) {
					op = StringConcat.INSTANCE;
					types = Caches.types().mkSingletonSet(GoStringType.INSTANCE);
				} else if (leftType.isNumericType() || rightType.isNumericType()) {
					op = NumericNonOverflowingAdd.INSTANCE;
					types = resultType(left, right);
				} else
					continue;

				result = result.lub(state.smallStepSemantics(
						new BinaryExpression(types, left, right, op, getLocation()), this));
			}

		return result;
	}
}
