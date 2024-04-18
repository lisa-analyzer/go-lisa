package it.unive.golisa.checker;

import java.util.Collection;

import it.unive.golisa.analysis.heap.GoAbstractState;
import it.unive.golisa.analysis.heap.GoPointBasedHeap;
import it.unive.golisa.analysis.taint.TaintDomainForPhase1;
import it.unive.golisa.analysis.taint.TaintDomainForPhase2;
import it.unive.lisa.analysis.CFGWithAnalysisResults;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.annotations.matcher.AnnotationMatcher;
import it.unive.lisa.program.annotations.matcher.BasicAnnotationMatcher;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.NativeCall;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;

/**
 * A Go taint checker.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class UCCICheckerPhase2 implements
		SemanticCheck<GoAbstractState<ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>>,
				GoPointBasedHeap, ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>> {

	/**
	 * Sink annotation.
	 */
	public static final Annotation SINK_ANNOTATION_PHASE2 = new Annotation("lisa.taint.SinkPhase2");

	/**
	 * Sink matcher.
	 */
	public static final AnnotationMatcher SINK_MATCHER_PHASE2 = new BasicAnnotationMatcher(SINK_ANNOTATION_PHASE2);

	@Override
	public void beforeExecution(CheckToolWithAnalysisResults<
			GoAbstractState<ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>>,
			GoPointBasedHeap, ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>> tool) {
	}

	@Override
	public void afterExecution(
			CheckToolWithAnalysisResults<
					GoAbstractState<ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>>,
					GoPointBasedHeap, ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>> tool) {
	}

	@Override
	public void visitGlobal(
			CheckToolWithAnalysisResults<
					GoAbstractState<ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>>,
					GoPointBasedHeap, ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>> tool,
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
	public boolean visit(CheckToolWithAnalysisResults<
			GoAbstractState<ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>>,
			GoPointBasedHeap, ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>> tool, CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					GoAbstractState<ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>>,
					GoPointBasedHeap, ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>> tool,
			CFG graph, Statement node) {
		if (!(node instanceof UnresolvedCall))
			return true;

		UnresolvedCall call = (UnresolvedCall) node;
		Call resolved = (Call) tool.getResolvedVersion(call);

		if (resolved instanceof NativeCall) {
			NativeCall nativeCfg = (NativeCall) resolved;
			Collection<CodeMember> nativeCfgs = nativeCfg.getTargets();
			for (CodeMember n : nativeCfgs) {
				Parameter[] parameters = n.getDescriptor().getFormals();
				boolean[] resultsParam = new boolean[parameters.length];
				for (int i = 0; i < parameters.length; i++) {
					if (parameters[i].getAnnotations().contains(SINK_MATCHER_PHASE2))
						for (CFGWithAnalysisResults<
								GoAbstractState<ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>>,
								GoPointBasedHeap, ValueEnvironment<TaintDomainForPhase2>,
								TypeEnvironment<InferredTypes>> result : tool.getResultOf(call.getCFG())) {
							TaintDomainForPhase2 valueOnStack = result.getAnalysisStateAfter(call.getParameters()[i]).getState().getValueState()
							.getValueOnStack();
							if (valueOnStack.isTainted() || valueOnStack.maybeTainted()) {
								resultsParam[i] = true;
							}
						}
				}
				buildWarning(tool, call, parameters, resultsParam);
			}
		} else if (resolved instanceof CFGCall) {
			CFGCall cfg = (CFGCall) resolved;
			for (CodeMember n : cfg.getTargets()) {
				Parameter[] parameters = n.getDescriptor().getFormals();
				boolean[] resultsParam = new boolean[parameters.length];
				for (int i = 0; i < parameters.length; i++)
					if (parameters[i].getAnnotations().contains(SINK_MATCHER_PHASE2))
						for (CFGWithAnalysisResults<
								GoAbstractState<ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>>,
								GoPointBasedHeap, ValueEnvironment<TaintDomainForPhase2>,
								TypeEnvironment<InferredTypes>> result : tool.getResultOf(call.getCFG())) {
							TaintDomainForPhase2 valueOnStack = result.getAnalysisStateAfter(call.getParameters()[i]).getState().getValueState()
							.getValueOnStack();
							if (valueOnStack.isTainted() || valueOnStack.maybeTainted()) {
								resultsParam[i] = true;
							}
						}
				buildWarning(tool, call, parameters, resultsParam);
			}
		} else {
			checkSignature(call, tool);
		}

		return true;

	}

	private void checkSignature(UnresolvedCall call,
			CheckToolWithAnalysisResults<GoAbstractState<ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>>,
					GoPointBasedHeap, ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>> tool) {
		if (call != null) {
			String targetName = call.getTargetName();
			if (((targetName.equals("PutState") || targetName.equals("PutPrivateData"))
					&& call.getParameters().length == 3)
					|| ((targetName.equals("DelState") || targetName.equals("DelPrivateData"))
							&& call.getParameters().length == 2)) {
				boolean[] resultsParam = new boolean[call.getParameters().length];
				for (CFGWithAnalysisResults<
						GoAbstractState<ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>>,
						GoPointBasedHeap, ValueEnvironment<TaintDomainForPhase2>,
						TypeEnvironment<InferredTypes>> result : tool.getResultOf(call.getCFG())) {
					for (int i = 1; i < call.getParameters().length; i++) {
						TaintDomainForPhase2 valueOnStack = result.getAnalysisStateAfter(call.getParameters()[i]).getState().getValueState()
						.getValueOnStack();
						if (valueOnStack.isTainted() || valueOnStack.maybeTainted()) {
							resultsParam[i] = true;
						}
					}
				}
				buildWarning(tool, call, null, resultsParam);
			}
		}

	}


	private void buildWarning(
			CheckToolWithAnalysisResults<
			GoAbstractState<ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>>,
			GoPointBasedHeap, ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>> tool,
			UnresolvedCall call, Parameter[] parameters, boolean[] results) {
			
		int matches = 0;
		String res = "";
		for (int i=0; i < results.length; i++) {
			if(results[i]) {
				if(matches > 0)
					res += ", ";
				
				res += parameters != null ? parameters[i].getName() +"("+ordinal(i + 1)+")" : ordinal(i + 1) +" param";
				matches++;
			}
			
		}
		if(matches > 0)
			tool.warnOn(call, "The sink "+ call.getFullTargetName() +" has the following tainted parameter"+ (matches > 1 ? "s": "") + ": " + res); 
			
	}
	
	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					GoAbstractState<ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>>,
					GoPointBasedHeap, ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>> tool,
			CFG graph, Edge edge) {
		return true;
	}

	@Override
	public boolean visitUnit(
			CheckToolWithAnalysisResults<GoAbstractState<ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>>,
					GoPointBasedHeap, ValueEnvironment<TaintDomainForPhase2>, TypeEnvironment<InferredTypes>> tool,
			Unit unit) {
		return true;
	}
}
