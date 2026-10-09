package it.unive.golisa.checker.hf.events;

import it.unive.golisa.cfg.statement.GoDefer;
import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.golisa.cfg.utils.CFGUtils.Search;
import it.unive.golisa.program.cfg.VariableScopingCFG;
import it.unive.lisa.analysis.SimpleAbstractDomain;
import it.unive.lisa.analysis.nonrelational.heap.HeapEnvironment;
import it.unive.lisa.analysis.nonrelational.heap.HeapValue;
import it.unive.lisa.analysis.nonrelational.type.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.type.TypeValue;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.checks.semantic.SemanticTool;
import it.unive.lisa.lattices.SimpleAbstractState;
import it.unive.lisa.lattices.string.tarsis.RegexAutomaton;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.util.collections.workset.VisitOnceFIFOWorkingSet;
import it.unive.lisa.util.collections.workset.VisitOnceWorkingSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A Go Checker for detect multiple event emits in Hyperledger Fabric. Including also self-multiple event emits
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 * 
 * @param <H> the lattice that represents a property of the memory of the
 *                program
 * @param <T> the lattice that represents a set of types corresponding to the
 *                runtime types of an expression
 */
public class MultipleEventEmitChecker<H extends HeapValue<H>, T extends TypeValue<T>> implements
		SemanticCheck<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
				SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> {

	private static final Logger LOG = LogManager.getLogger(MultipleEventEmitChecker.class);

	private final boolean computeGraph;
	private Set<Pair<Statement, Statement>> multipleEventEmittion;
	
	private Set<CodeMember> eventTriggerCFGs;

	/**
	 * Builds the checker.
	 * 
	 * @param computeGraph {@code true} if required the computed graph
	 */
	public MultipleEventEmitChecker(boolean computeGraph) {
		this.computeGraph = computeGraph;
		this.multipleEventEmittion = new HashSet<>();
	}

	@Override
	public void beforeExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool) {
		eventTriggerCFGs = new HashSet<>();
	}

	@Override
	public void afterExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool) {

		for (Pair<Statement, Statement> pair : multipleEventEmittion)
			tool.warnOn(pair.getLeft(),
					"Detected at least a possible multiple event emittion. " +  (pair.getLeft().equals(pair.getRight()) ? "There is at least an execution flow that trigger the same event emission multiple time." : "Other emitted event location: "
							+ pair.getRight().getLocation()));
	}

	@Override
	public boolean visitUnit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			Unit unit) {
		return true;
	}

	@Override
	public void visitGlobal(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			Unit unit, Global global, boolean instance) {
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Edge edge) {
		return true;
	}

	@Override
	public boolean visit(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Statement node) {
		List<Call> calls = CFGUtils.extractCallsFromStatement(node);
		
		if (calls.isEmpty())
			return true;

		checkMultipleEventEmittionIssue(tool, graph, node);

		return true;
	}

	private void checkMultipleEventEmittionIssue(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Statement node) {

		List<Call> calls = CFGUtils.extractCallsFromStatement(node);

		boolean found = false;
		for (Call c : calls)
			if (isSetEventCall(c))
				found = true;

		if (!found)
			return;

		// node contains a SetEvent call
		Set<CodeMember> seenCallers = new HashSet<>();
		
		checkForMultipleEmissions(tool, node, graph, seenCallers, node);

	}
	
	private void checkForMultipleEmissions(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			Statement eventEmitter, CFG graph, Set<CodeMember> seenCallers, Statement firstEventEmit) {
		
		eventTriggerCFGs.add(graph);
		
		if(!intraproceduralCheck(tool, eventEmitter, graph, firstEventEmit)) // checks for multiple event emission in the same CFG
			interproceduralChecks(tool, eventEmitter, graph, seenCallers, firstEventEmit); // checks in other functions
		
	}

	private boolean isSetEventCall(Call c) {
		return c.getTargetName().equals("SetEvent")
				&& (c.getParameters().length == 2 || c.getParameters().length == 3);
	}

	private boolean intraproceduralCheck(SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool, Statement first, CFG graph, Statement firstEventEmit) {
		Set<Statement> statements = extractAllSetEventsOrEventTriggerCalls(graph);
		
		for(Statement second : statements) {

			if (isExecutedAfter(graph, first, second)) {
				multipleEventEmittion.add(Pair.of(firstEventEmit, containsSetEventCall(second) ? second : firstEventEmit));
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the second statement is executed after the first
	 * @param graph the CFG to check
	 * @param first the first statement
	 * @param second the second statement
	 * @return true, if the second is executed after the first
	 */
	private boolean isExecutedAfter(CFG graph, Statement first, Statement second) {
		boolean isStartDeferred = first instanceof GoDefer;
		boolean isEndDeferred = second instanceof GoDefer;

		if (existExecutionPath(graph, first, isStartDeferred, second, isEndDeferred)) {
			return true;
		}
		
		return false;
	}

	private Set<Statement> extractAllSetEventsOrEventTriggerCalls(CFG graph) {
		Set<Statement> res = new HashSet<>();
		for(Statement node : graph.getNodes()) {
			List<Call> calls = CFGUtils.extractCallsFromStatement(node);
			for (Call c : calls)
				if (isSetEventCall(c) || isCallToEventTriggerCFGs(c)) {
					res.add(node);
					break;
				}
		}

		return res;
	}
	
	private boolean containsSetEventCall(Statement node) {
		List<Call> calls = CFGUtils.extractCallsFromStatement(node);
		for (Call c : calls)
			if (isSetEventCall(c)) {
				return true;
			}
		return false;
	}
	
	private boolean containsCallToEventTriggerCFGs(Statement node) {
		List<Call> calls = CFGUtils.extractCallsFromStatement(node);
		for (Call c : calls)
			if (isCallToEventTriggerCFGs(c)) {
				return true;
			}
		return false;
	}
	
	private boolean isCallToEventTriggerCFGs(Call c) {
		for(CodeMember cm : eventTriggerCFGs) {
			if(c.getTargetName().equals(cm.getDescriptor().getName())
					&&  c.getParameters().length == cm.getDescriptor().getFormals().length){
				return true;
			}
		}
		return false;
	}

	private void interproceduralChecks(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			Statement eventEmitter, CFG graph, Set<CodeMember> seenCallers, Statement firstEventEmit) {
		
		if (!checkCalleesAfter(tool, graph, eventEmitter, firstEventEmit))
			 checkCallersAfterCallStatements(tool, graph, eventEmitter, seenCallers, firstEventEmit);
	}

	private boolean checkCalleesAfter(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			CFG graph, Statement eventEmitter, Statement firstEventEmit) {
		Collection<CodeMember> codemembers = getCalleesTransitively(tool, graph);
	
		for(Statement node : graph.getNodes()) {
			List<Call> calls = CFGUtils.extractCallsFromStatement(firstEventEmit);
			if(!calls.isEmpty()) 
				for (CodeMember cm : codemembers) {	
					if (cm instanceof VariableScopingCFG && atLeastOneCallMatchesCodeMember(calls,cm)
						&& isExecutedAfter(graph, firstEventEmit, node)) {
						VariableScopingCFG callCFG = (VariableScopingCFG) cm;
						//check if contains the SetEvent, then trigger a waning
						Set<Statement> secondEmittions = extractAllSetEventsOrEventTriggerCalls(callCFG);
						if(!secondEmittions.isEmpty())
							for(Statement second : secondEmittions)
								multipleEventEmittion.add(Pair.of(firstEventEmit, containsSetEventCall(second) ? second : firstEventEmit));
						else {  //if not found continue to check callees recursively (sub-calls)
							Set<CodeMember> seen = new HashSet<>();
							checkCallees(tool, graph, eventEmitter, seen, firstEventEmit);
						}
					}
				}
		}
		return false;
	}

	private boolean atLeastOneCallMatchesCodeMember(List<Call> calls, CodeMember cm) {
		for(Call c : calls) {
			if(c.getTargetName().equals(cm.getDescriptor().getName())
					&&  c.getParameters().length == cm.getDescriptor().getFormals().length){
				return true;
			}
		}
		return false;
	}

	private void checkCallees(SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			CFG graph, Statement eventEmitter, Set<CodeMember> seen, Statement firstEventEmit) {
		if(seen.contains(graph))
			return;
		else
			seen.add(graph);
		
		Collection<CodeMember> codemembers = getCalleesTransitively(tool, graph);
		for (CodeMember cm : codemembers) {
			if (cm instanceof VariableScopingCFG) {
				VariableScopingCFG callCFG = (VariableScopingCFG) cm;
				//check if contains the SetEvent, then trigger a waning
				Set<Statement> secondEmittions = extractAllSetEventsOrEventTriggerCalls(callCFG);
				if(!secondEmittions.isEmpty())
					for(Statement second : secondEmittions)
						multipleEventEmittion.add(Pair.of(firstEventEmit, containsSetEventCall(second) ? second : firstEventEmit));
				else { //if not found continue to check callees recursively
					checkCallees(tool, graph, eventEmitter, seen, firstEventEmit);
				}
			}
		}
	}
	
	
	private void checkCallersAfterCallStatements(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>, SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>> tool,
			CFG graph, Statement eventEmitter, Set<CodeMember> seenCallers, Statement firstEventEmit) {
	
		if (tool.getCallGraph().getNodes().stream().anyMatch(n -> n.getCodeMember().equals(graph))) {

			Collection<CodeMember> callers = tool.getCallers(graph);
			
			for (CodeMember cm : callers) {
				if (seenCallers.contains(cm))
					return;
				seenCallers.add(cm);
				
				for (Call c : tool.getCallSites(graph)) { // yields calling points as start , and rerun checks recursively
					if (cm instanceof VariableScopingCFG && c.getCFG().equals(cm)) {
						VariableScopingCFG callerCFG = (VariableScopingCFG) cm;
						Statement sTarget = CFGUtils.extractTargetNodeFromGraph(callerCFG, c);
						if (sTarget != null)
							checkForMultipleEmissions(tool, sTarget, callerCFG, seenCallers, firstEventEmit);
					}
				}
			}
		}
		
	}

	private boolean existExecutionPath(CFG graph, Statement startNode, boolean isStartDeferred, Statement endNode,
			boolean isEndDeferred) {

		if (!isStartDeferred && !isEndDeferred) {
			if (CFGUtils.existPathWithAtLeastOneEdge(graph, startNode, endNode, Search.DFS))
				return true;
		}

		if (isEndDeferred && isStartDeferred)
			if (CFGUtils.existPathWithAtLeastOneEdge(graph, endNode, startNode, Search.BFS))
				return true;

		if (!isStartDeferred && isEndDeferred)
			if (CFGUtils.existPathWithAtLeastOneEdge(graph, startNode, endNode, Search.BFS))
				return true;

		if (isEndDeferred && !isStartDeferred)
			if (CFGUtils.existPathWithAtLeastOneEdge(graph, endNode, startNode, Search.BFS))
				return true;

		return false;
	}

	/**
	 * Compute the callees.
	 * 
	 * @param tool the semantic tool
	 * @param cm   the code memeber
	 * 
	 * @return the computed callees
	 */
	public Collection<CodeMember> getCalleesTransitively(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CodeMember cm) {
		VisitOnceFIFOWorkingSet<CodeMember> instance = new VisitOnceFIFOWorkingSet<>();
		VisitOnceWorkingSet<CodeMember> ws = instance.mk();
		if (tool.getCallGraph().getNodes().stream().anyMatch(n -> n.getCodeMember().equals(cm))) {
			tool.getCallees(cm).stream().forEach(ws::push);
			while (!ws.isEmpty())
				tool.getCallees(ws.pop()).stream().forEach(ws::push);
		}
		return ws.getSeen();

	}

}