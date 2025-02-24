package it.unive.golisa.checker.hf;

import it.unive.golisa.analysis.tarsis.utils.TarsisUtils;
import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.golisa.checker.hf.cci.CrossContractInvocationInformation;
import it.unive.lisa.analysis.AnalysisState;
import it.unive.lisa.analysis.AnalyzedCFG;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.RegexAutomaton;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.program.Global;
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
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.ValueExpression;
import it.unive.lisa.util.datastructures.automaton.State;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

/**
 * A Go Checker for the detection of different cross-channel invocations in
 * Hyperledger Fabric.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class CrossChannelInvocationsIssuesChecker implements
		SemanticCheck<
				SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> {

	private Map<Statement, CrossContractInvocationInformation> crossContractInvocations;
	private Map<Statement, CrossContractInvocationInformation> crossChannelInvocations;

	
	private boolean intraChaincode = true;
	private String channelName; // Name of the channel provided by the command line
	
	public CrossChannelInvocationsIssuesChecker() {
		channelName= null;
	}
	
	public CrossChannelInvocationsIssuesChecker(boolean intraChaincode, String channelName) {
		this.intraChaincode = intraChaincode;
		this.channelName= channelName;
	}
	@Override
	public void beforeExecution(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool) {
		crossContractInvocations = new HashMap<>();
		crossChannelInvocations = new HashMap<>();
	}

	@Override
	public void afterExecution(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>,
							TypeEnvironment<InferredTypes>>> tool) {
		
		Set<Statement> singleCrossChannelInvocations = new HashSet<>();
		
		//CASE 1: single CCIs with arbitrary channel
		Statement[] invocations = crossContractInvocations.keySet().toArray(new Statement[] {});
		for (int i = 0; i < invocations.length; i++) {
			Set<Tarsis> stringApproximations = crossContractInvocations.get(invocations[i]).getChannelApproximations();
			CrossContractInvocationInformation cchiInfo = new CrossContractInvocationInformation(); 
			boolean found =false;
			for(Tarsis t : stringApproximations) {
				if(CchiUtils.mayCrossChannel(channelName, t)) {
					singleCrossChannelInvocations.add(invocations[i]);
					CrossContractInvocationInformation cciInfo = crossContractInvocations.get(invocations[i]);
					cchiInfo.addAllChannelApproximations(cciInfo.getChannelApproximations());
					cchiInfo.addAllContractNameApproximations(cciInfo.getContractNameApproximations());
					found = true;
				}
			}
			if(found)
				crossChannelInvocations.put(invocations[i], cchiInfo);	
		}
		
		for(Statement cch : singleCrossChannelInvocations) {
			tool.warnOn(cch, "Detected possible cross-channel invocation. It may lead to a lack of transparency because no new transactions are created during the invocation.");
			if(intraChaincode)
				tool.warnOn(cch, "Detected possible cross-channel invocation. It may lead to uncommited write operations during the execution of callee chaincode.");
			
		}
		
		//CASE 2: CCIs with different channels
		Set<Pair<Statement,Statement>> multipleCrossChannelInvocations  = new HashSet<>();
		
		boolean isDiff = false;
		for (int i = 0; i < invocations.length - 1; i++) {
			for (int j = i + 1; j < invocations.length; j++) {
				Set<Tarsis> t1Set = crossContractInvocations.get(invocations[i]).getChannelApproximations();
				Set<Tarsis> t2Set = crossContractInvocations.get(invocations[j]).getChannelApproximations();
				for (Tarsis t1 : t1Set) {
					for (Tarsis t2 : t2Set) {
						if (t1.isTop() || t2.isTop()
								|| !TarsisUtils.possibleEqualsMatch(t1, t2)
								|| (TarsisUtils.extractValueStringFromTarsisStates(t1) != null
										&& TarsisUtils.extractValueStringFromTarsisStates(t2) != null &&
										!TarsisUtils.extractValueStringFromTarsisStates(t1)
												.equals(TarsisUtils.extractValueStringFromTarsisStates(t2)))) {
							isDiff = true;
							break;
						}
					}
					if (isDiff)
						break;
				}

				if (isDiff) {
					if(!multipleCrossChannelInvocations.contains(Pair.of(invocations[j], invocations[i])))
						multipleCrossChannelInvocations.add(Pair.of(invocations[i], invocations[j]));
					if(!crossChannelInvocations.containsKey(invocations[i]))
						crossChannelInvocations.put(invocations[i], new CrossContractInvocationInformation());
					crossChannelInvocations.get(invocations[i]).addAllChannelApproximations(crossContractInvocations.get(invocations[i]).getChannelApproximations());
					crossChannelInvocations.get(invocations[i]).addAllContractNameApproximations(crossContractInvocations.get(invocations[i]).getContractNameApproximations());
					if(!crossChannelInvocations.containsKey(invocations[j]))
						crossChannelInvocations.put(invocations[j], new CrossContractInvocationInformation());
					crossChannelInvocations.get(invocations[j]).addAllChannelApproximations(crossContractInvocations.get(invocations[j]).getChannelApproximations());
					crossChannelInvocations.get(invocations[j]).addAllContractNameApproximations(crossContractInvocations.get(invocations[j]).getContractNameApproximations());
				}
					
			}
		}
		
		for( Pair<Statement, Statement> cchs : multipleCrossChannelInvocations) {
			tool.warnOn(cchs.getLeft(),
					"Detected cross-channel invocations on different channels. The other invocation: "
							+ cchs.getRight().getLocation() + ". They may lead to a lack of transparency because no new transactions are created during the invocation.");
			if(intraChaincode)
				tool.warnOn(cchs.getLeft(),
						"Detected cross-channel invocations on different channels. The other invocation: "
								+ cchs.getRight().getLocation() + ". They may lead to uncommited write operations during the execution of callee chaincode.");
		}

	}

	@Override
	public void visitGlobal(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			Unit unit, Global global, boolean instance) {
	}

	@Override
	public boolean visit(CheckToolWithAnalysisResults<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {

		List<Call> calls = CFGUtils.extractCallsFromStatement(node);
		if (calls.isEmpty())
			return true;

		for (Call call : calls) {
			if (call.getTargetName().equals("InvokeChaincode")) {
				try {

					for (AnalyzedCFG<
							SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>,
									TypeEnvironment<InferredTypes>>> result : tool.getResultOf(call.getCFG())) {
						Call resolved = call instanceof UnresolvedCall
								? (Call) tool.getResolvedVersion((UnresolvedCall) call, result)
								: call;
						Set<Tarsis> channelValues = null;
						Set<Tarsis> contractNameValues = null;
						
						if (resolved instanceof NativeCall) {
							NativeCall nativeCfg = (NativeCall) resolved;
							Collection<CodeMember> nativeCfgs = nativeCfg.getTargets();
							for (CodeMember n : nativeCfgs) {
								Parameter[] parameters = n.getDescriptor().getFormals();
								channelValues = extractChannelValues(call, parameters.length, node, result);
								contractNameValues = extractContractNameValues(call, parameters.length, node, result);
							}
						} else if (resolved instanceof CFGCall) {
							CFGCall cfg = (CFGCall) resolved;

							for (CodeMember n : cfg.getTargets()) {
								Parameter[] parameters = n.getDescriptor().getFormals();
								channelValues = extractChannelValues(call, parameters.length, node, result);
								contractNameValues = extractContractNameValues(call, parameters.length, node, result);
							}
						} else {
							channelValues = extractChannelValues(call, call.getParameters().length, node, result);
							contractNameValues = extractContractNameValues(call, call.getParameters().length, node, result);
						}

						crossContractInvocations.putIfAbsent(call, new CrossContractInvocationInformation());
						if (channelValues != null)
							crossContractInvocations.get(call).addAllChannelApproximations(channelValues);
						if(contractNameValues != null)
							crossContractInvocations.get(call).addAllContractNameApproximations(contractNameValues);
					}

				} catch (SemanticException e) {
					System.err.println("Cannot check " + node);
					e.printStackTrace(System.err);
				}
			}
		}
		return true;
	}

	private Set<Tarsis> extractContractNameValues(Call call, int parametersLength, Statement node,
			AnalyzedCFG<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> result) 
					throws SemanticException {
		int par = 1;
		Set<Tarsis> res = new HashSet<>();
		if (par < parametersLength) {

			AnalysisState<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>,
							TypeEnvironment<InferredTypes>>> state = result
									.getAnalysisStateAfter(call.getParameters()[par]);
			for (SymbolicExpression stack : state.getState().rewrite(state.getComputedExpressions(), node,
					state.getState())) {
				res.add(state.getState().getValueState().eval((ValueExpression) stack, node, state.getState()));
			}
		}

		return res;
	}

	private Set<Tarsis> extractChannelValues(Call call, int parametersLength, Statement node, AnalyzedCFG<
			SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> result)
			throws SemanticException {

		int par = 3;
		Set<Tarsis> res = new HashSet<>();
		if (par < parametersLength) {

			AnalysisState<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>,
							TypeEnvironment<InferredTypes>>> state = result
									.getAnalysisStateAfter(call.getParameters()[par]);
			for (SymbolicExpression stack : state.getState().rewrite(state.getComputedExpressions(), node,
					state.getState())) {
				res.add(state.getState().getValueState().eval((ValueExpression) stack, node, state.getState()));
			}
		}

		return res;

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

	public Map<Statement, CrossContractInvocationInformation> getCrossChannelInvocations() {
		return crossChannelInvocations;
	}
	
}
