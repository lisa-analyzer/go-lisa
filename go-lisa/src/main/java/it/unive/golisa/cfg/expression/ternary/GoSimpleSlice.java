package it.unive.golisa.cfg.expression.ternary;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.taint.Tainted;
import it.unive.golisa.cfg.type.GoStringType;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.StatementStore;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.nonrelational.inference.InferredValue;
import it.unive.lisa.analysis.nonrelational.value.NonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.InterproceduralAnalysis;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.TernaryExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.symbolic.value.operator.ternary.StringSubstring;
import it.unive.lisa.type.Untyped;

/**
 * A Go slice expression (e.g., s[1:5]).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoSimpleSlice extends it.unive.lisa.program.cfg.statement.TernaryExpression {
	/**
	 * Builds a Go slice expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left expression
	 * @param middle   the middle expression
	 * @param right    the right expression
	 */
	public GoSimpleSlice(CFG cfg, SourceCodeLocation location, Expression left, Expression middle, Expression right) {
		super(cfg, location, "slice", left, middle, right);
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
		
		return state.smallStepSemantics(
				new TernaryExpression(GoStringType.INSTANCE,
						left, middle, right, StringSubstring.INSTANCE, getLocation()),
				this);
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
							return state.smallStepSemantics(new Tainted(getLocation()), this);
						}
					}
				if(middleState != null)
					for( SymbolicExpression reWriteExpr : middleState.rewrite(middleState.getComputedExpressions(), this)) {
						NonRelationalValueDomain<?> stack = middleEnv.eval((ValueExpression) reWriteExpr, this);
						isTaintDomain = isTaintDomain || stack instanceof TaintDomain;
						if (stack != null && stack instanceof TaintDomain && ((TaintDomain) stack).isTainted()) {
							return state.smallStepSemantics(new Tainted(getLocation()), this);
						}
					}
				if(leftEnv != null)
					for( SymbolicExpression reWriteExpr : leftState.rewrite(leftState.getComputedExpressions(), this)) {
						NonRelationalValueDomain<?> stack = leftEnv.eval((ValueExpression) reWriteExpr, this);
						isTaintDomain = isTaintDomain || stack instanceof TaintDomain;
						if (stack != null && stack instanceof TaintDomain && ((TaintDomain) stack).isTainted()) {
							return state.smallStepSemantics(new Tainted(getLocation()), this);
						}
					}
				if(isTaintDomain)
					return state.smallStepSemantics(new Clean(getStaticType(), getLocation()), this);
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
							return state.smallStepSemantics(new Tainted(getLocation()), this);
						}
					}
				if(middleSys != null)
					for( SymbolicExpression reWriteExpr : middleState.rewrite(middleState.getComputedExpressions(), this)) {
						InferredValue<?> stack = middleSys.eval((ValueExpression) reWriteExpr, this);
						isIntegrityNIDomain = isIntegrityNIDomain || stack instanceof IntegrityNIDomain;
						if (stack != null && stack instanceof IntegrityNIDomain && ((IntegrityNIDomain) stack).isLowIntegrity()) {
							return state.smallStepSemantics(new Tainted(getLocation()), this);
						}
					}
				
				if(leftSys != null)
					for( SymbolicExpression reWriteExpr : leftState.rewrite(leftState.getComputedExpressions(), this)) {
						InferredValue<?> stack = leftSys.eval((ValueExpression) reWriteExpr, this);
						isIntegrityNIDomain = isIntegrityNIDomain || stack instanceof IntegrityNIDomain;
						if (stack != null && stack instanceof IntegrityNIDomain && ((IntegrityNIDomain) stack).isLowIntegrity()) {
							return state.smallStepSemantics(new Tainted(getLocation()), this);
						}
					}
				if(isIntegrityNIDomain)
					return state.smallStepSemantics(new Clean(getStaticType(), getLocation()), this);
					
			}
			
			return null;
		
	}
}
