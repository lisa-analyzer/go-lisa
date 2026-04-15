package it.unive.golisa.checker.hf.events;

import it.unive.golisa.cfg.VariableScopingCFG;
import it.unive.golisa.cfg.statement.GoDefer;
import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.golisa.cfg.utils.CFGUtils.Search;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.string.tarsis.Tarsis;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A Go Checker for detect multiple event emits in Hyperledger Fabric.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class EventEmitWithNoSuccessChecker implements
		SemanticCheck<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> {

	private static final Logger LOG = LogManager.getLogger(EventEmitWithNoSuccessChecker.class);


	@Override
	public void beforeExecution(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool) {
	}

	@Override
	public void afterExecution(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool) {

	}

	@Override
	public void visitGlobal(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			Unit unit, Global global, boolean instance) {
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph) {
		return true;
	}

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {

		List<Call> calls = CFGUtils.extractCallsFromStatement(node);
		if (calls.isEmpty())
			return true;

		checkOverWriteIssue(tool, graph, node);

		return true;
	}

	private void checkOverWriteIssue(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {

		List<Call> calls = CFGUtils.extractCallsFromStatement(node);

		boolean found = false;
		for (Call c : calls)
			if (c.getTargetName().equals("SetEvent") && c.getParameters().length == 2)
				found = true;

		if (!found)
			return;


		if (!interproceduralCheck(tool, graph, node, node, new HashSet<CodeMember>(), new HashSet<CodeMember>())) {
			tool.warnOn(node, "Detected event emittion without a success response.");
		}

	}

	private boolean interproceduralCheck(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement root, Statement start, Set<CodeMember> seenCallees, Set<CodeMember> seenCallers) {

		Statement startNode = CFGUtils.extractTargetNodeFromGraph(graph, start);
		startNode = startNode == null ? start : startNode;

		boolean isStartDeferred = startNode instanceof GoDefer;

		Set<Statement> successResponseNodes = new HashSet<>();
		for (Statement node : graph.getNodeList()) {
			List<Call> calls = CFGUtils.extractCallsFromStatement(node);
			for (Call c : calls)
				if (c.getTargetName().equals("Success"))
					successResponseNodes.add(node);
		}

		for (Statement endNode : successResponseNodes) {
			boolean isEndDeferred = endNode instanceof GoDefer;

			if (isMatching(graph, startNode, isStartDeferred, endNode, isEndDeferred))
				return true;
		}

		if (checkCallees(tool, graph, root, startNode, seenCallees, isStartDeferred))
			return true;

		if (checkCallers(tool, graph, root, seenCallees, seenCallers))
			return true;

		return false;
	}

	private boolean checkCallees(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
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
						if (isMatching(graph, start, isStartDeferred, n, isEndDeferred)) {
							for (Call c : calls)
								if (c instanceof UnresolvedCall) {
									if (tool.getCallSites(cm).contains(c)) {
										for (Statement e : interCFG.getEntrypoints())
											if (checkCalleesRecursive(tool, interCFG, e, seen))
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

	private boolean checkCalleesRecursive(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement start, Set<CodeMember> seen) {

		if (seen.contains(graph))
			return false;
		seen.add(graph);

		Set<Statement> successResponseNodes = new HashSet<>();
		for (Statement node : graph.getNodeList()) {
			List<Call> calls = CFGUtils.extractCallsFromStatement(node);
			for (Call c : calls)
				if (c.getTargetName().equals("Success"))
					successResponseNodes.add(node);
		}

		if(successResponseNodes.size() > 0)
			return true;

		Collection<CodeMember> codemembers = getCalleesTransitively(tool, graph);
		for (CodeMember cm : codemembers) {
			if (cm instanceof VariableScopingCFG) {
				VariableScopingCFG interCFG = (VariableScopingCFG) cm;
				for (Statement endNode : successResponseNodes) {
					if (CFGUtils.extractTargetNodeFromGraph(graph, endNode) != null)
						for (Statement n : graph.getNodes()) {
							List<Call> calls = CFGUtils.extractCallsFromStatement(n);
							if (!calls.isEmpty()) {
								for (Call c : calls)
									if (c instanceof UnresolvedCall) {
										if (tool.getCallSites(cm).contains(c)) {
											for (Statement e : interCFG.getEntrypoints())
												if (checkCalleesRecursive(tool, interCFG, e, seen))
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
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement root, Set<CodeMember> seenCallees, Set<CodeMember> seenCallers) {

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
		return false;
	}

	private boolean isMatching(CFG graph, Statement startNode, boolean isStartDeferred, Statement endNode,
			boolean isEndDeferred) {

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

	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Edge edge) {
		return true;
	}

	@Override
	public boolean visitUnit(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			Unit unit) {
		return true;
	}

	public Collection<CodeMember> getCalleesTransitively(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<Tarsis>, TypeEnvironment<InferredTypes>>> tool,
			CodeMember cm) {
		VisitOnceWorkingSet<CodeMember> ws = VisitOnceFIFOWorkingSet.mk();
		tool.getCallees(cm).stream().forEach(ws::push);
		while (!ws.isEmpty())
			tool.getCallees(ws.pop()).stream().forEach(ws::push);
		return ws.getSeen();
	}

}