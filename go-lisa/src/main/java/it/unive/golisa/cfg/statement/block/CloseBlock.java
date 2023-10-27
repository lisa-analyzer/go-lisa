package it.unive.golisa.cfg.statement.block;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.ScopeToken;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;

/**
 * A close block statement.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class CloseBlock extends Statement {

	private OpenBlock open;

	/**
	 * Builds the close of a code block.
	 * 
	 * @param cfg      the cfg that this statement belongs to
	 * @param location the location where this statement is defined within the
	 *                     source file
	 * @param open     the open block with which this close block matches
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((open == null) ? 0 : open.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		CloseBlock other = (CloseBlock) obj;
		if (open == null) {
			if (other.open != null)
				return false;
		} else if (!open.equals(other.open))
			return false;
		return true;
	}

	/**
	 * Semantics of an close block is to restore the values of open block after
	 * the block (e.g., about variable re-declarations).
	 */
	@Override
	public <A extends AbstractState<A>> AnalysisState<A> forwardSemantics(
			AnalysisState<A> entryState, InterproceduralAnalysis<A> interprocedural,
			StatementStore<A> expressions) throws SemanticException {
		// The close block does not compute any symbolic expression, so it
		// returns the empty set just popping the scope on the analysis state
		return new AnalysisState<>(entryState.getState().popScope(new ScopeToken(open)),
				entryState.getComputedExpressions(), entryState.getFixpointInformation());
	}
}
