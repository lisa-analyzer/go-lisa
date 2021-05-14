package it.unive.golisa.cfg.statement.assignment;

import java.util.Arrays;

import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.caches.Caches;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.collections.externalSet.ExternalSet;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;

public class GoMultiShortVariableDeclaration extends Expression {

	private final VariableRef[] ids;
	private final Expression e;
	
	public GoMultiShortVariableDeclaration(CFG cfg, String filePath, int line, int col, Type staticType, VariableRef[]  ids, Expression e) {
		super(cfg, new SourceCodeLocation(filePath, line, col), staticType);
		this.ids = ids;
		this.e = e;
	}
	
	@Override
	public int setOffset(int offset) {
		this.offset = offset;
		ids[0].setOffset(offset + 1);
		for (int i = 1; i < ids.length; i++)
			ids[i].setOffset(ids[i-1].getOffset() +1);
		return e.setOffset(ids[ids.length -1].getOffset() +1);
	}
	
	@Override
	public <V> boolean accept(GraphVisitor<CFG, Statement, Edge, V> visitor, V tool) {
		for (int i = 0; i < ids.length; i++)
			if (!ids[i].accept(visitor, tool))
				return false;
		
		if (!e.accept(visitor, tool))
			return false;
		
		return visitor.visit(tool, getCFG(), this);
	}
	
	@Override
	public String toString() {
		return ids + " := " + e.toString();
	}
	
	@Override
	public <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> semantics(
			AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural, StatementStore<A, H, V> expressions)
			throws SemanticException {
		// FIXME: currently, we are just putting the identifiers to top, without
		// investigating the expression
		AnalysisState<A, H, V> result = entryState;
		expressions.put(e, entryState);

		ExternalSet<Type> untyped = Caches.types().mkSingletonSet(Untyped.INSTANCE);
		for (int i = 0; i < ids.length; i++) {
			Variable id = new Variable(untyped, ids[i].getName());
			result = result.assign(id, new PushAny(untyped), this);
			expressions.put(ids[i], result);
		}

		return result;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((e == null) ? 0 : e.hashCode());
		result = prime * result + Arrays.hashCode(ids);
		return result;
	}

	@Override
	public boolean isEqualTo(Statement obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GoMultiShortVariableDeclaration other = (GoMultiShortVariableDeclaration) obj;
		if (e == null) {
			if (other.e != null)
				return false;
		} else if (!e.equals(other.e))
			return false;
		if (!Arrays.equals(ids, other.ids))
			return false;
		return true;
	}
}
