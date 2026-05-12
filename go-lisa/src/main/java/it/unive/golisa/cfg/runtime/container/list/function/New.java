package it.unive.golisa.cfg.runtime.container.list.function;

import it.unive.golisa.cfg.runtime.container.list.type.List;
import it.unive.lisa.analysis.AbstractDomain;
import it.unive.lisa.analysis.AbstractLattice;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.lattices.ExpressionSet;
import it.unive.lisa.program.ProgramUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.type.ReferenceType;

/**
 * func New() *List.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class New extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param listUnit the unit to which this native cfg belongs to
	 */
	public New(CodeLocation location, ProgramUnit listUnit) {
		super(new CodeMemberDescriptor(location, listUnit, false, "New", List.INSTANCE),
				NewImpl.class);
	}

	/**
	 * The {@link New} implementation.
	 * 
	 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
	 */
	public static class NewImpl extends NaryExpression
			implements PluggableStatement {

		private Statement original;

		@Override
		public void setOriginatingStatement(Statement st) {
			original = st;
		}

		@Override
		protected int compareSameClassAndParams(Statement o) {
			return 0; // nothing else to compare
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 * 
		 * @return the pluggable statement
		 */
		public static NewImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new NewImpl(cfg, location);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 */
		public NewImpl(CFG cfg, CodeLocation location) {
			super(cfg, location, "NewImpl", List.INSTANCE);
		}

		@Override
		public <A extends AbstractLattice<A>, D extends AbstractDomain<A>> AnalysisState<A> forwardSemanticsAux(
				InterproceduralAnalysis<A, D> interprocedural, AnalysisState<A> state, ExpressionSet[] params,
				StatementStore<A> expressions) throws SemanticException {

			List listType = List.getListType(getProgram());

			// Allocates the new memory for a Time object
			MemoryAllocation alloc = new MemoryAllocation(listType, getLocation(), true);
			HeapReference ref = new HeapReference(new ReferenceType(listType), alloc, getLocation());
			return interprocedural.getAnalysis().smallStepSemantics(state, ref, original);
		}

	}
}