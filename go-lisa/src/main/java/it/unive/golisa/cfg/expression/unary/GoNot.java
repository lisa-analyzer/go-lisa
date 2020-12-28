package it.unive.golisa.cfg.expression.unary;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.UnaryNativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.UnaryOperator;

/**
 * Go unary not native function class (e.g., !(x > y)).
 * The static type of this expression is definite {@link GoBoolType}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoNot extends UnaryNativeCall {
	
	/**
	 * Builds a Go unary not expression. The location where 
	 * this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp	operand
	 */
	public GoNot(CFG cfg, Expression exp) {
		super(cfg, null, -1, -1, "!", GoBoolType.INSTANCE, exp);
	}

	@Override
	protected <H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<H, V> unarySemantics(
			AnalysisState<H, V> computedState, CallGraph callGraph, SymbolicExpression expr) throws SemanticException {
		
		if (!expr.getDynamicType().isBooleanType() && !expr.getDynamicType().isUntyped())
			return computedState.bottom();

		return computedState.smallStepSemantics(
				new UnaryExpression(Caches.types().mkSingletonSet(GoBoolType.INSTANCE), expr, UnaryOperator.LOGICAL_NOT));
	}

}
