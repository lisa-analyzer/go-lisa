package it.unive.golisa.cfg.statement.block;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;

public class CloseBlock extends Statement {

	private OpenBlock open;

	/**
	 * Builds the close of a code block
	 * 
	 * @param cfg      the cfg that this statement belongs to
	 * @param location the location where this statement is defined within the
	 *                     source file. If unknown, use {@code null}
	 */
	public CloseBlock(CFG cfg, CodeLocation location, OpenBlock open) {
		super(cfg, location);
		this.open = open;

	}

	@Override
	public int setOffset(int offset) {
		return this.offset = offset;
	}

	@Override
	public <V> boolean accept(GraphVisitor<CFG, Statement, Edge, V> visitor, V tool) {
		return true;
	}

	@Override
	public String toString() {

		return "Close block: " + open.getLocation();
	}

	/**
	 * Semantics of an close block is to restore the values of open block after
	 * the block (ex. about variable re-declarations)
	 */
	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> semantics(
					AnalysisState<A, H, V, T> entryState, InterproceduralAnalysis<A, H, V, T> interprocedural,
					StatementStore<A, H, V, T> expressions) throws SemanticException {
		// The close block does not compute any symbolic expression, so it
		// returns the empty set
		// just popping the scope on the analysis state
		return new AnalysisState<A, H, V, T>(entryState.getState().popScope(new ScopeToken(open)),
				new ExpressionSet<>());
	}
}
