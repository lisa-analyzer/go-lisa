package it.unive.golisa.cfg.statement.assignment;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

import it.unive.golisa.cfg.expression.GoAnonymousVariable;
import it.unive.golisa.cfg.statement.assignment.GoShortVariableDeclaration.NumericalTyper;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
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
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.AccessChild;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import it.unive.lisa.util.collections.externalSet.ExternalSet;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;

public class GoMultiAssignment extends Expression {
	
	protected final Expression[] ids;
	protected final Expression e;
	
	public GoMultiAssignment(CFG cfg, String filePath, int line, int col, Expression[]  ids, Expression e) {
		super(cfg, new SourceCodeLocation(filePath, line, col));
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
		return StringUtils.join(ids, ", ") + " := " + e.toString();
	}
	
	@Override
	public <A extends AbstractState<A, H, V>, H extends HeapDomain<H>, V extends ValueDomain<V>> AnalysisState<A, H, V> semantics(
			AnalysisState<A, H, V> entryState, InterproceduralAnalysis<A, H, V> interprocedural, StatementStore<A, H, V> expressions)
			throws SemanticException {
		AnalysisState<A, H, V> rightState = e.semantics(entryState, interprocedural, expressions);
		expressions.put(e, rightState);

		AnalysisState<A, H, V> result = entryState.bottom();

		ExternalSet<Type> untyped = Caches.types().mkSingletonSet(Untyped.INSTANCE);
		for (int i = 0; i < ids.length; i++) {
			AnalysisState<A, H, V> idState = ids[i].semantics(rightState, interprocedural, expressions);
			expressions.put(ids[i], idState);

			if (ids[i] instanceof GoAnonymousVariable)
				continue;
			
			for (SymbolicExpression retExp : rightState.getComputedExpressions()) {
				HeapDereference dereference = new HeapDereference(Caches.types().mkSingletonSet(getStaticType()), retExp, getLocation());
				AccessChild access = new AccessChild(untyped, dereference, new Constant(GoIntType.INSTANCE, i, getLocation()), getLocation());
				AnalysisState<A, H, V> accessState = rightState.smallStepSemantics(access, this);
				
				for (SymbolicExpression accessExp : accessState.getComputedExpressions()) {
					for (SymbolicExpression idExp : idState.getComputedExpressions())
					result = result.lub(rightState.assign(idExp, NumericalTyper.type(accessExp), this));
				}
			}
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
}
