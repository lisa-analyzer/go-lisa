package it.unive.golisa.cfg.expression.ternary;

import java.util.Set;

import it.unive.golisa.cfg.type.GoStringType;
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
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.operator.ternary.StringSubstring;
import it.unive.lisa.type.Type;

/**
 * A Go slice expression (e.g., s[1:5]).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoSimpleSlice extends it.unive.lisa.program.cfg.statement.TernaryExpression {
	/**
	 * Builds a Go slice expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left expression
	 * @param middle   the middle expression
	 * @param right    the right expression
	 */
	public GoSimpleSlice(CFG cfg, SourceCodeLocation location, Expression left, Expression middle, Expression right) {
		super(cfg, location, "slice", left, middle, right);
	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdTernarySemantics(InterproceduralAnalysis<A> interprocedural,
			AnalysisState<A> state, SymbolicExpression left, SymbolicExpression middle, SymbolicExpression right,
			StatementStore<A> expressions) throws SemanticException {
		Set<Type> ltypes = state.getState().getRuntimeTypesOf(left, this, state.getState());
		Set<Type> mtypes = state.getState().getRuntimeTypesOf(middle, this, state.getState());
		Set<Type> rtypes = state.getState().getRuntimeTypesOf(right, this, state.getState());

		AnalysisState<A> result = state.bottom();
		for (Type leftType : ltypes)
			for (Type middleType : mtypes)
				for (Type rightType : rtypes)
					if ((leftType.isStringType() || leftType.isUntyped())
							&& (middleType.isNumericType() || middleType.isUntyped())
							&& (rightType.isNumericType() || rightType.isUntyped())) {
						AnalysisState<A> tmp = state.smallStepSemantics(
								new TernaryExpression(GoStringType.INSTANCE,
										left, middle, right, StringSubstring.INSTANCE, getLocation()),
								this);
						result = result.lub(tmp);
					}
		return result;
	}
}
