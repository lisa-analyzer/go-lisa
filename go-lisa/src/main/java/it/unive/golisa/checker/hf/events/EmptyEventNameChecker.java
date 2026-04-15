package it.unive.golisa.checker.hf.events;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.AnalyzedCFG;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.NativeCall;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.ValueExpression;

/**
 * A Go checker for event issues in Hyperledger Fabric. It detects event emitted
 * with an empty string name, i.e. that leads to an error during the chaincode
 * execution.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class EmptyEventNameChecker implements
		SemanticCheck<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> {

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {

		List<Call> calls = CFGUtils.extractCallsFromStatement(node);
		if (calls.isEmpty())
			return true;

		for (Call call : calls) {
			if (call.getTargetName().equals("SetEvent") && call.getParameters().length == 2) {
				try {
					for (AnalyzedCFG<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> result : tool
							.getResultOf(call.getCFG())) {
						Call resolved = call instanceof UnresolvedCall
								? (Call) tool.getResolvedVersion((UnresolvedCall) call, result)
								: call;
						Set<Tarsis> nameValues = new HashSet<>();

						if (resolved instanceof NativeCall) {
							NativeCall nativeCfg = (NativeCall) resolved;
							Collection<CodeMember> nativeCfgs = nativeCfg.getTargets();
							for (CodeMember n : nativeCfgs) {
								Parameter[] parameters = n.getDescriptor().getFormals();
								nameValues = extractNameValues(call, 0, parameters.length, node, result);
							}
						} else if (resolved instanceof CFGCall) {
							CFGCall cfg = (CFGCall) resolved;

							for (CodeMember n : cfg.getTargets()) {
								Parameter[] parameters = n.getDescriptor().getFormals();
								nameValues = extractNameValues(call, 0, parameters.length, node, result);
							}
						} else {
							nameValues = extractNameValues(call, 0, call.getParameters().length, node, result);
						}

						for (Tarsis val : nameValues) {
							if (val.getAutomaton().isEqualTo(val.getAutomaton().emptyString())) {
								tool.warnOn(node, "The event name is an empty string");
							} else if (val.isTop() && val.getAutomaton().getStates().size() == 1) {
								if(val.getAutomaton().getTransitions().size() == 0) 
									tool.warnOn(node, "The event may set an empty name string");
								else // added to be sound, but it may be a non empty combined string 
									tool.warnOn(node, "The event name may be an empty string");
							}
						}
					}

				} catch (SemanticException e) {
					System.err.println("Cannot check " + node);
					e.printStackTrace(System.err);
				}
			}
		}
		return true;
	}

	private Set<Tarsis> extractNameValues(Call call, int nameParam, int parametersLength, Statement node,
			AnalyzedCFG<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> result)
			throws SemanticException {

		Set<Tarsis> valStringDomain = new HashSet<>();

		int par = call.getCallType().equals(CallType.STATIC) ? nameParam : nameParam + 1;
		if (par < parametersLength) {

			AnalysisState<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> state = result
					.getAnalysisStateAfter(call.getParameters()[par]);
			for (SymbolicExpression stack : state.getState().rewrite(state.getComputedExpressions(), node,
					state.getState())) {
				valStringDomain
						.add(state.getState().getValueState().eval((ValueExpression) stack, node, state.getState()));
			}
		}

		return valStringDomain;

	}
	
	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph) {
		return true;
	}
	
	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Edge edge) {
		return true;
	}

	@Override
	public boolean visitUnit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			Unit unit) {
		return true;
	}
}
