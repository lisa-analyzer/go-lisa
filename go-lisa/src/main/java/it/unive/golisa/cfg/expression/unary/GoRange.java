package it.unive.golisa.cfg.expression.unary;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;

public class GoRange extends UnaryExpression {

	public GoRange(CFG cfg, SourceCodeLocation location, Expression exp) {
		super(cfg, location, "range", GoBoolType.INSTANCE, exp);
	}

	@Override
	protected <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> unarySemantics(
					InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
					SymbolicExpression expr, StatementStore<A, H, V> expressions)
					throws SemanticException {
		return state.smallStepSemantics(expr, this);

	}
}
