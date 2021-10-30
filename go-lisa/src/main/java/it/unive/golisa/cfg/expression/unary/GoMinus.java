package it.unive.golisa.cfg.expression.unary;

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
import it.unive.lisa.program.cfg.statement.call.UnaryNativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.UnaryOperator;

/**
 * Go unary minus native function class (e.g., -(5 + 3), -5).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoMinus extends UnaryNativeCall {

	public GoMinus(CFG cfg, SourceCodeLocation location, Expression exp) {
		super(cfg, location, "-", exp);
	}

	@Override
	protected <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
					AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural,
					AnalysisState<A, H, V> exprState,
					SymbolicExpression expr) throws SemanticException {
		if (!expr.getDynamicType().isNumericType() && !expr.getDynamicType().isUntyped())
			return entryState.bottom();

		return exprState.smallStepSemantics(
				new UnaryExpression(Caches.types().mkSingletonSet(expr.getDynamicType()), expr,
						UnaryOperator.NUMERIC_NEG, getLocation()),
				this);
	}
}
