package it.unive.golisa.cfg.runtime.time.function;

import it.unive.golisa.cfg.runtime.time.type.Duration;
import it.unive.golisa.cfg.runtime.time.type.Time;
import it.unive.lisa.analysis.AbstractDomain;
import it.unive.lisa.analysis.AbstractLattice;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.lattices.ExpressionSet;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.ReferenceType;
import java.util.Collection;

/**
 * func Now() Time.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Now extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param timeUnit the unit to which this native cfg belongs to
	 */
	public Now(CodeLocation location, CodeUnit timeUnit) {
		super(new CodeMemberDescriptor(location, timeUnit, false, "Now", Duration.INSTANCE),
				NowImpl.class);
	}

	/**
	 * The {@link Now} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class NowImpl extends it.unive.lisa.program.cfg.statement.NaryExpression
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
		public static NowImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new NowImpl(cfg, location);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 */
		public NowImpl(CFG cfg, CodeLocation location) {
			super(cfg, location, "NowImpl", Time.getTimeType(null));
		}

		@Override
		public <A extends AbstractLattice<A>, D extends AbstractDomain<A>> AnalysisState<A> forwardSemanticsAux(
				InterproceduralAnalysis<A, D> interprocedural, AnalysisState<A> state, ExpressionSet[] params,
				StatementStore<A> expressions) throws SemanticException {

			Time timeType = Time.getTimeType(getProgram());

			Unit unit = getProgram().getUnit("time");
			Collection<CodeMember> members = unit.getCodeMembersByName("Now");

			Annotations annots = new Annotations();
			for (CodeMember cm : members) {
				for (Annotation a : cm.getDescriptor().getAnnotations()) {
					annots.addAnnotation(a);
				}
			}

			// Allocates the new memory for a Time object
			MemoryAllocation alloc = new MemoryAllocation(timeType, getLocation(), annots, true);
			AnalysisState<A> allocState = interprocedural.getAnalysis().smallStepSemantics(state, alloc, this);

			// Assigns an unknown object to each allocation identifier
			HeapReference ref = new HeapReference(new ReferenceType(timeType), alloc, getLocation());
			HeapDereference deref = new HeapDereference(timeType, ref, getLocation());
			AnalysisState<A> asg = interprocedural.getAnalysis().assign(allocState, deref,
					new PushAny(timeType, getLocation()), this);

			return interprocedural.getAnalysis().smallStepSemantics(asg, ref, original);
		}
	}
}
