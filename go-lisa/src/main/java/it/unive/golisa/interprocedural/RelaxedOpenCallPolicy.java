package it.unive.golisa.interprocedural;

import it.unive.golisa.analysis.GoIntervalDomain;
import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.taint.TaintDomainForPrivacyHF;
import it.unive.golisa.analysis.taint.Tainted;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.nonrelational.inference.InferredValue;
import it.unive.lisa.analysis.nonrelational.value.NonRelationalValueDomain;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.numeric.Interval;
import it.unive.lisa.interprocedural.OpenCallPolicy;
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
public abstract class RelaxedOpenCallPolicy implements OpenCallPolicy {


	@Override
	public <A extends AbstractState<A>> AnalysisState<A> apply(
			OpenCall call,
			AnalysisState<A> entryState,
			ExpressionSet[] params)
			throws SemanticException {

		if (call.getStaticType().isVoidType())
			return entryState.smallStepSemantics(new Skip(call.getLocation()), call);

		if (entryState.getState() instanceof SimpleAbstractState<?, ?, ?>) {
			SimpleAbstractState<?, ?, ?> state = (SimpleAbstractState<?, ?, ?>) entryState.getState();
			if (state.getValueState() instanceof ValueEnvironment<?>) {
				ValueEnvironment<?> valueEnv = (ValueEnvironment<?>) state.getValueState();
				for (SymbolicExpression stack : state.rewrite(entryState.getComputedExpressions(), call, state)) {
					NonRelationalValueDomain<?> stackValue = valueEnv.eval((ValueExpression) stack, call, state);
					if (stackValue instanceof TaintDomain) {
						Identifier var = call.getMetaVariable();
						if (((TaintDomain) stackValue).isTainted() || ((TaintDomain) stackValue).isTop()) {
							return entryState.assign(var, new Tainted(call.getLocation()), call);
						} else if (((TaintDomain) stackValue).isClean()) {
							
							if (!isSourceForTaint(call))
								return entryState.assign(var, new Constant(call.getStaticType(), "SAFE_RETURNED_VALUE", call.getLocation()), call);
							else
								return entryState.assign(var, new Tainted(call.getLocation()), call);
						} else if (((TaintDomain) stackValue).isBottom()) {
							return entryState;
						}
						
					
							
					} else if (stackValue instanceof TaintDomainForPrivacyHF) {
						Identifier var = call.getMetaVariable();
						if (((TaintDomainForPrivacyHF) stackValue).isTainted() || ((TaintDomainForPrivacyHF) stackValue).isTop()) {
							return entryState.assign(var, new Tainted(call.getLocation()), call);
						} else if (((TaintDomainForPrivacyHF) stackValue).isClean()) {
							
							if (!isSourceForTaint(call))
								return entryState.assign(var, new Constant(call.getStaticType(), "SAFE_RETURNED_VALUE", call.getLocation()), call);
							else
								return entryState.assign(var, new Tainted(call.getLocation()), call);
						} else if (((TaintDomainForPrivacyHF) stackValue).isBottom()) {
							return entryState;
						}
						
					
							
					} else if (stackValue instanceof GoIntervalDomain) {
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
		return entryState.assign(var, pushany, call);
	}
	
	public abstract boolean isSourceForTaint(OpenCall call);
}
