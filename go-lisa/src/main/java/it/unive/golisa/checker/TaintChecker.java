package it.unive.golisa.checker;

import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.lisa.analysis.CFGWithAnalysisResults;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.MonolithicHeap;
import it.unive.lisa.analysis.nonrelational.inference.InferenceSystem;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.annotations.matcher.AnnotationMatcher;
import it.unive.lisa.program.annotations.matcher.BasicAnnotationMatcher;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.NativeCFG;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.MultiCall;
import it.unive.lisa.program.cfg.statement.call.NativeCall;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import java.util.Collection;

public class TaintChecker implements
		SemanticCheck<SimpleAbstractState<MonolithicHeap, InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>>,
				MonolithicHeap, InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>> {

	public static final Annotation SINK_ANNOTATION = new Annotation("lisa.taint.Sink");
	public static final AnnotationMatcher SINK_MATCHER = new BasicAnnotationMatcher(SINK_ANNOTATION);

	@Override
	public void beforeExecution(CheckToolWithAnalysisResults<
			SimpleAbstractState<MonolithicHeap, InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>>,
			MonolithicHeap,
			InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>> tool) {
	}

	@Override
	public void afterExecution(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<MonolithicHeap, InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>>,
					MonolithicHeap, InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>> tool) {
	}

	@Override
	public boolean visitCompilationUnit(CheckToolWithAnalysisResults<
			SimpleAbstractState<MonolithicHeap, InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>>,
			MonolithicHeap,
			InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>> tool,
			CompilationUnit unit) {
		return true;
	}

	@Override
	public void visitGlobal(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<MonolithicHeap, InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>>,
					MonolithicHeap, InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>> tool,
			Unit unit, Global global, boolean instance) {
	}

	private static final String[] suffixes = new String[] { "th", "st", "nd", "rd", "th", "th", "th", "th", "th",
			"th" };

	public static String ordinal(int i) {
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
			SimpleAbstractState<MonolithicHeap, InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>>,
			MonolithicHeap, InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>> tool, CFG graph) {
		Parameter[] parameters = graph.getDescriptor().getFormals();
		for (int i = 0; i < parameters.length; i++)
			if (parameters[i].getAnnotations().contains(SINK_MATCHER))
				for (Call call : tool.getCallSites(graph))
					for (CFGWithAnalysisResults<
							SimpleAbstractState<MonolithicHeap, InferenceSystem<TaintDomain>,
									TypeEnvironment<InferredTypes>>,
							MonolithicHeap, InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>> result : tool
									.getResultOf(call.getCFG()))
						if (result.getAnalysisStateAfter(call.getParameters()[i]).getState().getValueState()
								.getInferredValue().isTainted())
							tool.warnOn(call, "The value passed for the " + ordinal(i + 1)
									+ " parameter of this call is tainted, and it reaches the sink at parameter '"
									+ parameters[i].getName() + "' of " + graph.getDescriptor().getFullName());

		return true;
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<MonolithicHeap, InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>>,
					MonolithicHeap, InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>> tool,
			CFG graph, Statement node) {
		if (!(node instanceof UnresolvedCall))
			return true;

		UnresolvedCall call = (UnresolvedCall) node;
		Call resolved = (Call) tool.getResolvedVersion(call);

		if (resolved instanceof MultiCall) {
			MultiCall hybrid = (MultiCall) resolved;

			Collection<Call> nativeCfgs = hybrid.getCalls();
			for (Call nativeCfg : nativeCfgs) {
				Parameter[] parameters = nativeCfg.getCFG().getDescriptor().getFormals();
				for (int i = 0; i < parameters.length; i++)
					if (parameters[i].getAnnotations().contains(SINK_MATCHER))
						for (CFGWithAnalysisResults<
								SimpleAbstractState<MonolithicHeap, InferenceSystem<TaintDomain>,
										TypeEnvironment<InferredTypes>>,
								MonolithicHeap, InferenceSystem<TaintDomain>,
								TypeEnvironment<InferredTypes>> result : tool.getResultOf(call.getCFG()))
							if (result.getAnalysisStateAfter(call.getParameters()[i]).getState().getValueState()
									.getInferredValue().isTainted())
								tool.warnOn(call, "The value passed for the " + ordinal(i + 1)
										+ " parameter of this call is tainted, and it reaches the sink at parameter '"
										+ parameters[i].getName() + "' of " + graph.getDescriptor().getFullName());
			}
		} else if (resolved instanceof NativeCall) {
			NativeCall nativeCfg = (NativeCall) resolved;
			Collection<NativeCFG> nativeCfgs = nativeCfg.getTargets();
			for (NativeCFG n : nativeCfgs) {

				Parameter[] parameters = n.getDescriptor().getFormals();
				for (int i = 0; i < parameters.length; i++)
					if (parameters[i].getAnnotations().contains(SINK_MATCHER))
						for (CFGWithAnalysisResults<
								SimpleAbstractState<MonolithicHeap, InferenceSystem<TaintDomain>,
										TypeEnvironment<InferredTypes>>,
								MonolithicHeap, InferenceSystem<TaintDomain>,
								TypeEnvironment<InferredTypes>> result : tool.getResultOf(call.getCFG()))
							if (result.getAnalysisStateAfter(call.getParameters()[i]).getState().getValueState()
									.getInferredValue().isTainted())
								tool.warnOn(call, "The value passed for the " + ordinal(i + 1)
										+ " parameter of this call is tainted, and it reaches the sink at parameter '"
										+ parameters[i].getName() + "' of " + graph.getDescriptor().getFullName());

			}
		}

		return true;

	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<MonolithicHeap, InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>>,
					MonolithicHeap, InferenceSystem<TaintDomain>, TypeEnvironment<InferredTypes>> tool,
			CFG graph, Edge edge) {
		return true;
	}
}
