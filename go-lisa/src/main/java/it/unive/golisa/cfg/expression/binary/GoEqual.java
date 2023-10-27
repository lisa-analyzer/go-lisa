package it.unive.golisa.cfg.expression.binary;

import java.util.Set;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonEq;
import it.unive.lisa.type.Type;

/**
 * A Go equal expression (e.g., x == y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoEqual extends it.unive.lisa.program.cfg.statement.BinaryExpression {

	/**
	 * Builds the equal expression.
	 *
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left-hand side of this expression
	 * @param right    the right-hand side of this expression
	 */
	public GoEqual(CFG cfg, SourceCodeLocation location, Expression left, Expression right) {
		super(cfg, location, "==", GoBoolType.INSTANCE, left, right);
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(InterproceduralAnalysis<A> interprocedural,
			AnalysisState<A> state, SymbolicExpression left, SymbolicExpression right, StatementStore<A> expressions)
			throws SemanticException {
		// FIXME: this should be removed eventually
		if (left.getStaticType().canBeAssignedTo(right.getStaticType())
				|| right.getStaticType().canBeAssignedTo(left.getStaticType()))
			return state
					.smallStepSemantics(
							new BinaryExpression(GoBoolType.INSTANCE,
									left, right, ComparisonEq.INSTANCE, getLocation()),
							this);
		AnalysisState<A> result = state.bottom();
		Set<Type> ltypes = state.getState().getRuntimeTypesOf(left, this, state.getState());
		Set<Type> rtypes = state.getState().getRuntimeTypesOf(right, this, state.getState());

		for (Type leftType : ltypes)
			for (Type rightType : rtypes)

				if (rightType.canBeAssignedTo(leftType) || leftType.canBeAssignedTo(rightType)) {
					// TODO: not covering composite types (e.g., channels,
					// arrays, structs...)
					AnalysisState<A> tmp = state
							.smallStepSemantics(new BinaryExpression(GoBoolType.INSTANCE,
									left, right,
									ComparisonEq.INSTANCE, getLocation()), this);
					result = result.lub(tmp);
				}
		return result;
	}
}
