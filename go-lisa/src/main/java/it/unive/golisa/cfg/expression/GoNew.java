package it.unive.golisa.cfg.expression;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.HeapAllocation;
import it.unive.lisa.type.Type;

public class GoNew extends NaryExpression {

	public GoNew(CFG cfg, SourceCodeLocation location, Type type) {
		super(cfg, location, "new", type);
	}

	@Override
	public <A extends AbstractState<A, H, V>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>> AnalysisState<A, H, V> expressionSemantics(
					InterproceduralAnalysis<A, H, V> interprocedural, AnalysisState<A, H, V> state,
					ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V> expressions)
					throws SemanticException {
		// Following the Golang reference:
		// The new built-in function allocates memory. The first argument is a
		// type, not a value,
		// and the value returned is a pointer to a newly allocated zero value
		// of that type.
		HeapAllocation created = new HeapAllocation(Caches.types().mkSingletonSet(getStaticType()), getLocation());
		return state.smallStepSemantics(created, this);
	}
}
