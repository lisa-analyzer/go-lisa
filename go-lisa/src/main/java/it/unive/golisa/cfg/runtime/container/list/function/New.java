package it.unive.golisa.cfg.runtime.container.list.function;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.cfg.runtime.container.list.type.List;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.ProgramUnit;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.ReferenceType;
import it.unive.lisa.type.Untyped;

/**
 * func New() *List
 * 
 * @link https://pkg.go.dev/container/list#New
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class New extends NativeCFG {

	private final static Annotations anns = new Annotations(TaintDomain.CLEAN_ANNOTATION, IntegrityNIDomain.HIGH_ANNOTATION);

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
			super(cfg, location, "ListImpl", List.INSTANCE);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>,
		H extends HeapDomain<H>,
		V extends ValueDomain<V>,
		T extends TypeDomain<T>> AnalysisState<A, H, V, T> expressionSemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				ExpressionSet<SymbolicExpression>[] params, StatementStore<A, H, V, T> expressions)
						throws SemanticException {

			List listType = List.getListType(getProgram());

			// Allocates the new memory for a Time object
			MemoryAllocation alloc = new MemoryAllocation(listType, getLocation(), anns, true);
			HeapReference ref = new HeapReference(new ReferenceType(listType), alloc, getLocation());
			HeapDereference deref = new HeapDereference(listType, ref, getLocation());
			AnalysisState<A, H, V, T> asg = state.assign(deref, new PushAny(Untyped.INSTANCE, getLocation()), this);				
			return asg.smallStepSemantics(ref, original);
		}
	}
}