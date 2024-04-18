package it.unive.golisa.cfg.runtime.fmt;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.taint.TaintDomainForPhase1;
import it.unive.golisa.analysis.taint.TaintDomainForPhase2;
import it.unive.golisa.analysis.taint.Tainted;
import it.unive.golisa.analysis.taint.TaintedP1;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.CodeUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.TernaryExpression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Untyped;

/**
 * The Sprintf function from fmt package.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Sprintf extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param fmtUnit  the unit to which this native cfg belongs to
	 */
	public Sprintf(CodeLocation location, CodeUnit fmtUnit) {
		super(new CodeMemberDescriptor(location, fmtUnit, false, "Sprintf", GoStringType.INSTANCE,
				new Parameter(location, "this", Untyped.INSTANCE), new Parameter(location, "that", Untyped.INSTANCE), new Parameter(location, "other", Untyped.INSTANCE)),
				SprintfImpl.class);
	}

	/**
	 * The {@link Sprintf} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class SprintfImpl extends TernaryExpression implements PluggableStatement {

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
		public static SprintfImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new SprintfImpl(cfg, location, params[0], params[1], params[2]);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param arg      the expression
		 */
		public SprintfImpl(CFG cfg, CodeLocation location, Expression left, Expression middle, Expression right) {
			super(cfg, location, "Sprintf", GoStringType.INSTANCE, left, middle, right);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>,
				H extends HeapDomain<H>,
				V extends ValueDomain<V>,
				T extends TypeDomain<T>> AnalysisState<A, H, V, T> ternarySemantics(
						InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
						SymbolicExpression left, SymbolicExpression middle, SymbolicExpression right,
						StatementStore<A, H, V, T> expressions) throws SemanticException {
			ValueEnvironment<?> env = state.getDomainInstance(ValueEnvironment.class);
			if (env != null) {
				ValueEnvironment<?> linst = state.smallStepSemantics(left, original).getDomainInstance(ValueEnvironment.class);
				ValueEnvironment<?> minst = state.smallStepSemantics(middle, original).getDomainInstance(ValueEnvironment.class);
				ValueEnvironment<?> rinst = state.smallStepSemantics(right, original).getDomainInstance(ValueEnvironment.class);
				if (linst.getValueOnStack() instanceof TaintDomainForPhase1) {
					if (((TaintDomainForPhase1)linst.getValueOnStack()).isTainted()
							|| ((TaintDomainForPhase1)minst.getValueOnStack()).isTainted()
							|| ((TaintDomainForPhase1)rinst.getValueOnStack()).isTainted())
						return state.smallStepSemantics(new TaintedP1(getLocation()), original);
					return state.smallStepSemantics(new Clean(Untyped.INSTANCE, getLocation()), original);
				}
				if (linst.getValueOnStack() instanceof TaintDomainForPhase2) {
					if (((TaintDomainForPhase2)linst.getValueOnStack()).isTainted()
							|| ((TaintDomainForPhase2)minst.getValueOnStack()).isTainted()
							|| ((TaintDomainForPhase2)rinst.getValueOnStack()).isTainted())
						return state.smallStepSemantics(new TaintedP1(getLocation()), original);
					return state.smallStepSemantics(new Clean(Untyped.INSTANCE, getLocation()), original);
				}
				if (linst.getValueOnStack() instanceof TaintDomain) {
					if (((TaintDomain)linst.getValueOnStack()).isTainted()
							|| ((TaintDomain)minst.getValueOnStack()).isTainted()
							|| ((TaintDomain)rinst.getValueOnStack()).isTainted())
						return state.smallStepSemantics(new Tainted(getLocation()), original);
					return state.smallStepSemantics(new Clean(Untyped.INSTANCE, getLocation()), original);
				}
			}
			
			InferenceSystem<?> sys = state.getDomainInstance(InferenceSystem.class);
			if (sys != null) {
				InferenceSystem<?> linst = state.smallStepSemantics(left, original).getDomainInstance(InferenceSystem.class);
				InferenceSystem<?> minst = state.smallStepSemantics(middle, original).getDomainInstance(InferenceSystem.class);
				InferenceSystem<?> rinst = state.smallStepSemantics(right, original).getDomainInstance(InferenceSystem.class);
				if (linst.getInferredValue() instanceof IntegrityNIDomain) {
					if (((IntegrityNIDomain)linst.getInferredValue()).isLowIntegrity()
							|| ((IntegrityNIDomain)minst.getInferredValue()).isLowIntegrity()
							|| ((IntegrityNIDomain)rinst.getInferredValue()).isLowIntegrity())
						return state.smallStepSemantics(new Tainted(getLocation()), original);
					return state.smallStepSemantics(new Clean(Untyped.INSTANCE, getLocation()), original);
				}
			}
			
			return state.smallStepSemantics(left, original)
								.lub(state.smallStepSemantics(middle, original))
								.lub(state.smallStepSemantics(right, original));
		}
	}
}
