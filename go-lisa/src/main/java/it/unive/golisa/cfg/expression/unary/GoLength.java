package it.unive.golisa.cfg.expression.unary;

import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.HeapDomain;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.callgraph.CallGraph;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.UnaryNativeCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.UnaryExpression;
import it.unive.lisa.symbolic.value.UnaryOperator;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.collections.ExternalSet;

public class GoLength extends UnaryNativeCall {

	public GoLength(CFG cfg, Expression exp) {
		super(cfg, null, -1, -1, "len", exp);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V> exprState,
			SymbolicExpression expr) throws SemanticException {
	
		
		if (expr.getDynamicType().isArrayType() || expr.getDynamicType() instanceof GoSliceType) {
			ExternalSet<Type> type = Caches.types().mkSingletonSet(GoIntType.INSTANCE);
			return exprState.smallStepSemantics(new PushAny(type));
		}
				
		if (!expr.getDynamicType().isStringType() && !expr.getDynamicType().isUntyped())
			return entryState.bottom();
		
		
		return exprState.smallStepSemantics(new UnaryExpression(Caches.types().mkSingletonSet(GoIntType.INSTANCE), expr, UnaryOperator.STRING_LENGTH));
	}
}
