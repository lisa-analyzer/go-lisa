package it.unive.golisa.checker.cosmos.panic;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unive.golisa.analysis.DummyDomain;
import it.unive.golisa.cfg.VariableScopingCFG;
import it.unive.golisa.cfg.expression.GoPanic;
import it.unive.golisa.cfg.expression.GoRecover;
import it.unive.golisa.cfg.statement.GoDefer;
import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.golisa.cfg.utils.CFGUtils.Search;
import it.unive.golisa.checker.cosmos.panic.graph.PanicNode;
import it.unive.golisa.checker.cosmos.panic.graph.PossileRecoveryNode;
import it.unive.golisa.checker.cosmos.panic.graph.RecoveryNode;
import it.unive.golisa.checker.utils.graph.GraphForCheckers;
import it.unive.golisa.checker.utils.graph.edges.CallerEdge;
import it.unive.golisa.checker.utils.graph.edges.LabeledEdge;
import it.unive.golisa.checker.utils.graph.edges.StandardEdge;
import it.unive.golisa.checker.utils.graph.nodes.StandardNode;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.checks.syntactic.CheckTool;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.program.cfg.statement.call.Call;
import javassist.expr.NewArray;


/**
 * Unhandled errors Checker in Hyperledger Fabric.
 *
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class ABCIPanicChecker implements SemanticCheck<
SimpleAbstractState<PointBasedHeap, ValueEnvironment<DummyDomain>, TypeEnvironment<InferredTypes>>>  {

	
	
	@Override
	public boolean visit(
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<DummyDomain>, TypeEnvironment<InferredTypes>>> tool,
			CFG graph, Statement node) {

		if (node instanceof GoPanic) {

			try {
				GraphForCheckers panicGraph = extractPathCriticalComponentsInvolvedInPanic((GoPanic) node, tool);
				if (panicGraph != null && !panicGraph.getNodes().isEmpty()) {

					GraphForCheckers panicGraphWithRecoveries = computePossibleRecoveryDefers(panicGraph,
							(GoPanic) node, tool);

					String printComponents = criticalComponentsToString(panicGraphWithRecoveries);
					if (!existPossibleRecoveryDefer(panicGraphWithRecoveries)) {
						tool.warnOn(node, "Detected unhandled panic within the following ABCI methods: "
								+ printComponents + ". Possible recovery functions not found.");
					} else {
						// TODO: handle the case where at least one path is unhandled and the another is
						// handled
						tool.warnOn(node, "Detected panic within the following ABCI methods: " + printComponents
								+ ". Ensure that recovery functions properly handle the panic exception.");
					}

				}
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}

		}

		return true;
	}


	private String criticalComponentsToString(GraphForCheckers panicGraphWithRecoveries) {

		Set<String> signatures = new HashSet<>();
		for(StandardNode n : panicGraphWithRecoveries.getNodes()) {
			if(panicGraphWithRecoveries.getIngoingEdges(n) == null)
				signatures.add(n.getStatement().getCFG().getDescriptor().getFullSignature());
		}
		return String.join(",", signatures);
	}



	private boolean existPossibleRecoveryDefer(GraphForCheckers panicGraphWithRecoveries) {
		
		return panicGraphWithRecoveries.getNodes().stream().anyMatch(n -> n instanceof PossileRecoveryNode);
	}



	private GraphForCheckers computePossibleRecoveryDefers(GraphForCheckers panicGraph, GoPanic panic, CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<DummyDomain>, TypeEnvironment<InferredTypes>>> tool) throws CloneNotSupportedException {
		GraphForCheckers graphWithRecovery = panicGraph.clone();
		
		computePossibleRecoveryDefersRecursive(graphWithRecovery, panic, tool, new HashSet<Statement>());
		
		return graphWithRecovery;
	}
	
	private void computePossibleRecoveryDefersRecursive(GraphForCheckers graphWithRecovery, Statement st, CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<DummyDomain>, TypeEnvironment<InferredTypes>>> tool, Set<Statement> seen) throws CloneNotSupportedException {

		if(seen.contains(st))
			return;
		seen.add(st);
		
		StandardNode stNode = graphWithRecovery.getNodeFromStatement(st);
		for(Statement n : st.getCFG().getNodes()) {
			if(!n.equals(st) && n instanceof GoDefer) {
				boolean add = st instanceof GoDefer ? CFGUtils.existPath(st.getCFG(), st, (GoDefer) n, Search.BFS) : CFGUtils.existPath(st.getCFG(), n, st, Search.BFS);

				if(add) {
					StandardNode recovery = hasExplicitRecovery((GoDefer) n, tool) ? new RecoveryNode(graphWithRecovery, (GoDefer) n) : new PossileRecoveryNode(graphWithRecovery, (GoDefer) n);
					graphWithRecovery.addNode(recovery);
					graphWithRecovery.addEdge(new StandardEdge(recovery, stNode));
					Collection<LabeledEdge> edgesToRemove = graphWithRecovery.getIngoingEdges(stNode);
					for( LabeledEdge e : edgesToRemove) {
						graphWithRecovery.addEdge(e.newInstance(e.getSource(), recovery));
						graphWithRecovery.getEdges().remove(e);
					}
				}
			}
				
		}
		
		Collection<LabeledEdge> ingoingEdges = graphWithRecovery.getIngoingEdges(stNode);
		if(!ingoingEdges.isEmpty())
			for(LabeledEdge e : ingoingEdges)
				computePossibleRecoveryDefersRecursive(graphWithRecovery, e.getSource().getStatement(), tool, seen);
	}

	private boolean hasExplicitRecovery(GoDefer defer, CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<DummyDomain>, TypeEnvironment<InferredTypes>>> tool) {
		Expression expr = defer.getSubExpression();
		if(expr instanceof CFGCall) {
			CFGCall call = (CFGCall) expr;
			return call.getTargetedCFGs().stream().anyMatch(cfg -> cfg.getNodes().stream().anyMatch(n -> CFGUtils.matchNodeOrSubExpressions(n, st -> st instanceof GoRecover)));
		}
		return false;
	}

	private GraphForCheckers extractPathCriticalComponentsInvolvedInPanic(GoPanic node,
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<DummyDomain>, TypeEnvironment<InferredTypes>>> tool) throws CloneNotSupportedException {
		GraphForCheckers graph = new GraphForCheckers(node.getLocation().toString());
		Set<CodeMember> seen = new HashSet<>();
		return extractPathCriticalComponentsInvolvedInPanicRecursive(node, null, graph, tool,seen);
	}


	private GraphForCheckers extractPathCriticalComponentsInvolvedInPanicRecursive(Statement node, Statement previous, GraphForCheckers graph,
			CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<DummyDomain>, TypeEnvironment<InferredTypes>>> tool,
			Set<CodeMember> seen) throws CloneNotSupportedException {
		
		if(!seen.contains(node.getCFG())) {
			seen.add(node.getCFG());	
			StandardNode pNode = node instanceof GoPanic ? new PanicNode(graph, (GoPanic) node) : new StandardNode(graph, node);
			graph.addNode(pNode);
			if(previous != null) {
				StandardNode prev = graph.getNodeFromStatement(previous);
				graph.addEdge(new CallerEdge(pNode, prev));
			}
			
			if(isCriticalComponent(node.getCFG().getDescriptor())) {
				return graph;
			} else {
				// check callers of CFG recursively
				Collection<CodeMember> callers = tool.getCallers(node.getCFG());

				for (CodeMember cm : callers) {
					if (!seen.contains(cm)) {
						for (Call c : tool.getCallSites(node.getCFG())) {
							if (cm instanceof VariableScopingCFG) {
								VariableScopingCFG callerCFG = (VariableScopingCFG) cm;
								Statement sTarget = CFGUtils.extractTargetNodeFromGraph(callerCFG, c);
								if (sTarget != null) {
									 GraphForCheckers tmp = extractPathCriticalComponentsInvolvedInPanicRecursive(sTarget, sTarget, graph, tool, seen);
									 if(tmp != null) {
											graph.merge(tmp);
									 }
								}
							}
						}
					}
				}
			}
		}
		return graph;
	}



	private Set<CodeMemberDescriptor> extractCriticalComponentsInvolvedInPanic(GoPanic panic, CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<DummyDomain>, TypeEnvironment<InferredTypes>>> tool) {
		Set<CodeMember> seen = new HashSet<>();
		return extractCriticalComponentsRecursive(panic, tool, seen);
	}
	
	private Set<CodeMemberDescriptor> extractCriticalComponentsRecursive(Statement node, CheckToolWithAnalysisResults<SimpleAbstractState<PointBasedHeap, ValueEnvironment<DummyDomain>, TypeEnvironment<InferredTypes>>> tool, Set<CodeMember>  seen) {
		Set<CodeMemberDescriptor> result = new HashSet<>();
		if(!seen.contains(node.getCFG())) {
			seen.add(node.getCFG());	
			if(isCriticalComponent(node.getCFG().getDescriptor())) {
				result.add(node.getCFG().getDescriptor());
			} else {
				// check callers of CFG recursively
				Collection<CodeMember> callers = tool.getCallers(node.getCFG());

				for (CodeMember cm : callers) {
					if (!seen.contains(cm)) {
						seen.add(cm);

						for (Call c : tool.getCallSites(node.getCFG())) {
							if (cm instanceof VariableScopingCFG) {
								VariableScopingCFG callerCFG = (VariableScopingCFG) cm;
								Statement sTarget = CFGUtils.extractTargetNodeFromGraph(callerCFG, c);
								if (sTarget != null) {
									result.addAll(extractCriticalComponentsRecursive(sTarget, tool, seen));
								}
							}
						}
					}
				}
			}
		}
		return result;
	}

	private boolean isCriticalComponent(CodeMemberDescriptor descriptor) {
		return descriptor.getSignature().contains("BeginBlocker") 
		|| descriptor.getSignature().contains("EndBlocker"); 
	}

	private Set<Statement> extractPossibleRecoveryDefer(Statement node) {
		// TODO Auto-generated method stub
		return null;
	}

}
