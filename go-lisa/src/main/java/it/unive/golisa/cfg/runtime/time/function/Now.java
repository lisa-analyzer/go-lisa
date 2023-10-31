package it.unive.golisa.cfg.runtime.time.function;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.cfg.runtime.time.type.Duration;
import it.unive.golisa.cfg.runtime.time.type.Time;
import it.unive.golisa.cfg.type.numeric.signed.GoIntType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
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

/**
 * func Now() Time.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Now extends NativeCFG {

	/**
	 * Annotations of this CFG. The output of this CFG is non-deterministic.
	 */
	private static final Annotations anns = new Annotations(TaintDomain.TAINTED_ANNOTATION,
			IntegrityNIDomain.LOW_ANNOTATION);

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param timeUnit the unit to which this native cfg belongs to
	 */
	public Now(CodeLocation location, CodeUnit timeUnit) {
		super(new CodeMemberDescriptor(location, timeUnit, false, "Now", Duration.INSTANCE, anns),
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
		public <A extends AbstractState<A>> AnalysisState<A> forwardSemanticsAux(
				InterproceduralAnalysis<A> interprocedural, AnalysisState<A> state,
				ExpressionSet[] params, StatementStore<A> expressions)
				throws SemanticException {

			Time timeType = Time.getTimeType(getProgram());

			// Allocates the new memory for a Time object
			MemoryAllocation alloc = new MemoryAllocation(timeType, getLocation(), anns, true);
			AnalysisState<A> allocState = state.smallStepSemantics(alloc, this);

			// Assigns an unknown object to each allocation identifier
			HeapReference ref = new HeapReference(new ReferenceType(timeType), alloc, getLocation());
			HeapDereference deref = new HeapDereference(timeType, ref, getLocation());
			AnalysisState<A> asg = allocState.assign(deref, new PushAny(timeType, getLocation()), this);
			asg = asg.assign(deref, new PushAny(GoIntType.INSTANCE, getLocation()), this);
			return asg.smallStepSemantics(ref, original);
		}
	}
}
