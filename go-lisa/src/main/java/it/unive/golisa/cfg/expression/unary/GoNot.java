package it.unive.golisa.cfg.expression.unary;

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
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.operator.unary.LogicalNegation;
import it.unive.lisa.type.Type;
import java.util.Set;

/**
 * Go unary not Boolean expression (e.g., !(x > y)).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoNot extends it.unive.lisa.program.cfg.statement.UnaryExpression {

	/**
	 * Builds the unary not Boolean expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param exp      the expression
	 */
	public GoNot(CFG cfg, SourceCodeLocation location, Expression exp) {
		super(cfg, location, "!", GoBoolType.INSTANCE, exp);
	}

	@Override
	public <A extends AbstractState<A>> AnalysisState<A> fwdUnarySemantics(InterproceduralAnalysis<A> interprocedural,
			AnalysisState<A> state, SymbolicExpression expr, StatementStore<A> expressions) throws SemanticException {
		Set<Type> etypes = state.getState().getRuntimeTypesOf(expr, this, state.getState());
		AnalysisState<A> result = state.bottom();
		for (Type type : etypes)
			if (type.isBooleanType() || type.isUntyped())
				result = result.lub(state.smallStepSemantics(
						new UnaryExpression(GoBoolType.INSTANCE, expr, LogicalNegation.INSTANCE,
								getLocation()),
						this));
		return result;
	}
}
