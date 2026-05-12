package it.unive.golisa.cfg.runtime.time.function;

import java.util.Collection;

import it.unive.golisa.cfg.runtime.time.type.Duration;
import it.unive.golisa.cfg.runtime.time.type.Time;
import it.unive.lisa.analysis.AbstractDomain;
import it.unive.lisa.analysis.AbstractLattice;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.UnaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.heap.HeapDereference;
import it.unive.lisa.symbolic.heap.HeapReference;
import it.unive.lisa.symbolic.heap.MemoryAllocation;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.type.ReferenceType;

/**
 * func Since(t Time) Duration.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Since extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param timeUnit the unit to which this native cfg belongs to
	 */
	public Since(CodeLocation location, CodeUnit timeUnit) {
		super(new CodeMemberDescriptor(location, timeUnit, false, "Since", Duration.INSTANCE,
				new Parameter(location, "this", Time.getTimeType(timeUnit.getProgram()))),
				SinceImpl.class);
	}

	/**
	 * The Since implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class SinceImpl extends UnaryExpression
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
		public static SinceImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new SinceImpl(cfg, location, params[0]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param expr     the expression
		 */
		public SinceImpl(CFG cfg, CodeLocation location, Expression expr) {
			super(cfg, location, "SinceImpl", Duration.INSTANCE, expr);
		}

		@Override
		public <A extends AbstractLattice<A>, D extends AbstractDomain<A>> AnalysisState<A> fwdUnarySemantics(
				InterproceduralAnalysis<A, D> interprocedural, AnalysisState<A> state, SymbolicExpression expr,
				StatementStore<A> expressions) throws SemanticException {
			
			Unit unit = getProgram().getUnit("time");
			Collection<CodeMember> members = unit.getCodeMembersByName("Since");
			
			Annotations annots = new Annotations();
			for(CodeMember cm : members) {
				for(Annotation a : cm.getDescriptor().getAnnotations()) {
					annots.addAnnotation(a);
				}
			}
			
			// Allocates the new memory for a Time object
			MemoryAllocation alloc = new MemoryAllocation(Duration.INSTANCE, getLocation(), annots,  true);
			AnalysisState<A> allocState = interprocedural.getAnalysis().smallStepSemantics(state, alloc, this);

			// Assigns an unknown object to each allocation identifier
			HeapReference ref = new HeapReference(new ReferenceType(Duration.INSTANCE), alloc, getLocation());
			HeapDereference deref = new HeapDereference(Duration.INSTANCE, ref, getLocation());
			AnalysisState<A> asg = interprocedural.getAnalysis().assign(allocState, deref, new PushAny(Duration.INSTANCE, getLocation()), this);

			return interprocedural.getAnalysis().smallStepSemantics(asg, ref, original);
		}

	}
}
