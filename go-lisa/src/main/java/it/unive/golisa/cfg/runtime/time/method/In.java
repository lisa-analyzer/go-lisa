package it.unive.golisa.cfg.runtime.time.method;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.taint.Tainted;
import it.unive.golisa.cfg.runtime.time.type.Time;
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
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.statement.BinaryExpression;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.PluggableStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.type.Untyped;

/**
 * func (t Time) In(loc *Location) Time 
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class In extends NativeCFG {

	/**
	 * Builds the native cfg.
	 * 
	 * @param location the location where this native cfg is defined
	 * @param timeUnit the unit to which this native cfg belongs to
	 */
	public In(CodeLocation location, CompilationUnit timeUnit) {
		super(new CodeMemberDescriptor(location, timeUnit, true, "In",
				Time.getTimeType(timeUnit.getProgram()),
				new Parameter(location, "this", Time.getTimeType(timeUnit.getProgram())), 
				new Parameter(location, "loc", Untyped.INSTANCE)),
				InImpl.class);
	}

	/**
	 * The {@link InImpl} implementation.
	 * 
	 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
	 */
	public static class InImpl extends BinaryExpression implements PluggableStatement {

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
		public static InImpl build(CFG cfg, CodeLocation location, Expression... params) {
			return new InImpl(cfg, location, params);
		}

		/**
		 * Builds the pluggable statement.
		 * 
		 * @param cfg      the {@link CFG} where this pluggable statement lies
		 * @param location the location where this pluggable statement is
		 *                     defined
		 * @param params   the parameters
		 */
		public InImpl(CFG cfg, CodeLocation location, Expression... params) {
			super(cfg, location, "In", Time.getTimeType(null), params[0], params[1]);
		}

		@Override
		public <A extends AbstractState<A, H, V, T>, H extends HeapDomain<H>, V extends ValueDomain<V>, T extends TypeDomain<T>> AnalysisState<A, H, V, T> binarySemantics(
				InterproceduralAnalysis<A, H, V, T> interprocedural, AnalysisState<A, H, V, T> state,
				SymbolicExpression left, SymbolicExpression right, StatementStore<A, H, V, T> expressions)
				throws SemanticException {
			
			ValueEnvironment<?> env = state.getDomainInstance(ValueEnvironment.class);
			if (env != null) {
				ValueEnvironment<?> linst = state.smallStepSemantics(left, original).getDomainInstance(ValueEnvironment.class);
				ValueEnvironment<?> rinst = state.smallStepSemantics(right, original).getDomainInstance(ValueEnvironment.class);
				if (linst.getValueOnStack() instanceof TaintDomain) {
					if (((TaintDomain)linst.getValueOnStack()).isTainted()
							|| ((TaintDomain)rinst.getValueOnStack()).isTainted())
						return state.smallStepSemantics(new Tainted(getStaticType(), getLocation()), original);
					return state.smallStepSemantics(new Clean(Untyped.INSTANCE, getLocation()), original);
				}
			}
			
			InferenceSystem<?> sys = state.getDomainInstance(InferenceSystem.class);
			if (sys != null) {
				InferenceSystem<?> linst = state.smallStepSemantics(left, original).getDomainInstance(InferenceSystem.class);
				InferenceSystem<?> rinst = state.smallStepSemantics(right, original).getDomainInstance(InferenceSystem.class);
				if (linst.getInferredValue() instanceof IntegrityNIDomain) {
					if (((IntegrityNIDomain)linst.getInferredValue()).isLowIntegrity()
							|| ((IntegrityNIDomain)rinst.getInferredValue()).isLowIntegrity())
						return state.smallStepSemantics(new Tainted(getStaticType(),getLocation()), original);
					return state.smallStepSemantics(new Clean(Untyped.INSTANCE, getLocation()), original);
				}
			}
			
			return state.smallStepSemantics(left, original);
		}	
	}
}
