package it.unive.golisa.cfg.runtime.fmt;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.taint.Tainted;
import it.unive.golisa.cfg.type.GoStringType;
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
import it.unive.lisa.symbolic.value.ValueExpression;
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
			
			
			AnalysisState<A, H, V, T> rightState = state.smallStepSemantics(right, this);
			AnalysisState<A, H, V, T> middleState = state.smallStepSemantics(middle, this);
			AnalysisState<A, H, V, T> leftState = state.smallStepSemantics(left, this);
			
			AnalysisState<A, H, V, T> taintSemantics = semanticsForTaintDomain(state, rightState, middleState, leftState);
			if(taintSemantics != null)
				return taintSemantics;
			
			AnalysisState<A, H, V, T> integrityNISemantics = semanticsForIntegrityNIDomain(state, rightState, middleState, leftState);
			if(integrityNISemantics != null)
				return integrityNISemantics;
			
			return state.smallStepSemantics(left, original)
								.lub(state.smallStepSemantics(middle, original))
								.lub(state.smallStepSemantics(right, original));
		}



		private <A extends AbstractState<A, H, V, T>,
		H extends HeapDomain<H>,
		V extends ValueDomain<V>,
		T extends TypeDomain<T>> AnalysisState<A, H, V, T> semanticsForTaintDomain(AnalysisState<A, H, V, T>  state, AnalysisState<A, H, V, T>  rightState,
				AnalysisState<A, H, V, T>  middleState, AnalysisState<A, H, V, T> leftState) throws SemanticException {
				
			ValueEnvironment<?> rightEnv = rightState.getDomainInstance(ValueEnvironment.class);
			ValueEnvironment<?> middleEnv = middleState.getDomainInstance(ValueEnvironment.class);
			ValueEnvironment<?> leftEnv = leftState.getDomainInstance(ValueEnvironment.class);
			
			if (rightEnv != null || middleState != null ||leftEnv != null) {
					boolean isTaintDomain = false;
					if(rightEnv != null)
						for( SymbolicExpression reWriteExpr : rightState.rewrite(rightState.getComputedExpressions(), this)) {
							NonRelationalValueDomain<?> stack = rightEnv.eval((ValueExpression) reWriteExpr, this);
							isTaintDomain = isTaintDomain || stack instanceof TaintDomain;
							if (stack != null && stack instanceof TaintDomain && ((TaintDomain) stack).isTainted()) {
								return state.smallStepSemantics(new Tainted(getLocation()), original);
							}
						}
					if(middleState != null)
						for( SymbolicExpression reWriteExpr : middleState.rewrite(middleState.getComputedExpressions(), this)) {
							NonRelationalValueDomain<?> stack = middleEnv.eval((ValueExpression) reWriteExpr, this);
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
				AnalysisState<A, H, V, T>  middleState, AnalysisState<A, H, V, T> leftState) throws SemanticException {
			
				InferenceSystem<?> rightSys = rightState.getDomainInstance(InferenceSystem.class);
				InferenceSystem<?> middleSys = middleState.getDomainInstance(InferenceSystem.class);
				InferenceSystem<?> leftSys = leftState.getDomainInstance(InferenceSystem.class);
				
				if (rightSys != null || middleSys != null || leftSys != null) {
					boolean isIntegrityNIDomain = false;
					if(rightSys != null)
						for( SymbolicExpression reWriteExpr : rightState.rewrite(rightState.getComputedExpressions(), this)) {
							InferredValue<?> stack = rightSys.eval((ValueExpression) reWriteExpr, this);
							isIntegrityNIDomain = isIntegrityNIDomain || stack instanceof IntegrityNIDomain;
							if (stack != null && stack instanceof IntegrityNIDomain && ((IntegrityNIDomain) stack).isLowIntegrity()) {
								return state.smallStepSemantics(new Tainted(getLocation()), original);
							}
						}
					if(middleSys != null)
						for( SymbolicExpression reWriteExpr : middleState.rewrite(middleState.getComputedExpressions(), this)) {
							InferredValue<?> stack = middleSys.eval((ValueExpression) reWriteExpr, this);
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
