package it.unive.golisa.checker.hf.readwrite;

import it.unive.golisa.cfg.VariableScopingCFG;
import it.unive.golisa.cfg.statement.GoDefer;
import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.golisa.cfg.utils.CFGUtils.Search;
import it.unive.golisa.checker.hf.readwrite.graph.ReadWriteGraph;
import it.unive.golisa.checker.hf.readwrite.graph.ReadWriteNode;
import it.unive.golisa.checker.hf.readwrite.graph.edges.CalleeEdge;
import it.unive.golisa.checker.hf.readwrite.graph.edges.CallerEdge;
import it.unive.golisa.checker.hf.readwrite.graph.edges.DeferEdge;
import it.unive.golisa.checker.hf.readwrite.graph.edges.StandardEdge;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A Go Checker for Read-Write Set Issues of Hyperledger Fabric. Note that
 * Read-Write Set Issues analysis is split in two checkers/phases. This is the
 * 2nd phase.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class ReadWritePathChecker implements
		SemanticCheck<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> {

	private static final Logger LOG = LogManager.getLogger(ReadWritePathChecker.class);

	/**
	 * Set of candidates to check for read after write issues.
	 */
	private final Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> readAfterWriteCandidates;

	/**
	 * Set of candidates to check for over-write issues.
	 */
	private final Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> overWriteCandidates;

	private final boolean computeGraph;
	private final Map<String, ReadWriteGraph> reconstructedGraphs;

	public ReadWritePathChecker(Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> readAfterWriteCandidates,
			Set<Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo>> overWriteCandidates, boolean computeGraph) {
		this.readAfterWriteCandidates = readAfterWriteCandidates;
		this.overWriteCandidates = overWriteCandidates;
		this.computeGraph = true;
		this.reconstructedGraphs = new HashMap<>();
	}

	@Override
	public void beforeExecution(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>,
					TypeEnvironment<InferredTypes>>> tool) {
	}

	@Override
	public void afterExecution(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>,
					TypeEnvironment<InferredTypes>>> tool) {

		if (computeGraph) {
			for (Entry<String, ReadWriteGraph> entry : reconstructedGraphs.entrySet()) {
				try {

					dump(tool.getFileManager(), entry.getKey(), entry.getValue().toSerializableGraph());
				} catch (IOException e) {
					LOG.warn("Unable to dump read-write graph \"" + entry.getKey() + "\". Error: " + e.getMessage());
				}
			}
		}
	}

	/**
	 * Dump a path graph in .DOT format
	 */
	private void dump(FileManager fileManager, String filename, SerializableGraph graph) throws IOException {
		fileManager.mkDotFile(filename, writer -> graph.toDot().dump(writer));
	}

	@Override
	public void visitGlobal(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			Unit unit, Global global, boolean instance) {
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
			CFG graph, Statement node) {

		List<Call> calls = CFGUtils.extractCallsFromStatement(node);
		if (calls.isEmpty())
			return true;

		checkReadAfterWriteIssues(tool, graph, node);

		checkOverWriteIssue(tool, graph, node);

		return true;
	}

	private ReadWriteGraph tmpGraph;
	private ReadWriteNode destinationNode;
	private boolean isDeferredCallee;

	/**
	 * Checks the read after write candidates and triggers a warning in case of
	 * issue detection.
	 */
	private void checkReadAfterWriteIssues(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {
		for (Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo> p : readAfterWriteCandidates) {
			if (CFGUtils.equalsOrContains(node, p.getLeft().getCall())) {
				AnalysisReadWriteHFInfo write = p.getLeft();
				AnalysisReadWriteHFInfo read = p.getRight();
				if (computeGraph)
					tmpGraph = new ReadWriteGraph("ReadAfterWrite - Write location: " + write.getCall().getLocation()
							+ " - Read Location -" + read.getCall().getLocation());
				if (interproceduralCheck(tool, graph, write.getCall(), read.getCall(), new HashSet<CodeMember>(),
						new HashSet<CodeMember>())) {
					reconstructedGraphs.put(tmpGraph.getName(), tmpGraph);
					tool.warnOn(node, "Detected a possible read after write issue. Read location: "
							+ p.getRight().getCall().getLocation());
				}
			}

		}

	}

	/**
	 * Checks the over-write candidates and triggers a warning in case of issue
	 * detection.
	 */
	private void checkOverWriteIssue(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {
		for (Pair<AnalysisReadWriteHFInfo, AnalysisReadWriteHFInfo> p : overWriteCandidates) {
			if (CFGUtils.equalsOrContains(node, p.getLeft().getCall())) {
				if (computeGraph)
					tmpGraph = new ReadWriteGraph("OverWrite - Write1 location: " + p.getRight().getCall().getLocation()
							+ " - Write2 Location -" + p.getLeft().getCall().getLocation());
				if (interproceduralCheck(tool, graph, p.getLeft().getCall(), p.getRight().getCall(),
						new HashSet<CodeMember>(), new HashSet<CodeMember>())) {
					reconstructedGraphs.put(tmpGraph.getName(), tmpGraph);
					tool.warnOn(node, "Detected a possible over-write issue. Over-write location: "
							+ p.getRight().getCall().getLocation());
				}
			}

		}
	}

	private boolean interproceduralCheck(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement start, Statement end, Set<CodeMember> seenCallees, Set<CodeMember> seenCallers) {

		Statement startNode = CFGUtils.extractTargetNodeFromGraph(graph, start);
		startNode = startNode == null ? start : startNode;

		boolean isStartDeferred = startNode instanceof GoDefer;

		Statement endNode = CFGUtils.extractTargetNodeFromGraph(graph, end);
		if (endNode != null) {

			boolean isEndDeferred = endNode instanceof GoDefer;

			if (isMatching(graph, startNode, isStartDeferred, endNode, isEndDeferred)) {
				if (computeGraph) {
					ReadWriteNode node1 = new ReadWriteNode(tmpGraph, startNode);
					ReadWriteNode node2 = new ReadWriteNode(tmpGraph, endNode);

					tmpGraph.addNode(node1);
					tmpGraph.addNode(node2);

					tmpGraph.addEdge(isEndDeferred ? new DeferEdge(node1, node2) : new StandardEdge(node1, node2));
					destinationNode = node1;
				}
				return true;
			}

		}

		if (checkCallees(tool, graph, startNode, end, seenCallees, isStartDeferred)) {
			if (computeGraph) {
				ReadWriteNode node = new ReadWriteNode(tmpGraph, startNode);

				tmpGraph.addNode(node);

				tmpGraph.addEdge(isDeferredCallee ? new DeferEdge(node, destinationNode)
						: new StandardEdge(node, destinationNode));
				destinationNode = node;
			}
			return true;
		}

		if (checkCallers(tool, graph, end, seenCallees, seenCallers)) {
			if (computeGraph) {
				ReadWriteNode node = new ReadWriteNode(tmpGraph, startNode);

				tmpGraph.addNode(node);

				tmpGraph.addEdge(new CallerEdge(node, destinationNode));
				destinationNode = node;
			}
			return true;
		}

		return false;
	}

	private boolean isMatching(CFG graph, Statement startNode, boolean isStartDeferred, Statement endNode,
			boolean isEndDeferred) {
		/*
		 * #### LEGENDA r = read dr = defer read w = write df = defer write ###
		 * EXECUTION CASES r -> w | OK write after read w -> r | KO read after
		 * write dr -> dw | KO defer write after defer read (semantics of defer
		 * is LIFO (Last in First out)) dw -> dr | OK defer read after defer
		 * write (semantics of defer is LIFO (Last in First out)) dr -> w | KO
		 * write after defer read dw -> r | OK read after defer write w -> dr |
		 * KO defer read after write r -> dw | OK defer write after read ###
		 * IMPLEMENTED CHECKS w -> r | KO exist a path from w to r dr -> dw | KO
		 * exist a path from dr to dw dr -> w | KO exist a path from dr to w w
		 * -> dr | KO exist a path from w to dr ### PROGRAMMATICALLY
		 * !isStartDeferred -> !isEndDeferred | CFGUtils.existPath(graph,
		 * startNode, endNode, Search.BFS) isEndDeferred -> isStartDeferred |
		 * CFGUtils.existPath(graph, endNode, startNode, Search.BFS)
		 * isEndDeferred -> !isStartDeferred | CFGUtils.existPath(graph,
		 * endNode, startNode, Search.BFS) !isStartDeferred -> isEndDeferred |
		 * CFGUtils.existPath(graph, startNode, endNode, Search.BFS)
		 */

		if (!isStartDeferred && !isEndDeferred) {
			if (CFGUtils.existPath(graph, startNode, endNode, Search.BFS))
				return true;
		}

		if (isEndDeferred && isStartDeferred)
			if (CFGUtils.existPath(graph, endNode, startNode, Search.BFS))
				return true;

		if (!isStartDeferred && isEndDeferred)
			if (CFGUtils.existPath(graph, startNode, endNode, Search.BFS))
				return true;

		if (isEndDeferred && !isStartDeferred)
			if (CFGUtils.existPath(graph, endNode, startNode, Search.BFS))
				return true;

		return false;
	}

	private boolean checkCallees(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement start, Statement end, Set<CodeMember> seen, boolean isStartDeferred) {

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
											if (checkCalleesRecursive(tool, interCFG, e, end, seen)) {
												if (computeGraph) {
													ReadWriteNode node = new ReadWriteNode(tmpGraph, n);
													tmpGraph.addNode(node);
													tmpGraph.addEdge(new CalleeEdge(node, destinationNode));
													destinationNode = node;
													isDeferredCallee = n instanceof GoDefer;
												}

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
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement start, Statement end, Set<CodeMember> seen) {

		if (seen.contains(graph))
			return false;
		seen.add(graph);

		Statement endNode = CFGUtils.extractTargetNodeFromGraph(graph, end);

		if (endNode != null) {
			if (computeGraph) {
				ReadWriteNode node = new ReadWriteNode(tmpGraph, endNode);
				tmpGraph.addNode(node);
				destinationNode = node;
			}

			return true;
		}

		Collection<CodeMember> codemembers = getCalleesTransitively(tool, graph);
		for (CodeMember cm : codemembers) {
			if (cm instanceof VariableScopingCFG) {
				VariableScopingCFG interCFG = (VariableScopingCFG) cm;
				if (CFGUtils.extractTargetNodeFromGraph(graph, end) != null)
					for (Statement n : graph.getNodes()) {
						List<Call> calls = CFGUtils.extractCallsFromStatement(n);
						if (!calls.isEmpty()) {
							for (Call c : calls)
								if (c instanceof UnresolvedCall) {
									if (tool.getCallSites(cm).contains(c)) {
										for (Statement e : interCFG.getEntrypoints())
											if (checkCalleesRecursive(tool, interCFG, e, end, seen)) {
												if (computeGraph) {
													ReadWriteNode node = new ReadWriteNode(tmpGraph, e);
													tmpGraph.addNode(node);
													tmpGraph.addEdge(new CalleeEdge(node, destinationNode));
													destinationNode = node;
												}
												return true;
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
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement end, Set<CodeMember> seenCallees, Set<CodeMember> seenCallers) {

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
						if (interproceduralCheck(tool, callerCFG, sTarget, end, seenCallees, seenCallers)) {
							/*
							 * if(computeGraph){ ReadWriteNode node = new
							 * ReadWriteNode(tmpGraph, sTarget);
							 * tmpGraph.addNode(node); //tmpGraph.addEdge(new
							 * ReadWriteEdge(node, destinationNode,
							 * InfoEdgeType.CALLER)); destinationNode = node; }
							 */
							return true;
						}
				}
			}
		}
		return false;
	}

	public Collection<CodeMember> getCalleesTransitively(
			CheckToolWithAnalysisResults<
					SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CodeMember cm) {
		VisitOnceWorkingSet<CodeMember> ws = VisitOnceFIFOWorkingSet.mk();
		tool.getCallees(cm).stream().forEach(ws::push);
		while (!ws.isEmpty())
			tool.getCallees(ws.pop()).stream().forEach(ws::push);
		return ws.getSeen();
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
