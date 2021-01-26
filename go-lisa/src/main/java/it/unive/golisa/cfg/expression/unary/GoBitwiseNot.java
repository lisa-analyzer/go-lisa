package it.unive.golisa.cfg.expression.unary;

import it.unive.golisa.cfg.type.GoType;
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

public class GoBitwiseNot extends UnaryNativeCall {

	public GoBitwiseNot(CFG cfg, Expression exp) {
		super(cfg, null, -1, -1, "^", exp.getStaticType(), exp);
	}
	
	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
			AnalysisState<A, H, V> entryState, CallGraph callGraph, AnalysisState<A, H, V> exprState,
			SymbolicExpression expr) throws SemanticException {
		
		
		if (!expr.getDynamicType().isUntyped() && (expr.getDynamicType() instanceof GoType && !((GoType) expr.getDynamicType()).isGoInteger()))
			return entryState.bottom();
		
		// TODO: LiSA has not symbolic expression handling bitwise, return top at the moment
		return exprState.smallStepSemantics(new PushAny(Caches.types().mkSingletonSet(expr.getDynamicType())));	
	}

}
