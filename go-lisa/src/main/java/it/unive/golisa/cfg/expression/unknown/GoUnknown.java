package it.unive.golisa.cfg.expression.unknown;

import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;

/**
 * Placeholder for an unknown value.
 * 
 * @author <a href="mailto:luca.olvieri@univr.it">Luca Olivieri</a>
 */
public class GoUnknown extends Expression {

	/**
	*/
	public GoUnknown(CFG cfg, SourceCodeLocation location) {
		super(cfg, location, Untyped.INSTANCE);
	}
	
	public GoUnknown(CFG cfg, SourceCodeLocation location, GoType type) {
		super(cfg, location, type);
	}

	@Override
	public int setOffset(int offset) {
		return this.offset = offset;
	}

	@Override
	public <V> boolean accept(GraphVisitor<CFG, Statement, Edge, V> visitor, V tool) {
		return visitor.visit(tool, getCFG(), this);
	}

	@Override
	public String toString() {
		return "<UNKNOWN>";
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> semantics(
					AnalysisState<A, H, V, T> entryState, InterproceduralAnalysis<A, H, V, T> interprocedural,
					StatementStore<A, H, V, T> expressions) throws SemanticException {
		return entryState.smallStepSemantics(new PushAny(Untyped.INSTANCE, getLocation()), getParentStatement());
	}
}
