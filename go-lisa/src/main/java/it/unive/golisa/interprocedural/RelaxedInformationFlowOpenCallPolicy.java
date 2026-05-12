package it.unive.golisa.interprocedural;

import it.unive.golisa.cfg.expression.instrumented.TaintPlaceholder;
import it.unive.lisa.analysis.AbstractDomain;
import it.unive.lisa.analysis.AbstractLattice;
import it.unive.lisa.analysis.Analysis;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.SimpleAbstractDomain;
import it.unive.lisa.analysis.informationFlow.BaseTaint;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.interprocedural.OpenCallPolicy;
import it.unive.lisa.lattices.ExpressionSet;
import it.unive.lisa.lattices.SimpleAbstractState;
import it.unive.lisa.lattices.informationFlow.TaintLattice;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.statement.call.OpenCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.Skip;
import it.unive.lisa.symbolic.value.ValueExpression;

/**
 * OpenCall policy to be less conservative during taint and non-interference
 * analysis.
 */
public class RelaxedInformationFlowOpenCallPolicy implements OpenCallPolicy {

	/**
	 * The singleton instance of this class.
	 */
	public static final RelaxedInformationFlowOpenCallPolicy INSTANCE = new RelaxedInformationFlowOpenCallPolicy();

	private RelaxedInformationFlowOpenCallPolicy() {
	}

	@Override
	public <A extends AbstractLattice<A>, D extends AbstractDomain<A>> AnalysisState<A> apply(OpenCall call,
			AnalysisState<A> entryState, Analysis<A, D> analysis, it.unive.lisa.lattices.ExpressionSet[] params)
			throws SemanticException {
		if (call.getStaticType().isVoidType())
			return analysis.smallStepSemantics(entryState, new Skip(call.getLocation()), call);
	
		if(entryState.getExecutionState() instanceof SimpleAbstractState<?, ?, ?>) {
			SimpleAbstractState<?, ?, ?> execState = (SimpleAbstractState<?, ?, ?>) entryState.getExecutionState();
			if (analysis.domain instanceof SimpleAbstractDomain<?, ?, ?>) {
				SimpleAbstractDomain<?, ?, ?> domain = (SimpleAbstractDomain<?, ?, ?>) analysis.domain;
				var analysisValueDomain = domain.valueDomain;
				if(analysisValueDomain instanceof BaseTaint<?>) {
					BaseTaint<?> baseTaint =  (BaseTaint<?>) analysisValueDomain;
					int[] vParams = new int[params.length];
					int i=0;
					for(ExpressionSet pSet : params) {
						for(SymbolicExpression expr : pSet) {
							for (SymbolicExpression stack : analysis.rewrite(entryState, expr, call)) {
								var valueState =  execState.valueState;
								SemanticOracle oracle = analysis.domain.makeOracle(entryState.getExecutionState());
								TaintLattice<?> value = baseTaint.eval((ValueEnvironment) valueState, (ValueExpression) stack, (ProgramPoint) call, oracle);
								vParams[i] = updateAbstractValue(vParams[i], value);
							}
						}
						i++;
					}
					
					if(isClean(vParams)) {
						Identifier var = call.getMetaVariable();
						return analysis.assign(entryState, var, new Constant(call.getStaticType(), "_CLEAN_RETURNED_VALUE_", call.getLocation()), call);
					} else if(isTainted(vParams)) {
						Identifier var = call.getMetaVariable();
						return analysis.assign(entryState, var, new TaintPlaceholder(call.getStaticType(), call.getLocation()), call);
					}
				}
			}
			/*if (state.getValueState() instanceof ValueEnvironment<?>) {
				ValueEnvironment<?> valueEnv = (ValueEnvironment<?>) state.getValueState();
				for (SymbolicExpression stack : state.rewrite(entryState.getComputedExpressions(), call, state)) {
					NonRelationalValueDomain<?> stackValue = valueEnv.eval((ValueExpression) stack, call, state);
					if (stackValue instanceof TaintDomain) {
						Identifier var = call.getMetaVariable();
						if (((TaintDomain) stackValue).isTainted() || ((TaintDomain) stackValue).isTop()) {
							PushAny pushany = new PushAny(call.getStaticType(), call.getLocation());
							return entryState.assign(var, pushany, call);
						} else if (((TaintDomain) stackValue).isClean()) {
							return entryState.assign(var,
									new Constant(call.getStaticType(), "SAFE_RETURNED_VALUE", call.getLocation()),
									call);
						} else if (((TaintDomain) stackValue).isBottom()) {
							return entryState;
						}
					}else if (stackValue instanceof GoIntervalDomain) {
						Identifier var = call.getMetaVariable();
						PushAny pushany = new PushAny(call.getStaticType(), call.getLocation());
						return entryState.assign(var, pushany, call);
					}
				}
			} else if (state.getValueState() instanceof InferenceSystem<?>) {
				Identifier var = call.getMetaVariable();
				var infSys = ((InferenceSystem<?>) state.getValueState());
				for (SymbolicExpression stack : state.rewrite(entryState.getComputedExpressions(), call, state)) {
					InferredValue<?> value = infSys.eval((ValueExpression) stack, call, state);
					if (value != null && value instanceof IntegrityNIDomain) {
						IntegrityNIDomain ni = (IntegrityNIDomain) value;
						if (ni.isLowIntegrity() || ni.isTop()) {
							PushAny pushany = new PushAny(call.getStaticType(), call.getLocation());
							return entryState.assign(var, pushany, call);
						} else if (ni.isHighIntegrity()) {
							return entryState.assign(var,
									new Constant(call.getStaticType(), "SAFE_RETURNED_VALUE", call.getLocation()),
									call);

						} else if (ni.isBottom())
							return entryState;
					}
				}
			}
			*/
		}
		
		PushAny pushany = new PushAny(call.getStaticType(), call.getLocation());
		Identifier var = call.getMetaVariable();
		return analysis.assign(entryState, var, pushany, call);
	}

	private boolean isClean(int[] vParams) {
		for(int p : vParams) {
			if(p != 0)
				return false;
		}
		return true;
	}
	
	private boolean isTainted(int[] vParams) {
		for(int p : vParams) {
			if(p == 2)
				return true;
		}
		return false;
	}

	private int updateAbstractValue(int v, TaintLattice<?> value) {
		// 3 top
		// 2 taint
		// 1 clean
		// 0 to set
	
		if(value.isTop() || v == 3)
			return 3;
		
		int vValue = value.isTop() ? 3 : value.isAlwaysTainted() ? 2 : value.isAlwaysClean() ? 1 : value.isPossiblyTainted() ? 2 : value.isPossiblyClean() ? 1 : 3;

		if(v == 0 || v == vValue)
			return vValue;
		
		return 3;
	}

	/*
	@Override
	public <A extends AbstractLattice<A>, D extends AbstractDomain<A>> AnalysisState<A> apply(OpenCall call,
			AnalysisState<A> entryState, Analysis<A, D> analysis, it.unive.lisa.lattices.ExpressionSet[] params)
			throws SemanticException {
		if (call.getStaticType().isVoidType())
			return analysis.smallStepSemantics(entryState, new Skip(call.getLocation()), call);

		if (entryState.getState() instanceof SimpleAbstractState<?, ?, ?>) {
			SimpleAbstractState<?, ?, ?> state = (SimpleAbstractState<?, ?, ?>) entryState.getState();
			if (state.getValueState() instanceof ValueEnvironment<?>) {
				ValueEnvironment<?> valueEnv = (ValueEnvironment<?>) state.getValueState();
				for (SymbolicExpression stack : state.rewrite(entryState.getComputedExpressions(), call, state)) {
					NonRelationalValueDomain<?> stackValue = valueEnv.eval((ValueExpression) stack, call, state);
					if (stackValue instanceof TaintDomain) {
						Identifier var = call.getMetaVariable();
						if (((TaintDomain) stackValue).isTainted() || ((TaintDomain) stackValue).isTop()) {
							PushAny pushany = new PushAny(call.getStaticType(), call.getLocation());
							return entryState.assign(var, pushany, call);
						} else if (((TaintDomain) stackValue).isClean()) {
							return entryState.assign(var,
									new Constant(call.getStaticType(), "SAFE_RETURNED_VALUE", call.getLocation()),
									call);
						} else if (((TaintDomain) stackValue).isBottom()) {
							return entryState;
						}
					}else if (stackValue instanceof GoIntervalDomain) {
						Identifier var = call.getMetaVariable();
						PushAny pushany = new PushAny(call.getStaticType(), call.getLocation());
						return entryState.assign(var, pushany, call);
					}
				}
			} else if (state.getValueState() instanceof InferenceSystem<?>) {
				Identifier var = call.getMetaVariable();
				var infSys = ((InferenceSystem<?>) state.getValueState());
				for (SymbolicExpression stack : state.rewrite(entryState.getComputedExpressions(), call, state)) {
					InferredValue<?> value = infSys.eval((ValueExpression) stack, call, state);
					if (value != null && value instanceof IntegrityNIDomain) {
						IntegrityNIDomain ni = (IntegrityNIDomain) value;
						if (ni.isLowIntegrity() || ni.isTop()) {
							PushAny pushany = new PushAny(call.getStaticType(), call.getLocation());
							return entryState.assign(var, pushany, call);
						} else if (ni.isHighIntegrity()) {
							return entryState.assign(var,
									new Constant(call.getStaticType(), "SAFE_RETURNED_VALUE", call.getLocation()),
									call);

						} else if (ni.isBottom())
							return entryState;
					}
				}
			}
		}

		PushAny pushany = new PushAny(call.getStaticType(), call.getLocation());
		Identifier var = call.getMetaVariable();
		return analysis.assign(entryState, var, pushany, call);
	}
	*/
}
