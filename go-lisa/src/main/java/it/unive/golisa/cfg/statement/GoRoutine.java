package it.unive.golisa.cfg.statement;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;

public class GoRoutine extends Statement {

	private final Call call;

	public GoRoutine(CFG cfg, CodeLocation location, Call call) {
		super(cfg, location);
		this.call = call;
	}

	@Override
	public int setOffset(int offset) {
		this.offset = offset;
		return call.setOffset(offset + 1);
	}

	@Override
	public <V> boolean accept(GraphVisitor<CFG, Statement, Edge, V> visitor, V tool) {
		if (!call.accept(visitor, tool))
			return false;
		return visitor.visit(tool, getCFG(), this);
	}

	@Override
	public String toString() {
		return "go " + call.toString();
	}

	@Override
	public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> semantics(
			AnalysisState<A, H, V, T> entryState, InterproceduralAnalysis<A, H, V, T> interprocedural,
			StatementStore<A, H, V, T> expressions) throws SemanticException {
		// TODO semantics of go routine not supported yet
		return entryState.top();
	}
}
