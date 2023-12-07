package it.unive.golisa.cfg.expression.binary;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonNe;
import it.unive.lisa.type.Type;
import java.util.Set;

/**
 * A Go equal expression (e.g., x == y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoNotEqual extends it.unive.lisa.program.cfg.statement.BinaryExpression {

	/**
	 * Builds the not equal expression.
	 *
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left-hand side of this expression
	 * @param right    the right-hand side of this expression
	 */
	public GoNotEqual(CFG cfg, SourceCodeLocation location, Expression left, Expression right) {
		super(cfg, location, "!=", GoBoolType.INSTANCE, left, right);
	}
	
	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(InterproceduralAnalysis<A> arg0,
			AnalysisState<A> state, SymbolicExpression left, SymbolicExpression right, StatementStore<A> arg4)
			throws SemanticException {
		if (right.getStaticType().canBeAssignedTo(left.getStaticType())
				|| left.getStaticType().canBeAssignedTo(right.getStaticType()))
			return state
					.smallStepSemantics(new BinaryExpression(GoBoolType.INSTANCE,
							left, right,
							ComparisonNe.INSTANCE, getLocation()), this);

		Set<Type> ltypes = state.getState().getRuntimeTypesOf(left, this, state.getState());
		Set<Type> rtypes = state.getState().getRuntimeTypesOf(right, this, state.getState());

		// TODO: composite types are not covered yey
		AnalysisState<A> result = state.bottom();
		for (Type lType : ltypes)
			for (Type rType : rtypes)
				if (rType.canBeAssignedTo(lType) || lType.canBeAssignedTo(rType))
					result = result.lub(state
							.smallStepSemantics(new BinaryExpression(GoBoolType.INSTANCE,
									left, right,
									ComparisonNe.INSTANCE, getLocation()), this));
		return result;

	}
}
