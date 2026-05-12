package it.unive.golisa.cfg.expression;

import it.unive.lisa.analysis.AbstractDomain;
import it.unive.lisa.analysis.AbstractLattice;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.lattices.ExpressionSet;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Type;

/**
 * A Go new expression, allocation an object in the heap (e.g., new(Vertex)).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoNew extends NaryExpression {

	/**
	 * Builds the new expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param type     the type to allocate
	 */
	public GoNew(CFG cfg, SourceCodeLocation location, Type type) {
		super(cfg, location, "new", type);
	}

	@Override
	protected int compareSameClassAndParams(Statement o) {
		return 0; // nothing else to compare
	}

	@Override
	public <A extends AbstractLattice<A>, D extends AbstractDomain<A>> AnalysisState<A> forwardSemanticsAux(
			InterproceduralAnalysis<A, D> interprocedural, AnalysisState<A> state, ExpressionSet[] params,
			StatementStore<A> expressions) throws SemanticException {
		// Following the Golang reference:
		// The new built-in function allocates memory. The first argument is a
		// type, not a value,
		// and the value returned is a pointer to a newly allocated zero value
		// of that type.
		MemoryAllocation created = new MemoryAllocation(getStaticType(), getLocation(), new Annotations(), false);
		HeapReference ref = new HeapReference(new ReferenceType(getStaticType()), created, getLocation());
		return interprocedural.getAnalysis().smallStepSemantics(state, ref, this);
	}
}
