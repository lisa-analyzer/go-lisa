package it.unive.golisa.checker;

import it.unive.golisa.analysis.heap.GoAbstractState;
import it.unive.golisa.analysis.heap.GoPointBasedHeap;
import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.lisa.analysis.CFGWithAnalysisResults;
import it.unive.lisa.analysis.SemanticException;
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
import java.util.Collection;

/**
 * A non-interference integrity checker.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class IntegrityNIChecker implements
		SemanticCheck<
				GoAbstractState<InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>>,
				GoPointBasedHeap, InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>> {

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
			GoAbstractState<InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>>,
			GoPointBasedHeap, InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>> tool) {
	}

	@Override
	public void afterExecution(
			CheckToolWithAnalysisResults<
					GoAbstractState<InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>>,
					GoPointBasedHeap, InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>> tool) {
	}

	@Override
	public boolean visitUnit(CheckToolWithAnalysisResults<
			GoAbstractState<InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>>,
			GoPointBasedHeap, InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>> tool,
			Unit unit) {
		return true;
	}

	@Override
	public void visitGlobal(
			CheckToolWithAnalysisResults<
					GoAbstractState<InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>>,
					GoPointBasedHeap, InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>> tool,
			Unit unit, Global global, boolean instance) {
	}

	private static final String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th",
			"th" };

	private static String ordinal(int i) {
		switch (i % 100) {
		case 11:
		case 12:
		case 13:
			return i + "th";
		default:
			return i + suffixes[i % 10];

		}
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					GoAbstractState<InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>>,
					GoPointBasedHeap, InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>> tool,
			CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					GoAbstractState<InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>>,
					GoPointBasedHeap, InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>> tool,
			CFG graph, Statement node) {
		if (!(node instanceof UnresolvedCall))
			return true;

		UnresolvedCall call = (UnresolvedCall) node;
		Call resolved = (Call) tool.getResolvedVersion(call);

		if (resolved instanceof NativeCall) {
			NativeCall nativeCfg = (NativeCall) resolved;
			Collection<NativeCFG> nativeCfgs = nativeCfg.getTargetedConstructs();
			for (NativeCFG n : nativeCfgs)
				try {
					process(tool, call, resolved, n.getDescriptor());
				} catch (SemanticException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		} else if (resolved instanceof CFGCall) {
			CFGCall cfg = (CFGCall) resolved;
			for (CFG n : cfg.getTargetedCFGs())
				try {
					process(tool, call, resolved, n.getDescriptor());
				} catch (SemanticException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		return true;

	}

	private void process(
			CheckToolWithAnalysisResults<
					GoAbstractState<InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>>,
					GoPointBasedHeap, InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>> tool,
			UnresolvedCall call, Call resolved, CodeMemberDescriptor desc) throws SemanticException {
		if (desc.getAnnotations().contains(SINK_MATCHER))
			for (CFGWithAnalysisResults<
					GoAbstractState<InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>>,
					GoPointBasedHeap, InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>> result : tool
							.getResultOf(call.getCFG())) {
				if (result.getAnalysisStateAfter(call).getState()
						.getValueState().getExecutionState()
						.isLowIntegrity())
					tool.warnOn(call, "The execution of this call is guarded by a tainted condition"
							+ " resulting in an implicit flow");
			}
		Parameter[] parameters = desc.getFormals();
		for (int i = 0; i < parameters.length; i++)
			if (parameters[i].getAnnotations().contains(SINK_MATCHER))
				for (CFGWithAnalysisResults<
						GoAbstractState<InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>>,
						GoPointBasedHeap, InferenceSystem<IntegrityNIDomain>,
						TypeEnvironment<InferredTypes>> result : tool.getResultOf(call.getCFG())) {
					if (result.getAnalysisStateAfter(call.getParameters()[i])
							.getState().getValueState().getInferredValue().isLowIntegrity())
						tool.warnOn(call, "The value passed for the " + ordinal(i + 1)
								+ " parameter of this call is tainted, and it reaches the sink at parameter '"
								+ parameters[i].getName() + "' of " + resolved.getFullTargetName());
					if (result.getAnalysisStateAfter(call.getParameters()[call.getParameters().length - 1]).getState()
							.getValueState().getExecutionState()
							.isLowIntegrity())
						tool.warnOn(call, "The execution of this call is guarded by a tainted condition"
								+ " resulting in an implicit flow");
				}
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					GoAbstractState<InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>>,
					GoPointBasedHeap, InferenceSystem<IntegrityNIDomain>, TypeEnvironment<InferredTypes>> tool,
			CFG graph, Edge edge) {
		return true;
	}
}
