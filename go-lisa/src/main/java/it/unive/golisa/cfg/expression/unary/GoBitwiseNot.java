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
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.Type;

public class GoBitwiseNot extends UnaryExpression {

	public GoBitwiseNot(CFG cfg, SourceCodeLocation location, Expression exp) {
		super(cfg, location, "^", exp.getStaticType(), exp);
	}

	@Override
	protected <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
			InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state, SymbolicExpression expr)
			throws SemanticException {
		Type exprType = expr.getDynamicType();
		if (!exprType.isUntyped() || (exprType.isNumericType() && !exprType.asNumericType().isIntegral()))
			return state.bottom();

		// TODO: LiSA has not symbolic expression handling bitwise, return top
		// at the moment
		return state.smallStepSemantics(
				new PushAny(Caches.types().mkSingletonSet(expr.getDynamicType()), getLocation()), this);
	
	}

}
