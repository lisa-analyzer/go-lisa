package it.unive.golisa.checker;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.checker.TaintChecker.HeapResolver;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.AnalyzedCFG;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.annotations.matcher.AnnotationMatcher;
import it.unive.lisa.program.annotations.matcher.BasicAnnotationMatcher;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.NativeCall;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.Identifier;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.util.StringUtilities;

/**
 * A non-interference integrity checker.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class IntegrityNIChecker implements
SemanticCheck<
SimpleAbstractState<PointBasedHeap, InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>>> {

	/**
	 * The sink annotation.
	 */
	public static final Annotation SINK_ANNOTATION = new Annotation("lisa.intni.Sink");

	/**
	 * The sink matcher.
	 */
	public static final AnnotationMatcher SINK_MATCHER = new BasicAnnotationMatcher(SINK_ANNOTATION);

	@Override
	public void beforeExecution(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>>> tool) {
	}

	@Override
	public void afterExecution(
			CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, InferenceSystem<IntegrityNIDomain>,
			TypeEnvironment<InferredTypes>>> tool) {
	}

	@Override
	public boolean visitUnit(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>>> tool,
			Unit unit) {
		return true;
	}

	@Override
	public void visitGlobal(
			CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, InferenceSystem<IntegrityNIDomain>,
			TypeEnvironment<InferredTypes>>> tool,
			Unit unit, Global global, boolean instance) {
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, InferenceSystem<IntegrityNIDomain>,
			TypeEnvironment<InferredTypes>>> tool,
			CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, InferenceSystem<IntegrityNIDomain>,
			TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {
		if (!(node instanceof UnresolvedCall))
			return true;

		UnresolvedCall call = (UnresolvedCall) node;
		try {
			for (AnalyzedCFG<
					SimpleAbstractState<PointBasedHeap, InferenceSystem<IntegrityNIDomain>,
					TypeEnvironment<InferredTypes>>> result : tool.getResultOf(call.getCFG())) {
				Call resolved = (Call) tool.getResolvedVersion(call, result);

				if (resolved instanceof NativeCall) {
					NativeCall nativeCfg = (NativeCall) resolved;
					Collection<NativeCFG> nativeCfgs = nativeCfg.getTargetedConstructs();
					for (NativeCFG n : nativeCfgs)
						process(tool, call, resolved, n.getDescriptor(), result);
				} else if (resolved instanceof CFGCall) {
					CFGCall cfg = (CFGCall) resolved;
					for (CFG n : cfg.getTargetedCFGs())
						process(tool, call, resolved, n.getDescriptor(), result);
				}
			}
		} catch (SemanticException e) {
			e.printStackTrace();
		}

		return true;

	}

	private void process(
			CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, InferenceSystem<IntegrityNIDomain>,
			TypeEnvironment<InferredTypes>>> tool,
			UnresolvedCall call, Call resolved, CodeMemberDescriptor desc,
			AnalyzedCFG<
			SimpleAbstractState<PointBasedHeap, InferenceSystem<IntegrityNIDomain>,
			TypeEnvironment<InferredTypes>>> res)
					throws SemanticException {
		if (desc.getAnnotations().contains(SINK_MATCHER)) {
			if (res.getAnalysisStateAfter(call).getState()
					.getValueState().getExecutionState()
					.isLowIntegrity())
				tool.warnOn(call, "The execution of this call is guarded by a tainted condition"
						+ " resulting in an implicit flow");
		}

		Parameter[] parameters = desc.getFormals();
		for (int i = 0; i < parameters.length; i++)
			if (parameters[i].getAnnotations().contains(SINK_MATCHER)) {
				AnalysisState<
				SimpleAbstractState<PointBasedHeap, InferenceSystem<IntegrityNIDomain>,
				TypeEnvironment<InferredTypes>>> state = res.getAnalysisStateAfter(call.getParameters()[i]);


				Set<SymbolicExpression> reachableIds = new HashSet<>();								
				for (SymbolicExpression e : state.getComputedExpressions())
					reachableIds.addAll(HeapResolver.resolve(state, e, call));

				for (SymbolicExpression stack : reachableIds) {
					InferenceSystem<IntegrityNIDomain> valueState = state.getState().getValueState();
					if (stack instanceof Identifier && valueState.knowsIdentifier((Identifier) stack) &&valueState.eval((ValueExpression) stack, call, state.getState()).isLowIntegrity())
						tool.warnOn(call, "The value passed for the " + StringUtilities.ordinal(i + 1)
						+ " parameter of this call is tainted, and it reaches the sink at parameter '"
						+ parameters[i].getName() + "' of " + resolved.getFullTargetName());
				}
				
				if (res.getAnalysisStateAfter(call.getParameters()[call.getParameters().length - 1]).getState()
						.getValueState().getExecutionState()
						.isLowIntegrity())
					tool.warnOn(call, "The execution of this call is guarded by a tainted condition"
							+ " resulting in an implicit flow");
			}
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, InferenceSystem<IntegrityNIDomain>,
			TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Edge edge) {
		return true;
	}
}
