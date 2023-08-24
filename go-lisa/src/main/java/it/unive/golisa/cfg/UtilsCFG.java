package it.unive.golisa.cfg;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.util.collections.workset.VisitOnceFIFOWorkingSet;
import it.unive.lisa.util.collections.workset.VisitOnceWorkingSet;

public class UtilsCFG {

	enum Search {
		BFS,
		DFS
	}
	
	public static boolean existPath(CFG graph, Statement source, Statement destination, Search search) throws IllegalAccessException {
			if(search.equals(Search.BFS))
				searchBFS(graph, source, destination);
			else if(search.equals(Search.DFS))
				searchDFS(graph, source, destination);
			else
				throw new IllegalAccessException("The following search algorithm \"" + search + "\" is not supported");
			
		return false;
	}
	
	public static boolean searchBFS(CFG graph, Statement source, Statement destination) {

		if(graph.containsNode(source) && graph.containsNode(destination)) {
			Set<Statement> seen = new HashSet<>();
	
			LinkedList<Statement> workingList = new LinkedList<Statement>();
			workingList.add(source);
			
			while(!workingList.isEmpty()) {
				Statement node = workingList.remove();
				if(!seen.contains(node)) {
					seen.add(node);
					
					if(node.equals(destination))
						return true;

					Collection<Edge> edges = graph.getOutgoingEdges(node);
					edges.forEach(e -> workingList.add(e.getDestination()));
				}
			}
		}
		
		return false;
	}
	
	public static boolean searchDFS(CFG graph, Statement source, Statement destination) {

		if(graph.containsNode(source) && graph.containsNode(destination)) {
			Set<Statement> seen = new HashSet<>();
			recursiveDFS(graph, source, destination, seen);
		}
		
		return false;
	}
	
	private static boolean recursiveDFS(CFG graph, Statement source, Statement destination, Set<Statement> seen) {
		if(!seen.contains(source)) {
			seen.add(source);
			
			if(source.equals(destination))
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
