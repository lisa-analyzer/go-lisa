package it.unive.golisa.checker.hf;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.analysis.tarsis.utils.TarsisUtils;
import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.golisa.checker.hf.cci.CrossContractInvocationInformation;
import it.unive.lisa.analysis.Analysis;
import it.unive.lisa.analysis.AnalyzedCFG;
import it.unive.lisa.analysis.SemanticException;
import it.unive.lisa.analysis.SemanticOracle;
import it.unive.lisa.analysis.SimpleAbstractDomain;
import it.unive.lisa.analysis.nonrelational.heap.HeapEnvironment;
import it.unive.lisa.analysis.nonrelational.heap.HeapValue;
import it.unive.lisa.analysis.nonrelational.type.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.type.TypeValue;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.checks.semantic.SemanticTool;
import it.unive.lisa.lattices.SimpleAbstractState;
import it.unive.lisa.lattices.string.tarsis.RegexAutomaton;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.Parameter;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.NativeCall;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.symbolic.SymbolicExpression;
import it.unive.lisa.symbolic.value.ValueExpression;

/**
 * A Go Checker for the detection of different cross-channel invocations in
 * Hyperledger Fabric.
 *
 * @param <H> the lattice that represents a property of the memory of the program
 * @param <T> the lattice that represents a set of types corresponding to the runtime types of an expression
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class CrossChannelInvocationsIssuesChecker<H extends HeapValue<H>, T extends TypeValue<T>> implements
SemanticCheck<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> {

	private Map<Statement, CrossContractInvocationInformation> crossContractInvocations;
	private Map<Statement, CrossContractInvocationInformation> crossChannelInvocations;

	
	private boolean intraChaincode = true;
	private String channelName; // Name of the channel provided by the command line
	
	/**
	 * Builds an instance of the checker.
	 */
	public CrossChannelInvocationsIssuesChecker() {
		channelName= null;
	}
	
	/**
	 * Builds an instance of the checker.
	 * @param intraChaincode if {@code true} only intra chaincode checks
	 * @param channelName the channel name
	 */
	public CrossChannelInvocationsIssuesChecker(boolean intraChaincode, String channelName) {
		this.intraChaincode = intraChaincode;
		this.channelName= channelName;
	}

	@Override
	public void beforeExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool) {
		crossContractInvocations = new HashMap<>();
		crossChannelInvocations = new HashMap<>();
	}

	@Override
	public void afterExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool) {
		
		Set<Statement> singleCrossChannelInvocations = new HashSet<>();
		
		//CASE 1: single CCIs with arbitrary channel
		Statement[] invocations = crossContractInvocations.keySet().toArray(new Statement[] {});
		for (int i = 0; i < invocations.length; i++) {
			Set<RegexAutomaton> stringApproximations = crossContractInvocations.get(invocations[i]).getChannelApproximations();
			CrossContractInvocationInformation cchiInfo = new CrossContractInvocationInformation(); 
			boolean found =false;
			for(RegexAutomaton t : stringApproximations) {
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
		Map<Statement, Set<Statement>> multipleCrossChannelInvocations = new HashMap<>();
		for (int i = 0; i < invocations.length - 1; i++) {
			for (int j = i + 1; j < invocations.length; j++) {
				boolean isDiff = false;
				Set<RegexAutomaton> t1Set = crossContractInvocations.get(invocations[i]).getChannelApproximations();
				Set<RegexAutomaton> t2Set = crossContractInvocations.get(invocations[j]).getChannelApproximations();
				for (RegexAutomaton t1 : t1Set) {
					for (RegexAutomaton t2 : t2Set) {
						if(!t1.isTop() && !t2.isTop()
								&& (TarsisUtils.possibleEqualsMatch(t1, t2) || (TarsisUtils.extractValueStringFromTarsisStates(t1) != null
										&& TarsisUtils.extractValueStringFromTarsisStates(t2) != null &&
										TarsisUtils.extractValueStringFromTarsisStates(t1)
												.equals(TarsisUtils.extractValueStringFromTarsisStates(t2)))))
							continue;
						isDiff = true;
						break;

					}
					if (isDiff)
						break;
				}

				if (isDiff) {
					if(!multipleCrossChannelInvocations.containsKey(invocations[i]))
						multipleCrossChannelInvocations.put(invocations[i], new HashSet<>());
					multipleCrossChannelInvocations.get(invocations[i]).add(invocations[j]);
					
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
		
		Map<Statement, Set<Statement>> result = reduceForWarnings(multipleCrossChannelInvocations);
		for(Statement target : result.keySet()) {
		  Set<Statement> others = result.get(target);
		  tool.warnOn(target,
		      "Detected cross-channel invocations on different channels. The other invocations: "
		          + printOtherLocations(others) + ". They may lead to a lack of transparency because no new transactions are created during the invocation.");
		  if(intraChaincode)
		    tool.warnOn(target,
		        "Detected cross-channel invocations on different channels. The other invocations: "
		            + printOtherLocations(others) + ". They may lead to uncommited write operations during the execution of callee chaincode.");
		}
	}

	private Map<Statement, Set<Statement>> reduceForWarnings(
			Map<Statement, Set<Statement>> multipleCrossChannelInvocations) {
		Set<Pair<Statement, Statement>> tmp = new HashSet<>();
		
		for(Entry<Statement, Set<Statement>> entry :multipleCrossChannelInvocations.entrySet()) {
			Statement cci1 = entry.getKey();
			for(Statement cci2 :entry.getValue()) {
				if(!tmp.contains(Pair.of(cci1, cci2)) && !tmp.contains(Pair.of(cci2, cci1)))
					tmp.add(Pair.of(cci1, cci2));
			}
		}
		
		Map<Statement, Set<Statement>> result = new HashMap<>();
		for(Pair<Statement, Statement> pair : tmp) {
			if(!result.containsKey(pair.getLeft()))
				result.put(pair.getLeft(), new HashSet<>());
			result.get(pair.getLeft()).add(pair.getRight());
		}
		
		return result;
	}

	private String printOtherLocations(Set<Statement> others) {
	  String result = "";
	  Iterator<Statement> iter = others.iterator();
	  while(iter.hasNext()) {
	    Statement cchi = iter.next();
		result += cchi.getLocation();
		if(iter.hasNext())
			result += ", ";
	  }
	  return result;
	}

	

	@Override
	public boolean visitUnit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			Unit unit) {
		return true;
	}
	
	

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			CFG graph, Edge edge) {
		return true;
	}

	@Override
	public void visitGlobal(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			Unit unit, Global global, boolean instance) {
	}

	

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			CFG graph, Statement node) {

		List<Call> calls = CFGUtils.extractCallsFromStatement(node);
		if (calls.isEmpty())
			return true;

		for (Call call : calls) {
			if (call.getTargetName().equals("InvokeChaincode")) {
				try {

					for (var result : tool.getResultOf(call.getCFG())) {
						Call resolved = call instanceof UnresolvedCall
								? (Call) tool.getResolvedVersion((UnresolvedCall) call, result)
								: call;
						Set<RegexAutomaton> channelValues = null;
						Set<RegexAutomaton> contractNameValues = null;
						
						if (resolved instanceof NativeCall) {
							NativeCall nativeCfg = (NativeCall) resolved;
							Collection<CodeMember> nativeCfgs = nativeCfg.getTargets();
							for (CodeMember n : nativeCfgs) {
								Parameter[] parameters = n.getDescriptor().getFormals();
								channelValues = extractChannelValues(tool.getAnalysis(), call, parameters.length, node, result);
								contractNameValues = extractContractNameValues(tool.getAnalysis(), call, parameters.length, node, result);
							}
						} else if (resolved instanceof CFGCall) {
							CFGCall cfg = (CFGCall) resolved;

							for (CodeMember n : cfg.getTargets()) {
								Parameter[] parameters = n.getDescriptor().getFormals();
								channelValues = extractChannelValues(tool.getAnalysis(), call, parameters.length, node, result);
								contractNameValues = extractContractNameValues(tool.getAnalysis(), call, parameters.length, node, result);
							}
						} else {
							channelValues = extractChannelValues(tool.getAnalysis(), call, call.getParameters().length, node, result);
							contractNameValues = extractContractNameValues(tool.getAnalysis(), call, call.getParameters().length, node, result);
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

	private Set<RegexAutomaton> extractContractNameValues(Analysis<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> analysis, Call call, int parametersLength, Statement node,
			AnalyzedCFG<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> result) 
					throws SemanticException {
		int par = 1;
		Set<RegexAutomaton> res = new HashSet<>();
		if (par < parametersLength) {
			var state = result.getAnalysisStateAfter(call.getParameters()[par]);
			for (SymbolicExpression stack : analysis.rewrite(state, state.getExecutionExpressions(), node)) {
				var valueState = state.getExecutionState().valueState;
				Tarsis analysisValueDomain = (Tarsis) analysis.domain.valueDomain;
				SemanticOracle oracle = analysis.domain.makeOracle(state.getExecutionState());
				var value = analysisValueDomain.eval(valueState, (ValueExpression) stack, (ProgramPoint) node, oracle);
				res.add(value);
			}
		}

		return res;
	}

	private Set<RegexAutomaton> extractChannelValues(Analysis<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> analysis, Call call, int parametersLength, Statement node, AnalyzedCFG<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> result)
			throws SemanticException {

		int par = 3;
		Set<RegexAutomaton> res = new HashSet<>();
		if (par < parametersLength) {

			var state = result.getAnalysisStateAfter(call.getParameters()[par]);
			
			for(SymbolicExpression stack : analysis.rewrite(state, state.getExecutionExpressions(), node)) {
				var valueState = state.getExecutionState().valueState;
				Tarsis analysisValueDomain = (Tarsis) analysis.domain.valueDomain;
				SemanticOracle oracle = analysis.domain.makeOracle(state.getExecutionState());
				var value = analysisValueDomain.eval(valueState, (ValueExpression) stack, (ProgramPoint) node, oracle);
				res.add(value);
			}
		}
		return res;
	}

	/**
	 * Yields the cross-channel invocations.
	 * @return the cross-channel invocations
	 */
	public Map<Statement, CrossContractInvocationInformation> getCrossChannelInvocations() {
		return crossChannelInvocations;
	}
	
}
