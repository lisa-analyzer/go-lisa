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
import it.unive.lisa.analysis.nonrelational.inference.InferredValue;
import it.unive.lisa.analysis.nonrelational.value.NonRelationalValueDomain;
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
import it.unive.lisa.symbolic.value.ValueExpression;
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
			AnalysisState<A, H, V, T> rightState = state.smallStepSemantics(right, this);
			AnalysisState<A, H, V, T> leftState = state.smallStepSemantics(left, this);
			
			AnalysisState<A, H, V, T> taintSemantics = semanticsForTaintDomain(state, rightState, leftState);
			if(taintSemantics != null)
				return taintSemantics;
			
			AnalysisState<A, H, V, T> integrityNISemantics = semanticsForIntegrityNIDomain(state, rightState, leftState);
			if(integrityNISemantics != null)
				return integrityNISemantics;
	
			return state.smallStepSemantics(left, original);
		}
		

		private <A extends AbstractState<A, H, V, T>,
		H extends HeapDomain<H>,
		V extends ValueDomain<V>,
		T extends TypeDomain<T>> AnalysisState<A, H, V, T> semanticsForTaintDomain(AnalysisState<A, H, V, T>  state, AnalysisState<A, H, V, T>  rightState,
				AnalysisState<A, H, V, T> leftState) throws SemanticException {
				
			ValueEnvironment<?> rightEnv = rightState.getDomainInstance(ValueEnvironment.class);
			ValueEnvironment<?> leftEnv = leftState.getDomainInstance(ValueEnvironment.class);
			
			if (rightEnv != null || leftEnv != null) {
					boolean isTaintDomain = false;
					if(rightEnv != null)
						for( SymbolicExpression reWriteExpr : rightState.rewrite(rightState.getComputedExpressions(), this)) {
							NonRelationalValueDomain<?> stack = rightEnv.eval((ValueExpression) reWriteExpr, this);
							isTaintDomain = isTaintDomain || stack instanceof TaintDomain;
							if (stack != null && stack instanceof TaintDomain && ((TaintDomain) stack).isTainted()) {
								return state.smallStepSemantics(new Tainted(getLocation()), original);
							}
						}

					if(leftEnv != null)
						for( SymbolicExpression reWriteExpr : leftState.rewrite(leftState.getComputedExpressions(), this)) {
							NonRelationalValueDomain<?> stack = leftEnv.eval((ValueExpression) reWriteExpr, this);
							isTaintDomain = isTaintDomain || stack instanceof TaintDomain;
							if (stack != null && stack instanceof TaintDomain && ((TaintDomain) stack).isTainted()) {
								return state.smallStepSemantics(new Tainted(getLocation()), original);
							}
						}
					if(isTaintDomain)
						return state.smallStepSemantics(new Clean(getStaticType(), getLocation()), original);
			}
			
			return null;
			
		}
		
		private <A extends AbstractState<A, H, V, T>,
		H extends HeapDomain<H>,
		V extends ValueDomain<V>,
		T extends TypeDomain<T>> AnalysisState<A, H, V, T> semanticsForIntegrityNIDomain(AnalysisState<A, H, V, T>  state, AnalysisState<A, H, V, T>  rightState,
				AnalysisState<A, H, V, T> leftState) throws SemanticException {
			
				InferenceSystem<?> rightSys = rightState.getDomainInstance(InferenceSystem.class);
				InferenceSystem<?> leftSys = leftState.getDomainInstance(InferenceSystem.class);
				
				if (rightSys != null || leftSys != null) {
					boolean isIntegrityNIDomain = false;
					if(rightSys != null)
						for( SymbolicExpression reWriteExpr : rightState.rewrite(rightState.getComputedExpressions(), this)) {
							InferredValue<?> stack = rightSys.eval((ValueExpression) reWriteExpr, this);
							isIntegrityNIDomain = isIntegrityNIDomain || stack instanceof IntegrityNIDomain;
							if (stack != null && stack instanceof IntegrityNIDomain && ((IntegrityNIDomain) stack).isLowIntegrity()) {
								return state.smallStepSemantics(new Tainted(getLocation()), original);
							}
						}
					
					if(leftSys != null)
						for( SymbolicExpression reWriteExpr : leftState.rewrite(leftState.getComputedExpressions(), this)) {
							InferredValue<?> stack = leftSys.eval((ValueExpression) reWriteExpr, this);
							isIntegrityNIDomain = isIntegrityNIDomain || stack instanceof IntegrityNIDomain;
							if (stack != null && stack instanceof IntegrityNIDomain && ((IntegrityNIDomain) stack).isLowIntegrity()) {
								return state.smallStepSemantics(new Tainted(getLocation()), original);
							}
						}
					if(isIntegrityNIDomain)
						return state.smallStepSemantics(new Clean(getStaticType(), getLocation()), original);
						
				}
				
				return null;
			
		}
	}
}
