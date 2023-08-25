package it.unive.golisa.cfg;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import it.unive.golisa.cfg.statement.assignment.GoMultiAssignment;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.Statement;

public class UtilsCFG {

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
			Statement start = extractSourceFromGraph(graph, source);
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
	
	private static Statement extractSourceFromGraph(CFG graph, Statement source) {
		for(Statement n : graph.getNodes()) 
			if(equalsOrContains(n,source))
				return n;
		return null;
	}

	private static boolean equalsOrContains(Statement n1, Statement n2) {
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
				if(equalsOrContains(subExp, n2))
					return true;
			}
		} else if(n1 instanceof GoMultiAssignment ) {
			GoMultiAssignment multiAssign = (GoMultiAssignment) n1;
			if(equalsOrContains(multiAssign.getExpressionToAssign(),n2))
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
			recursiveDFS(graph, extractSourceFromGraph(graph, source), destination, seen);
		}
		
		return false;
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
	
}
