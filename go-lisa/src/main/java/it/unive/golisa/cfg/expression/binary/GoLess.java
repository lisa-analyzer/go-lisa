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
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.BinaryExpression;
import it.unive.lisa.symbolic.value.operator.binary.ComparisonLt;
import it.unive.lisa.type.Type;

/**
 * A Go less than expression (e.g., x < y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoLess extends it.unive.lisa.program.cfg.statement.BinaryExpression {

	/**
	 * Builds the less than expression.
	 *
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left-hand side of this expression
	 * @param right    the right-hand side of this expression
	 */
	public GoLess(CFG cfg, SourceCodeLocation location, Expression left, Expression right) {
		super(cfg, location, "<", GoBoolType.INSTANCE, left, right);
	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdBinarySemantics(InterproceduralAnalysis<A> arg0,
			AnalysisState<A> state, SymbolicExpression left, SymbolicExpression right, StatementStore<A> arg4)
			throws SemanticException {
		// TODO: only, integer, floating point values, strings are
		// ordered but missing lexicographical string order in LiSA
		Set<Type> ltypes = state.getState().getRuntimeTypesOf(left, this, state.getState());
		Set<Type> rtypes = state.getState().getRuntimeTypesOf(right, this, state.getState());

		AnalysisState<A> result = state.bottom();
		for (Type lType : ltypes)
			for (Type rType : rtypes) {
				if (lType.canBeAssignedTo(rType) || rType.canBeAssignedTo(lType))
					result = result.lub(state
							.smallStepSemantics(
									new BinaryExpression(GoBoolType.INSTANCE,
											left, right, ComparisonLt.INSTANCE, getLocation()),
									this));
			}
		return result;
	}
}
