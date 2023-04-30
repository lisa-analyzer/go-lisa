package it.unive.golisa.cfg.expression.ternary;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.Clean;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.taint.TaintDomainForPhase1;
import it.unive.golisa.analysis.taint.TaintDomainForPhase2;
import it.unive.golisa.analysis.taint.Tainted;
import it.unive.golisa.analysis.taint.TaintedP1;
import it.unive.golisa.analysis.taint.TaintedP2;
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
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.TernaryExpression;
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

		
		ValueEnvironment<?> env = state.getDomainInstance(ValueEnvironment.class);
		if (env != null) {
			ValueEnvironment<?> linst = state.smallStepSemantics(left, this).getDomainInstance(ValueEnvironment.class);
			ValueEnvironment<?> minst = state.smallStepSemantics(middle, this).getDomainInstance(ValueEnvironment.class);
			ValueEnvironment<?> rinst = state.smallStepSemantics(right, this).getDomainInstance(ValueEnvironment.class);
			if (linst.getValueOnStack() instanceof TaintDomainForPhase1) {
				if (((TaintDomainForPhase1)linst.getValueOnStack()).isTainted()
						|| ((TaintDomainForPhase1)minst.getValueOnStack()).isTainted()
						|| ((TaintDomainForPhase1)rinst.getValueOnStack()).isTainted())
					return state.smallStepSemantics(new TaintedP1(getLocation()), this);
			}
			if (linst.getValueOnStack() instanceof TaintDomainForPhase2) {
				if (((TaintDomainForPhase2)linst.getValueOnStack()).isTainted()
						|| ((TaintDomainForPhase2)minst.getValueOnStack()).isTainted()
						|| ((TaintDomainForPhase2)rinst.getValueOnStack()).isTainted())
					return state.smallStepSemantics(new TaintedP2(getLocation()), this);
			}
			if (linst.getValueOnStack() instanceof TaintDomain) {
				if (((TaintDomain)linst.getValueOnStack()).isTainted()
						|| ((TaintDomain)minst.getValueOnStack()).isTainted()
						|| ((TaintDomain)rinst.getValueOnStack()).isTainted())
					return state.smallStepSemantics(new Tainted(getLocation()), this);
				return state.smallStepSemantics(new Clean(Untyped.INSTANCE, getLocation()), this);
			}
		}
		
		InferenceSystem<?> sys = state.getDomainInstance(InferenceSystem.class);
		if (sys != null) {
			InferenceSystem<?> linst = state.smallStepSemantics(left, this).getDomainInstance(InferenceSystem.class);
			InferenceSystem<?> minst = state.smallStepSemantics(left, this).getDomainInstance(InferenceSystem.class);
			InferenceSystem<?> rinst = state.smallStepSemantics(right, this).getDomainInstance(InferenceSystem.class);
			if (linst.getInferredValue() instanceof IntegrityNIDomain) {
				if (((IntegrityNIDomain)linst.getInferredValue()).isLowIntegrity()
						|| ((IntegrityNIDomain)minst.getInferredValue()).isLowIntegrity()
						|| ((IntegrityNIDomain)rinst.getInferredValue()).isLowIntegrity())
					return state.smallStepSemantics(new Tainted(getLocation()), this);
				return state.smallStepSemantics(new Clean(Untyped.INSTANCE, getLocation()), this);
			}
		}
		
		return state.smallStepSemantics(
				new TernaryExpression(GoStringType.INSTANCE,
						left, middle, right, StringSubstring.INSTANCE, getLocation()),
				this);
	}
}
