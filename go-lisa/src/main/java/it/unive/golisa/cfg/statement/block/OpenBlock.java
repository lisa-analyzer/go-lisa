package it.unive.golisa.cfg.statement.block;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;

public class OpenBlock extends Statement {

	/**
	 * Builds the open of a code block
	 * 
	 * @param cfg        the cfg that this statement belongs to
	 * @param location   the location where this statement is defined within the
	 *                       source file. If unknown, use {@code null}
	 */
	public OpenBlock(CFG cfg, CodeLocation location) {
		super(cfg, location);
		
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
		
		return "Open block: " + getLocation();
	}

	/**
	 * Semantics of an open block is to save the values before the block,
	 * in order to re-store the previous values after the block in case of the variable re-declaration inside the block.
	 */
	@Override
	public <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> semantics(
			AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural,
			StatementStore<A, H, V> expressions) throws SemanticException {
		
		AnalysisState<A, H, V> scope = entryState.pushScope(new ScopeToken(this));
		scope = scope.lub(entryState);		
		return scope;
	}

}