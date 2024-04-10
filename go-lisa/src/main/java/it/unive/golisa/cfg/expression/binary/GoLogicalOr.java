package it.unive.golisa.cfg.expression.binary;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.lattices.Satisfiability;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.LogicalOr;
import it.unive.lisa.type.Type;

/**
 * A Go Boolean logical oe expression (e.g., x && y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoLogicalOr extends it.unive.lisa.program.cfg.statement.BinaryExpression {

	/**
	 * Builds the Boolean logical or expression.
	 *
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left-hand side of this expression
	 * @param right    the right-hand side of this expression
	 */
	public GoLogicalOr(CFG cfg, SourceCodeLocation location, Expression left, Expression right) {
		super(cfg, location, "||", GoBoolType.INSTANCE, left, right);
	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(
			InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
			SymbolicExpression left, SymbolicExpression right, StatementStore<A> expressions)
			throws SemanticException {
		// FIXME: need to check which state needs to be returned (left/right)
		Type ltype = state.getState().getDynamicTypeOf(left, this, state.getState());
		Type rtype = state.getState().getDynamicTypeOf(right, this, state.getState());

		if (!ltype.isBooleanType() && !ltype.isUntyped())
			return state.bottom();
		if (!rtype.isBooleanType() && !rtype.isUntyped())
			return state.bottom();

		if (state.satisfies(left, this) == Satisfiability.SATISFIED)
			return state;
		else if (state.satisfies(left, this) == Satisfiability.NOT_SATISFIED)
			return state
					.smallStepSemantics(new BinaryExpression(GoBoolType.INSTANCE,
							left, right, LogicalOr.INSTANCE, getLocation()), this);
		else if (state.satisfies(left, this) == Satisfiability.UNKNOWN)
			return state.lub(state
					.smallStepSemantics(new BinaryExpression(GoBoolType.INSTANCE,
							left, right, LogicalOr.INSTANCE, getLocation()), this));
		else
			return state.bottom();
	}
}