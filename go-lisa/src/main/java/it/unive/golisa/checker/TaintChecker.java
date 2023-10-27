package it.unive.golisa.checker;

import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.lisa.analysis.AbstractState;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.AnalyzedCFG;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
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
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.type.Type;
import it.unive.lisa.util.StringUtilities;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * A Go taint checker.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class TaintChecker implements
		SemanticCheck<
				SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomain>, TypeEnvironment<InferredTypes>>> {

	/**
	 * Sink annotation.
	 */
	public static final Annotation SINK_ANNOTATION = new Annotation("lisa.taint.Sink");

	/**
	 * Sink matcher.
	 */
	public static final AnnotationMatcher SINK_MATCHER = new BasicAnnotationMatcher(SINK_ANNOTATION);

	@Override
	public void beforeExecution(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomain>, TypeEnvironment<InferredTypes>>> tool) {
	}

	@Override
	public void afterExecution(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomain>,
							TypeEnvironment<InferredTypes>>> tool) {
	}

	@Override
	public void visitGlobal(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomain>,
							TypeEnvironment<InferredTypes>>> tool,
			Unit unit, Global global, boolean instance) {
	}

	@Override
	public boolean visit(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomain>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomain>,
							TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {
		if (!(node instanceof UnresolvedCall))
			return true;

		UnresolvedCall call = (UnresolvedCall) node;
		try {
			for (AnalyzedCFG<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomain>,
							TypeEnvironment<InferredTypes>>> result : tool.getResultOf(call.getCFG())) {
				Call resolved = tool.getResolvedVersion(call, result);
				if (resolved == null)
					System.err.println("Error");

				if (resolved instanceof NativeCall) {
					NativeCall nativeCfg = (NativeCall) resolved;
					Collection<CodeMember> nativeCfgs = nativeCfg.getTargets();
					for (CodeMember n : nativeCfgs) {
						Parameter[] parameters = n.getDescriptor().getFormals();
						for (int i = 0; i < parameters.length; i++)
							if (parameters[i].getAnnotations().contains(SINK_MATCHER)) {
								AnalysisState<
										SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomain>,
												TypeEnvironment<InferredTypes>>> state = result
														.getAnalysisStateAfter(call.getParameters()[i]);

								Set<SymbolicExpression> reachableIds = new HashSet<>();
								for (SymbolicExpression e : state.getComputedExpressions())
									reachableIds.addAll(HeapResolver.resolve(state, e, node));

								for (SymbolicExpression s : reachableIds) {
									Set<Type> types = state.getState().getRuntimeTypesOf(s, node, state.getState());

									if (types.stream().allMatch(t -> t.isInMemoryType() || t.isPointerType()))
										continue;

									ValueEnvironment<TaintDomain> valueState = state.getState().getValueState();
									if (valueState.eval((ValueExpression) s, node, state.getState())
											.isTainted())
										tool.warnOn(call, "The value passed for the " + StringUtilities.ordinal(i + 1)
												+ " parameter of this call is tainted, and it reaches the sink at parameter '"
												+ parameters[i].getName() + "' of " + resolved.getFullTargetName());
								}
							}

					}
				} else if (resolved instanceof CFGCall) {
					CFGCall cfg = (CFGCall) resolved;
					for (CodeMember n : cfg.getTargets()) {
						Parameter[] parameters = n.getDescriptor().getFormals();
						for (int i = 0; i < parameters.length; i++)
							if (parameters[i].getAnnotations().contains(SINK_MATCHER)) {
								AnalysisState<
										SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomain>,
												TypeEnvironment<InferredTypes>>> state = result
														.getAnalysisStateAfter(call.getParameters()[i]);
								Set<SymbolicExpression> reachableIds = new HashSet<>();
								for (SymbolicExpression e : state.getComputedExpressions())
									reachableIds.addAll(HeapResolver.resolve(state, e, node));

								for (SymbolicExpression s : reachableIds) {
									ValueEnvironment<TaintDomain> valueState = state.getState().getValueState();

									Set<Type> types = state.getState().getRuntimeTypesOf(s, node, state.getState());

									if (types.stream().allMatch(t -> t.isInMemoryType() || t.isPointerType()))
										continue;

									if (valueState.eval((ValueExpression) s, node, state.getState())
											.isTainted())
										tool.warnOn(call, "The value passed for the " + StringUtilities.ordinal(i + 1)
												+ " parameter of this call is tainted, and it reaches the sink at parameter '"
												+ parameters[i].getName() + "' of " + resolved.getFullTargetName());
								}
							}

					}
				}
			}
		} catch (SemanticException e) {
			System.err.println("Cannot check " + node);
			e.printStackTrace(System.err);
		}

		return true;

	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomain>,
							TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Edge edge) {
		return true;
	}

	@Override
	public boolean visitUnit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<TaintDomain>,
							TypeEnvironment<InferredTypes>>> tool,
			Unit unit) {
		return true;
	}

	public static class HeapResolver {

		public static <A extends AbstractState<A>> Collection<SymbolicExpression> resolve(
				AnalysisState<A> entryState,
				SymbolicExpression e,
				Statement pp) throws SemanticException {
			return entryState.getState().reachableFrom(e, pp, entryState.getState()).elements;
		}
	}
}
