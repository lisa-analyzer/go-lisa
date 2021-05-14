package it.unive.golisa.cfg.expression.literal;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;

public class GoExpressionsTuple extends Expression {
	
	private final Expression[] expressions;
	
	public GoExpressionsTuple(CFG cfg, CodeLocation location, Expression[] expressions) {
		super(cfg, location);
		this.expressions =  expressions;
	}

	public Expression[] getExpressions() {
		return expressions;
	}
	
	@Override
	public int setOffset(int offset) {
		this.offset = offset;
		int off = offset;
		for (int i = 0; i < expressions.length; i++)
			off = expressions[i].setOffset(off + 1);
		return off;
	}

	@Override
	public <V> boolean accept(GraphVisitor<CFG, Statement, Edge, V> visitor, V tool) {
		for (int i = 0; i < expressions.length; i++)
			if (!expressions[i].accept(visitor, tool))
				return false;
		return visitor.visit(tool, getCFG(), this);
	}

	@Override
	public String toString() {
		return expressions.toString();
	}

	@Override
	public <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> semantics(
			AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural, StatementStore<A, H, V> expressions)
			throws SemanticException {
		// TODO: need to evaluate a tuple of expressions
		return entryState.top();
	}
}
