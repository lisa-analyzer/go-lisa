package it.unive.golisa.interprocedural;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.taint.Tainted;
import it.unive.golisa.golang.api.signature.FuncGoLangApiSignature;
import it.unive.golisa.golang.api.signature.GoLangApiSignature;
import it.unive.golisa.golang.api.signature.MethodGoLangApiSignature;
import it.unive.golisa.golang.util.GoLangAPISignatureMapper;
import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.MethodAnnotation;
import it.unive.golisa.loader.annotation.sets.GoNonDeterminismAnnotationSet;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.heap.HeapDomain;
import it.unive.lisa.analysis.lattices.ExpressionSet;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.value.TypeDomain;
import it.unive.lisa.analysis.value.ValueDomain;
import it.unive.lisa.interprocedural.OpenCallPolicy;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.program.cfg.statement.call.OpenCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.symbolic.value.PushAny;
import it.unive.lisa.symbolic.value.Skip;
import it.unive.lisa.symbolic.value.Variable;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * OpenCall policy to be less conservative during taint and non-interference
 * analysis.
 */
public class RelaxedOpenCallPolicy implements OpenCallPolicy {

	/**
	 * The singleton instance of this class.
	 */
	public static final RelaxedOpenCallPolicy INSTANCE = new RelaxedOpenCallPolicy();

	private RelaxedOpenCallPolicy() {
	}

	@Override
	public <A extends AbstractState<A, H, V, T>,
			H extends HeapDomain<H>,
			V extends ValueDomain<V>,
			T extends TypeDomain<T>> AnalysisState<A, H, V, T> apply(
					OpenCall call,
					AnalysisState<A, H, V, T> entryState,
					ExpressionSet<SymbolicExpression>[] params)
					throws SemanticException {

		if (call.getStaticType().isVoidType())
			return entryState.smallStepSemantics(new Skip(call.getLocation()), call);

		if (entryState.getState().getValueState() instanceof ValueEnvironment<?>) {

			ValueEnvironment<?> valueEnv = (ValueEnvironment<?>) entryState.getState().getValueState();
			var stackValue = valueEnv.getValueOnStack();
			if (stackValue instanceof TaintDomain) {
				Variable var = new Variable(call.getStaticType(), RETURNED_VARIABLE_NAME, call.getLocation());
				if (((TaintDomain) stackValue).isTainted() || ((TaintDomain) stackValue).isTop()) {
					PushAny pushany = new PushAny(call.getStaticType(), call.getLocation());
					return entryState.assign(var, pushany, call);
				} else if (((TaintDomain) stackValue).isClean()) { // &&
																	// isRuntimeAPI(call))
																	// {
					if (!isSourceForNonDeterminism(call))
						return entryState.assign(var,
								new Constant(call.getStaticType(), "SAFE_RETURNED_VALUE", call.getLocation()), call);
					else
						return entryState.assign(var, new Tainted(call.getLocation()), call);

				} else if (((TaintDomain) stackValue).isBottom()) {
					return entryState;
				}
			}
		} else if (entryState.getState().getValueState() instanceof InferenceSystem<?>) {
			Variable var = new Variable(call.getStaticType(), RETURNED_VARIABLE_NAME, call.getLocation());
			var infSys = ((InferenceSystem<?>) entryState.getState().getValueState());
			var value = infSys.getInferredValue();
			if (value != null && value instanceof IntegrityNIDomain) {
				IntegrityNIDomain ni = (IntegrityNIDomain) value;
				if (ni.isLowIntegrity() || ni.isTop()) {
					PushAny pushany = new PushAny(call.getStaticType(), call.getLocation());
					return entryState.assign(var, pushany, call);
				} else if (ni.isHighIntegrity()) {// && isRuntimeAPI(call)) {
					if (!isSourceForNonDeterminism(call))
						return entryState.assign(var,
								new Constant(call.getStaticType(), "SAFE_RETURNED_VALUE", call.getLocation()), call);
					else
						return entryState.assign(var, new PushAny(call.getStaticType(), call.getLocation()), call);

				} else if (ni.isBottom())
					return entryState;
			}
		}

		PushAny pushany = new PushAny(call.getStaticType(), call.getLocation());
		Variable var = new Variable(call.getStaticType(), RETURNED_VARIABLE_NAME, call.getLocation());
		return entryState.assign(var, pushany, call);
	}

	private boolean isRuntimeAPI(OpenCall call) {

		if (call.getCallType().equals(CallType.STATIC)) {
			if (call.getQualifier() != null)
				return checkRuntimeApiFunc(call, call.getQualifier());
		} else if (call.getCallType().equals(CallType.INSTANCE)) {
			if (call.getQualifier() != null)
				return checkRuntimeApiMethod(call, call.getQualifier());
		} else {
			if (call.getQualifier() != null) {
				return checkRuntimeApiFunc(call, call.getQualifier())
						|| checkRuntimeApiMethod(call, call.getQualifier());
			}
		}

		return false;
	}

	private boolean checkRuntimeApiFunc(Call call, String qualifier) {
		Map<String, Set<FuncGoLangApiSignature>> mapFunc = GoLangAPISignatureMapper.getGoApiSignatures().getMapFunc();
		if (mapFunc.containsKey(call.getQualifier()))
			for (FuncGoLangApiSignature sign : mapFunc.get(qualifier))
				if (matchSignature(sign, call))
					return true;
		return false;
	}

	private boolean checkRuntimeApiMethod(Call call, String qualifier) {
		Map<String,
				Set<MethodGoLangApiSignature>> mapMethod = GoLangAPISignatureMapper.getGoApiSignatures().getMapMethod();
		if (mapMethod.containsKey(call.getQualifier())) {
			for (MethodGoLangApiSignature sign : mapMethod.get(qualifier))
				if (matchSignature(sign, call))
					return true;
		} else {
			Collection<Set<MethodGoLangApiSignature>> signaturesSets = mapMethod.values();
			for (Set<MethodGoLangApiSignature> set : signaturesSets)
				for (MethodGoLangApiSignature m : set)
					if (matchSignature(m, call))
						return true;
		}

		return false;
	}

	private boolean matchSignature(GoLangApiSignature goLangApiSignature, Call call) {

		String signatureName = null;
		String[] params = null;
		if (goLangApiSignature instanceof FuncGoLangApiSignature) {
			signatureName = ((FuncGoLangApiSignature) goLangApiSignature).getName();
			params = ((FuncGoLangApiSignature) goLangApiSignature).getParams();
		} else if (goLangApiSignature instanceof MethodGoLangApiSignature) {
			signatureName = ((MethodGoLangApiSignature) goLangApiSignature).getName();
			params = ((MethodGoLangApiSignature) goLangApiSignature).getParams();
		}
		if (signatureName != null && signatureName.equals(call.getTargetName())
				&& params != null && params.length <= call.getParameters().length)
			return true;

		return false;
	}

	private boolean isSourceForNonDeterminism(Call call) {
		GoNonDeterminismAnnotationSet sources = new GoNonDeterminismAnnotationSet();
		for (CodeAnnotation ca : sources.getAnnotationForSources()) {
			if (ca instanceof MethodAnnotation) {
				MethodAnnotation ma = (MethodAnnotation) ca;
				if (call.getTargetName().equals(ma.getName()))
					if (call.getQualifier() != null && (ma.getUnit().equals(call.getQualifier())
							|| (ma.getUnit().contains("/") && ma.getUnit().endsWith(call.getQualifier()))))
						return true;
			}
		}

		return false;

	}
}
