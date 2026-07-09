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
import it.unive.lisa.outputs.serializableGraph.SerializableGraph;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;
import it.unive.lisa.util.collections.workset.VisitOnceFIFOWorkingSet;
import it.unive.lisa.util.collections.workset.VisitOnceWorkingSet;
import it.unive.lisa.util.file.FileManager;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A Go Checker for detect multiple event emits in Hyperledger Fabric.
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
	// private final Map<String, ReadWriteGraph> reconstructedGraphs;
	private Set<Pair<Statement, Statement>> multipleEventEmittion;

	/**
	 * Builds the checker.
	 * 
	 * @param computeGraph {@code true} if required the computed graph
	 */
	public MultipleEventEmitChecker(boolean computeGraph) {
		this.computeGraph = computeGraph;
		// this.reconstructedGraphs = new HashMap<>();
		this.multipleEventEmittion = new HashSet<>();
	}

	private void dump(FileManager fileManager, String filename, SerializableGraph graph) throws IOException {
		fileManager.mkDotFile(filename, writer -> graph.toDot().dump(writer));
	}

	@Override
	public void beforeExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool) {
	}

	@Override
	public void afterExecution(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool) {

		for (Pair<Statement, Statement> pair : multipleEventEmittion)
			tool.warnOn(pair.getLeft(),
					"Detected at least a possible multiple event emittion. Other emitted event location: "
							+ pair.getRight().getLocation());

		/*
		 * if (computeGraph) { for (Entry<String, ReadWriteGraph> entry :
		 * reconstructedGraphs.entrySet()) { try { dump(tool.getFileManager(),
		 * entry.getKey(), entry.getValue().toSerializableGraph()); } catch
		 * (IOException e) { LOG.warn("Unable to dump read-write graph \"" +
		 * entry.getKey() + "\". Error: " + e.getMessage()); } } }
		 */
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

	/*
	 * private ReadWriteGraph tmpGraph; private ReadWriteNode destinationNode;
	 */
	private boolean isDeferredCallee;

	private void checkMultipleEventEmittionIssue(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Statement node) {

		List<Call> calls = CFGUtils.extractCallsFromStatement(node);

		boolean found = false;
		for (Call c : calls)
			if (c.getTargetName().equals("SetEvent")
					&& (c.getParameters().length == 2 || c.getParameters().length == 3))
				found = true;

		if (!found)
			return;

		/*
		 * if (computeGraph) tmpGraph = new ReadWriteGraph("MultipleEvent - " +
		 * node.getLocation());
		 */
		if (interproceduralCheck(tool, graph, node, node, new HashSet<CodeMember>(), new HashSet<CodeMember>())) {

			/*
			 * if (computeGraph) reconstructedGraphs.put(tmpGraph.getName(),
			 * tmpGraph);
			 */
		}

	}

	private boolean interproceduralCheck(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Statement root, Statement start, Set<CodeMember> seenCallees, Set<CodeMember> seenCallers) {

		Statement startNode = CFGUtils.extractTargetNodeFromGraph(graph, start);
		startNode = startNode == null ? start : startNode;

		boolean isStartDeferred = startNode instanceof GoDefer;

		Set<Statement> emitEventsNodes = new HashSet<>();
		for (Statement node : graph.getNodeList()) {
			List<Call> calls = CFGUtils.extractCallsFromStatement(node);
			for (Call c : calls)
				if (c.getTargetName().equals("SetEvent")
						&& (c.getParameters().length == 2 || c.getParameters().length == 3))
					emitEventsNodes.add(node);
		}

		for (Statement endNode : emitEventsNodes) {
			boolean isEndDeferred = endNode instanceof GoDefer;

			if (isMatching(graph, startNode, isStartDeferred, endNode, isEndDeferred)) {
				/*
				 * if (computeGraph) { ReadWriteNode node1 = new
				 * ReadWriteNode(tmpGraph, startNode); ReadWriteNode node2 = new
				 * ReadWriteNode(tmpGraph, endNode); tmpGraph.addNode(node1);
				 * tmpGraph.addNode(node2); tmpGraph.addEdge(isEndDeferred ? new
				 * DeferEdge(node1, node2) : new StandardEdge(node1, node2));
				 * destinationNode = node1; }
				 */
				multipleEventEmittion.add(Pair.of(root, endNode));

				return true;
			}

		}

		if (checkCallees(tool, graph, root, startNode, seenCallees, isStartDeferred)) {
			/*
			 * if (computeGraph) { ReadWriteNode node = new
			 * ReadWriteNode(tmpGraph, startNode); tmpGraph.addNode(node);
			 * tmpGraph.addEdge(isDeferredCallee ? new DeferEdge(node,
			 * destinationNode) : new StandardEdge(node, destinationNode));
			 * destinationNode = node; }
			 */
			return true;
		}

		if (checkCallers(tool, graph, root, seenCallees, seenCallers)) {
			/*
			 * if (computeGraph) { ReadWriteNode node = new
			 * ReadWriteNode(tmpGraph, startNode); tmpGraph.addNode(node);
			 * tmpGraph.addEdge(new CallerEdge(node, destinationNode));
			 * destinationNode = node; }
			 */
			return true;
		}

		return false;
	}

	private boolean checkCallees(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Statement root, Statement start, Set<CodeMember> seen, boolean isStartDeferred) {

		if (seen.contains(graph))
			return false;
		seen.add(graph);

		Collection<CodeMember> codemembers = getCalleesTransitively(tool, graph);
		for (CodeMember cm : codemembers) {
			if (cm instanceof VariableScopingCFG) {
				VariableScopingCFG interCFG = (VariableScopingCFG) cm;

				for (Statement n : graph.getNodes()) {
					if (n.equals(start))
						continue;
					List<Call> calls = CFGUtils.extractCallsFromStatement(n);
					if (!calls.isEmpty()) {

						boolean isEndDeferred = n instanceof GoDefer;
						isDeferredCallee = false;
						if (isMatching(graph, start, isStartDeferred, n, isEndDeferred)) {
							for (Call c : calls)
								if (c instanceof UnresolvedCall) {
									if (tool.getCallSites(cm).contains(c)) {
										for (Statement e : interCFG.getEntrypoints())
											if (checkCalleesRecursive(tool, interCFG, e, seen)) {
												/*
												 * if (computeGraph) {
												 * ReadWriteNode node = new
												 * ReadWriteNode(tmpGraph, n);
												 * tmpGraph.addNode(node);
												 * tmpGraph.addEdge(new
												 * CalleeEdge(node,
												 * destinationNode));
												 * destinationNode = node;
												 * isDeferredCallee = n
												 * instanceof GoDefer;
												 * multipleEventEmittion.add(
												 * Pair.of(root, n)); }
												 */

												return true;
											}
									}
								}
						}
					}
				}
			}
		}

		return false;
	}

	private boolean checkCalleesRecursive(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Statement start, Set<CodeMember> seen) {

		if (seen.contains(graph))
			return false;
		seen.add(graph);

		Set<Statement> emitEventsNodes = new HashSet<>();
		for (Statement node : graph.getNodeList()) {
			List<Call> calls = CFGUtils.extractCallsFromStatement(node);
			for (Call c : calls)
				if (c.getTargetName().equals("SetEvent")
						&& (c.getParameters().length == 2 || c.getParameters().length == 3))
					emitEventsNodes.add(node);
		}

		for (Statement endNode : emitEventsNodes) {

			/*
			 * if (computeGraph) { ReadWriteNode node = new
			 * ReadWriteNode(tmpGraph, endNode); tmpGraph.addNode(node);
			 * destinationNode = node; }
			 */
			return true;
		}

		Collection<CodeMember> codemembers = getCalleesTransitively(tool, graph);
		for (CodeMember cm : codemembers) {
			if (cm instanceof VariableScopingCFG) {
				VariableScopingCFG interCFG = (VariableScopingCFG) cm;
				for (Statement endNode : emitEventsNodes) {
					if (CFGUtils.extractTargetNodeFromGraph(graph, endNode) != null)
						for (Statement n : graph.getNodes()) {
							List<Call> calls = CFGUtils.extractCallsFromStatement(n);
							if (!calls.isEmpty()) {
								for (Call c : calls)
									if (c instanceof UnresolvedCall) {
										if (tool.getCallSites(cm).contains(c)) {
											for (Statement e : interCFG.getEntrypoints())
												if (checkCalleesRecursive(tool, interCFG, e, seen)) {
													/*
													 * if (computeGraph) {
													 * ReadWriteNode node = new
													 * ReadWriteNode(tmpGraph,
													 * e);
													 * tmpGraph.addNode(node);
													 * tmpGraph.addEdge(new
													 * CalleeEdge(node,
													 * destinationNode));
													 * destinationNode = node; }
													 */
													return true;
												}
										}
									}
							}
						}
				}

			}
		}
		return false;
	}

	private boolean checkCallers(
			SemanticTool<SimpleAbstractState<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>, TypeEnvironment<T>>,
					SimpleAbstractDomain<HeapEnvironment<H>, ValueEnvironment<RegexAutomaton>,
							TypeEnvironment<T>>> tool,
			CFG graph, Statement root, Set<CodeMember> seenCallees, Set<CodeMember> seenCallers) {

		if (tool.getCallGraph().getNodes().stream().anyMatch(n -> n.getCodeMember().equals(graph))) {

			Collection<CodeMember> callers = tool.getCallers(graph);

			for (CodeMember cm : callers) {
				if (seenCallers.contains(cm))
					return false;
				seenCallers.add(cm);

				for (Call c : tool.getCallSites(graph)) {
					if (cm instanceof VariableScopingCFG) {
						VariableScopingCFG callerCFG = (VariableScopingCFG) cm;
						Statement sTarget = CFGUtils.extractTargetNodeFromGraph(callerCFG, c);
						if (sTarget != null)
							if (interproceduralCheck(tool, callerCFG, root, sTarget, seenCallees, seenCallers)) {
								return true;
							}
					}
				}
			}
		}
		return false;
	}

	private boolean isMatching(CFG graph, Statement startNode, boolean isStartDeferred, Statement endNode,
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