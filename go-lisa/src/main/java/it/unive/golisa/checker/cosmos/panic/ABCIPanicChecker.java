package it.unive.golisa.checker.cosmos.panic;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
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
import it.unive.golisa.golang.api.signature.FuncGoLangApiSignature;
import it.unive.golisa.golang.api.signature.GoLangApiSignature;
import it.unive.golisa.golang.api.signature.MethodGoLangApiSignature;
import it.unive.golisa.golang.util.GoLangUtils;
import it.unive.lisa.analysis.SimpleAbstractState;
import it.unive.lisa.analysis.heap.pointbased.PointBasedHeap;
import it.unive.lisa.analysis.nonrelational.value.TypeEnvironment;
import it.unive.lisa.analysis.nonrelational.value.ValueEnvironment;
import it.unive.lisa.analysis.types.InferredTypes;
import it.unive.lisa.checks.semantic.CheckToolWithAnalysisResults;
import it.unive.lisa.checks.semantic.SemanticCheck;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.program.cfg.statement.call.CFGCall;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;

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
					if (!(existPossibleRecoveryDefer(panicGraphWithRecoveries) ||  existRecoveryDefer(panicGraphWithRecoveries))) {
						tool.warnOn(node, "Detected unhandled panic within a critical execution "
								+ printComponents + ". There are no execution paths with recovery function to handle the panic.");
					} else {
						if(atLeastOnePathWithoutRecovery(panicGraphWithRecoveries)) {
							tool.warnOn(node, "Detected panic within a critical execution " + printComponents
									+ ". There is at least an execution path without a recovery function.");
						} else {
							if(existPossibleRecoveryDefer(panicGraphWithRecoveries))
								tool.warnOn(node, "Detected panic within a critical execution " + printComponents
										+ ". Ensure that all the possible recovery functions properly handle the panic exception.");
						}
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

	private boolean existRecoveryDefer(GraphForCheckers panicGraphWithRecoveries) {
		return panicGraphWithRecoveries.getNodes().stream().anyMatch(n -> n instanceof RecoveryNode);
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
				boolean isCandidate = st instanceof GoDefer ? CFGUtils.existPath(st.getCFG(), st, (GoDefer) n, Search.BFS) : CFGUtils.existPath(st.getCFG(), n, st, Search.BFS);

				if(isCandidate && maybeRecovery((GoDefer) n)) {
					StandardNode recovery = hasExplicitRecovery((GoDefer) n, tool) ? new RecoveryNode(graphWithRecovery, (GoDefer) n) : new PossileRecoveryNode(graphWithRecovery, (GoDefer) n);
					graphWithRecovery.addNode(recovery);
					Collection<LabeledEdge> edgesToRemove = graphWithRecovery.getIngoingEdges(stNode);
					for( LabeledEdge e : edgesToRemove) {
						graphWithRecovery.addEdge(e.newInstance(e.getSource(), recovery));
						graphWithRecovery.getNodeList().removeEdge(e);
					}
					graphWithRecovery.addEdge(new StandardEdge(recovery, stNode));
				}
			}
				
		}
		
		Collection<LabeledEdge> ingoingEdges = graphWithRecovery.getIngoingEdges(stNode);
		if(!ingoingEdges.isEmpty())
			for(LabeledEdge e : ingoingEdges) {
				
				computePossibleRecoveryDefersRecursive(graphWithRecovery, e.getSource().getStatement(), tool, new HashSet<>(seen));

			}
	}

	private boolean maybeRecovery(GoDefer defer) {
		Expression expr = defer.getSubExpression();
		if(expr instanceof CFGCall) {
			CFGCall call = (CFGCall) expr;
			return call.getTargetedCFGs().stream().anyMatch(cfg -> cfg.getNodes().stream().anyMatch(n -> CFGUtils.matchNodeOrSubExpressions(n, st -> st instanceof GoRecover)));
		} else if(expr instanceof UnresolvedCall) {
			//TODO:add possible saniteizer list
			
			if(!matchAnyGoAPIMethodOrFunctionSignatures((UnresolvedCall) expr))
				return true;
		}
		return false;
	}


	private boolean matchAnyGoAPIMethodOrFunctionSignatures(UnresolvedCall call) {
		Map<String, Set<FuncGoLangApiSignature>> mapf = GoLangUtils.getGoLangApiFunctionSignatures();
		Map<String, Set<MethodGoLangApiSignature>> mapm = GoLangUtils.getGoLangApiMethodSignatures();

		for(String pckg : mapf.keySet())
			for(FuncGoLangApiSignature f : mapf.get(pckg)) 
				if(matchSignature(f, call))
						return true;

		for(String pckg : mapm.keySet())
			for(MethodGoLangApiSignature m : mapm.get(pckg)) 
				if(matchSignature(m, call))
					return true;
		
		return false;
	}

	private boolean matchSignature(GoLangApiSignature goLangApiSignature, UnresolvedCall call) {

		String signatureName = null;
		if (goLangApiSignature instanceof FuncGoLangApiSignature)
			signatureName = ((FuncGoLangApiSignature) goLangApiSignature).getName();
		else if (goLangApiSignature instanceof MethodGoLangApiSignature)
			signatureName = ((MethodGoLangApiSignature) goLangApiSignature).getName();

		if (signatureName != null && signatureName.equals(call.getTargetName())
				&& call.getParameters().length > 0
				&& call.getParameters()[0] instanceof VariableRef) {

			VariableRef var = (VariableRef) call.getParameters()[0];
			if (goLangApiSignature instanceof FuncGoLangApiSignature)
				if(goLangApiSignature.getPackage().contains(var.getName()))
					return true;
			else 
				return true;
		}

		return false;
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
									 GraphForCheckers tmp = extractPathCriticalComponentsInvolvedInPanicRecursive(sTarget, node, graph, tool, new HashSet<>(seen));
									 if(tmp != null) {
											graph.merge(tmp);
									 }
								}
							}
						}
					}
				}
			}
		} else {
			throw new UnsupportedOperationException("The analysis on cyclic graphs are currently not supported");
		}
		return graph;
	}

	private boolean isCriticalComponent(CodeMemberDescriptor descriptor) {
		return descriptor.getSignature().contains("BeginBlocker") 
		|| descriptor.getSignature().contains("EndBlocker"); 
	}
	

	private boolean atLeastOnePathWithoutRecovery(GraphForCheckers panicGraphWithRecoveries) {
		Set<Statement> seen = new HashSet<>();
		PanicNode panicNode = null;
		for(StandardNode n : panicGraphWithRecoveries.getNodes())
			if(n instanceof PanicNode) {
				panicNode= (PanicNode) n;
				break;
			}
			
		return atLeastOnePathWithoutRecoveryRecursive(panicGraphWithRecoveries, panicNode.getStatement(), seen);
	}
	
	private boolean atLeastOnePathWithoutRecoveryRecursive(GraphForCheckers panicGraphWithRecoveries, Statement st, Set<Statement> seen) {

		if(seen.contains(st))
			return false;
		seen.add(st);
		
		StandardNode stNode = panicGraphWithRecoveries.getNodeFromStatement(st);
		
		if(stNode instanceof PossileRecoveryNode || stNode instanceof RecoveryNode)
			return false;
		

		
		Collection<LabeledEdge> ingoingEdges = panicGraphWithRecoveries.getIngoingEdges(stNode);
		if(!ingoingEdges.isEmpty())
			for(LabeledEdge e : ingoingEdges) {
				
				if(atLeastOnePathWithoutRecoveryRecursive(panicGraphWithRecoveries, e.getSource().getStatement(), new HashSet<>(seen)))
					return true;
			}
		else 
			if(isCriticalComponent(st.getCFG().getDescriptor()))
				return true;
		return false;
	}

}
