package it.unive.golisa.cfg.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import it.unive.golisa.cfg.statement.assignment.GoMultiAssignment;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;

public class CFGUtils {

	public enum Search {
		BFS,
		DFS
	}
	
	public static boolean existPath(CFG graph, Statement source, Statement destination, Search search) {
			if(search.equals(Search.BFS))
				return searchBFS(graph, source, destination);
			else if(search.equals(Search.DFS))
				return searchDFS(graph, source, destination);
			else
				throw new IllegalArgumentException("The following search algorithm \"" + search + "\" is not supported");
	}
	
	public static boolean searchBFS(CFG graph, Statement source, Statement destination) {

		if(containsNode(graph, source) && containsNode(graph, destination)) {
			Set<Statement> seen = new HashSet<>();
	
			LinkedList<Statement> workingList = new LinkedList<Statement>();
			Statement start = extractTargetNodeFromGraph(graph, source);
			if(start != null) {
				workingList.add(start);
	
				while(!workingList.isEmpty()) {
					Statement node = workingList.remove();
					if(!seen.contains(node)) {
						seen.add(node);
						
						if(equalsOrContains(node, destination))
							return true;
	
						Collection<Edge> edges = graph.getOutgoingEdges(node);
						edges.forEach(e -> workingList.add(e.getDestination()));
					}
				}
			}
		}
		
		return false;
	}
	
	public static Statement extractTargetNodeFromGraph(CFG graph, Statement node) {
		for(Statement n : graph.getNodes()) 
			if(equalsOrContains(n,node))
				return n;
		return null;
	}
	
	public static List<Call> extractCallsFromStatement(Statement n) {
		Set<Statement> seen = new HashSet<>();
		List<Call> res = new ArrayList<Call>();
		extractCallsFromStatementRecursive(n, res, seen);
		return res;
	}
	
	private static void extractCallsFromStatementRecursive(Statement n, List<Call> list, Set<Statement> seen) {
		if(seen.contains(n))
			return;
		seen.add(n);
		
		if(n instanceof Call) {
			Call c = (Call) n;
			for(Expression subExp : c.getSubExpressions()) {
				extractCallsFromStatementRecursive(subExp, list, seen);
			}
			list.add((Call) n);
		} else if(n instanceof NaryExpression) {
			NaryExpression nExpr = (NaryExpression) n;
			for(Expression subExp : nExpr.getSubExpressions()) {
				extractCallsFromStatementRecursive(subExp, list, seen);
			}
		} else if(n instanceof GoMultiAssignment ) {
			GoMultiAssignment multiAssign = (GoMultiAssignment) n;
			extractCallsFromStatementRecursive(multiAssign.getExpressionToAssign(),list, seen);
		}
	}

	public static boolean equalsOrContains(Statement n1, Statement n2) {
		Set<Statement> seen = new HashSet<>();
		return equalsOrContainsRecursive(n1, n2, seen);
	}

	private static boolean equalsOrContainsRecursive(Statement n1, Statement n2, Set<Statement> seen) {
		if(seen.contains(n1))
			return false;
		seen.add(n1);
		
		if(n1.equals(n2)) {
			return true;
		} else if(n1 instanceof NaryExpression) {
			NaryExpression nExpr = (NaryExpression) n1;
			for(Expression subExp : nExpr.getSubExpressions()) {
				if(equalsOrContainsRecursive(subExp, n2, seen))
					return true;
			}
		} else if(n1 instanceof GoMultiAssignment ) {
			GoMultiAssignment multiAssign = (GoMultiAssignment) n1;
			if(equalsOrContainsRecursive(multiAssign.getExpressionToAssign(),n2, seen))
				return true;
		}
	
		return false;
	}

	private static boolean containsNode(CFG graph, Statement node) {

		for(Statement n : graph.getNodes()) 
			if(equalsOrContains(n,node))
				return true;
		
		return false;
	}

	public static boolean searchDFS(CFG graph, Statement source, Statement destination) {

		if(containsNode(graph, source) && containsNode(graph, destination)) {
			Set<Statement> seen = new HashSet<>();
			recursiveDFS(graph, extractTargetNodeFromGraph(graph, source), destination, seen);
		}
		
		return false;
	}
	
	public static boolean containsAllNodes(CFG graph, Statement ...nodes ) {
		boolean [] res = new boolean[nodes.length]; 
		for(Statement cfgNode : graph.getNodes())
			for(int i= 0; i < nodes.length;i++)
				if(!res[i] && equalsOrContains(cfgNode, nodes[i]))
					res[i] = true;
		for(boolean found : res)
			if(!found)
				return false;
		return true;
	}
	
	private static boolean recursiveDFS(CFG graph, Statement source, Statement destination, Set<Statement> seen) {
		if(!seen.contains(source)) {
			seen.add(source);
			
			if(equalsOrContains(source, destination))
				return true;

			Collection<Edge> edges = graph.getOutgoingEdges(source);
			Iterator<Edge> iter = edges.iterator();
			while(iter.hasNext()) {
				Edge e = iter.next();
				if(recursiveDFS(graph, e.getDestination(), destination, seen))
					return true;
			}
		}
		return false;
	}
	
	public static boolean anyMatchInCFGNodes(CFG cfg, Function<Statement, Boolean> condition) {
		return cfg.getNodes().stream().anyMatch(n ->matchNodeOrSubExpressions(n, condition));
	}
	
	public static boolean allMatchInCFGNodes(CFG cfg, Function<Statement, Boolean> condition) {
		return cfg.getNodes().stream().allMatch(n ->matchNodeOrSubExpressions(n, condition));
	}
	
	private static boolean matchNodeOrSubExpressions(Statement st, Function<Statement, Boolean> condition) {
		Set<Statement> seen = new HashSet<>();
		return matchNodeOrSubExpressionsRecursive(st, condition, seen);
	}

	private static boolean matchNodeOrSubExpressionsRecursive(Statement st, Function<Statement, Boolean> condition, Set<Statement> seen) {
		if(seen.contains(st))
			return false;
		seen.add(st);
		
		if(condition.apply(st).booleanValue()) {
			return true;
		} else if(st instanceof NaryExpression) {
			NaryExpression nExpr = (NaryExpression) st;
			for(Expression subExp : nExpr.getSubExpressions()) {
				if(matchNodeOrSubExpressions(subExp, condition))
					return true;
			}
		} else if(st instanceof GoMultiAssignment ) {
			GoMultiAssignment multiAssign = (GoMultiAssignment) st;
			if(matchNodeOrSubExpressions(multiAssign.getExpressionToAssign(), condition))
				return true;
		}
	
		return false;
	}
	
}
