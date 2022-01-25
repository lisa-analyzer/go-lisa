package it.unive.golisa.cfg.expression.unary;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.operator.unary.LogicalNegation;

/**
 * Go unary not native function class (e.g., !(x > y)). The static type of this
 * expression is definite {@link GoBoolType} and its operand must be instance of
 * {@link GoBoolType}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoNot extends it.unive.lisa.program.cfg.statement.UnaryExpression {

	public GoNot(CFG cfg, SourceCodeLocation location, Expression exp) {
		super(cfg, location, "!", GoBoolType.INSTANCE, exp);
	}

	@Override
	protected <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
					InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
					SymbolicExpression expr, StatementStore<A, H, V> expressions)
					throws SemanticException {

		if (!expr.getDynamicType().isBooleanType() && !expr.getDynamicType().isUntyped())
			return state.bottom();

		return state.smallStepSemantics(
				new UnaryExpression(Caches.types().mkSingletonSet(GoBoolType.INSTANCE), expr, LogicalNegation.INSTANCE,
						getLocation()),
				this);
	}

}
