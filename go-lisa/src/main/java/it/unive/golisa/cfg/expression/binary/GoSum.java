package it.unive.golisa.cfg.expression.binary;

import java.util.Set;

import it.unive.golisa.cfg.type.GoStringType;
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
import it.unive.lisa.symbolic.value.operator.binary.BinaryOperator;
import it.unive.lisa.symbolic.value.operator.binary.NumericNonOverflowingAdd;
import it.unive.lisa.symbolic.value.operator.binary.StringConcat;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * A Go numerical sum expression (e.g., x + y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoSum extends it.unive.lisa.program.cfg.statement.BinaryExpression implements GoBinaryNumericalOperation {

	/**
	 * Builds the sum expression.
	 *
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left-hand side of this expression
	 * @param right    the right-hand side of this expression
	 */
	public GoSum(CFG cfg, SourceCodeLocation location, Expression left, Expression right) {
		super(cfg, location, "+", left, right);
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
					InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
					SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
					throws SemanticException {
		BinaryOperator op;
		Type type;
		TypeSystem types = getProgram().getTypes();

		AnalysisState<A, H, V, T> result = state.bottom();

		if (left.getStaticType().isNumericType() && right.getStaticType().isNumericType()) {
			op = NumericNonOverflowingAdd.INSTANCE;
			type = resultType(left.getStaticType(), right.getStaticType());
			result = state.smallStepSemantics(new BinaryExpression(type, left, right, op, getLocation()), this);
		} else if (left.getStaticType().isStringType() && right.getStaticType().isStringType()) {
			op = StringConcat.INSTANCE;
			type = GoStringType.INSTANCE;
			result = state.smallStepSemantics(new BinaryExpression(type, left, right, op, getLocation()), this);
		} else {
			boolean found = false;
			for (Type leftType : left.getRuntimeTypes(types)) {
				for (Type rightType : right.getRuntimeTypes(types)) {
					if (leftType.isStringType() && rightType.isStringType()) {
						op = StringConcat.INSTANCE;
						type = GoStringType.INSTANCE;
						found = true;
					} else if (leftType.isNumericType() || rightType.isNumericType()) {
						op = NumericNonOverflowingAdd.INSTANCE;
						type = resultType(leftType, rightType);
						found = true;
					} else
						continue;
					result = result.lub(state.smallStepSemantics(
							new BinaryExpression(type, left, right, op, getLocation()), this));
				}
			}
			if(found)
				return result;
		}
		return state.smallStepSemantics(
				new BinaryExpression(Untyped.INSTANCE, left, right, new BinaryOperator() {
					
					@Override
					public Set<Type> typeInference(TypeSystem types, Set<Type> left, Set<Type> right) {
						return Set.of(Untyped.INSTANCE);
					}
				}, getLocation()), this);
	}
}