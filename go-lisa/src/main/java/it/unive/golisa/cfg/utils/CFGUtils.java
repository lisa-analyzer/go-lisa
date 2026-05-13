package it.unive.golisa.cfg.utils;

import it.unive.golisa.cfg.statement.assignment.GoMultiAssignment;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.edge.SequentialEdge;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.util.datastructures.graph.code.CodeGraph;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

/**
 * Class containing some utils methods to deal with CFGs.
 * 
 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
 */
public class CFGUtils {

	/**
	 * Type of search in a graph.
	 * 
	 * @author <a href="mailto:luca.olivieri@unive.it">Luca Olivieri</a>
	 */
	public enum Search {
		/**
		 * BFS.
		 */
		BFS,

		/**
		 * DFS.
		 */
		DFS
	}

	/**
	 * Checks whether there exists a path in {@code cfg} between {@code source}
	 * and {@code destination}.
	 * 
	 * @param cfg         the cfg
	 * @param source      the source statement
	 * @param destination the destination statement
	 * @param search      the type of search
	 * 
	 * @return whether there exists a path in {@code cfg} between {@code source}
	 *             and {@code destination}
	 * 
	 * @throws IllegalArgumentException if the search algorithm is not supported
	 */
	public static boolean existPath(CFG cfg, Statement source, Statement destination, Search search) {
		if (source.equals(destination))
			return true;
		if (search.equals(Search.BFS))
			return searchBFS(cfg, source, destination);
		else if (search.equals(Search.DFS))
			return searchDFS(cfg, source, destination);
		else
			throw new IllegalArgumentException("The following search algorithm \"" + search + "\" is not supported");
	}

	private static boolean searchBFS(CFG graph, Statement source, Statement destination) {

		if (containsNode(graph, source) && containsNode(graph, destination)) {
			Set<Statement> seen = new HashSet<>();

			LinkedList<Statement> workingList = new LinkedList<Statement>();
			Statement start = extractTargetNodeFromGraph(graph, source);
			if (start != null) {
				workingList.add(start);

				while (!workingList.isEmpty()) {
					Statement node = workingList.remove();
					if (!seen.contains(node)) {
						seen.add(node);

						if (equalsOrContains(node, destination))
							return true;

						Collection<Edge> edges = graph.getOutgoingEdges(node);
						edges.forEach(e -> workingList.add(e.getDestination()));
					}
				}
			}
		}

		return false;
	}

	/**
	 * Extracts a target statement from cfg.
	 * 
	 * @param cfg  the cfg
	 * @param stmt the statement
	 * 
	 * @return the target statement from cfg
	 */
	public static Statement extractTargetNodeFromGraph(CodeGraph<CFG, Statement, Edge> cfg, Statement stmt) {
		for (Statement n : cfg.getNodes())
			if (equalsOrContains(n, stmt))
				return n;
		return null;
	}

	/**
	 * Extracts call nodes from a statement.
	 * 
	 * @param stmt the statement
	 * 
	 * @return yields the call nodes from a statement
	 */
	public static List<Call> extractCallsFromStatement(Statement stmt) {
		Set<Statement> seen = new HashSet<>();
		List<Call> res = new ArrayList<Call>();
		extractCallsFromStatementRecursive(stmt, res, seen);
		return res;
	}

	private static void extractCallsFromStatementRecursive(Statement n, List<Call> list, Set<Statement> seen) {
		if (seen.contains(n))
			return;
		seen.add(n);

		if (n instanceof Call) {
			Call c = (Call) n;
			for (Expression subExp : c.getSubExpressions()) {
				extractCallsFromStatementRecursive(subExp, list, seen);
			}
			list.add((Call) n);
		} else if (n instanceof NaryExpression) {
			NaryExpression nExpr = (NaryExpression) n;
			for (Expression subExp : nExpr.getSubExpressions()) {
				extractCallsFromStatementRecursive(subExp, list, seen);
			}
		} else if (n instanceof GoMultiAssignment) {
			GoMultiAssignment multiAssign = (GoMultiAssignment) n;
			extractCallsFromStatementRecursive(multiAssign.getExpressionToAssign(), list, seen);
		}
	}

	/**
	 * Checks if the two statements are equals or {code n1} contains {@code n2}.
	 * 
	 * @param n1 the first statement
	 * @param n2 the second statement
	 * 
	 * @return {@code true} if the two statements are equals or {code n1}
	 *             contains {@code n2}, {@code false} otherwise.
	 */
	public static boolean equalsOrContains(Statement n1, Statement n2) {
		Set<Statement> seen = new HashSet<>();
		return equalsOrContainsRecursive(n1, n2, seen);
	}

	private static boolean equalsOrContainsRecursive(Statement n1, Statement n2, Set<Statement> seen) {
		if (seen.contains(n1))
			return false;
		seen.add(n1);

		if (n1.equals(n2)) {
			return true;
		} else if (n1 instanceof NaryExpression) {
			NaryExpression nExpr = (NaryExpression) n1;
			for (Expression subExp : nExpr.getSubExpressions()) {
				if (equalsOrContainsRecursive(subExp, n2, seen))
					return true;
			}
		} else if (n1 instanceof GoMultiAssignment) {
			GoMultiAssignment multiAssign = (GoMultiAssignment) n1;
			if (equalsOrContainsRecursive(multiAssign.getExpressionToAssign(), n2, seen))
				return true;
		}

		return false;
	}

	private static boolean containsNode(CFG graph, Statement node) {

		for (Statement n : graph.getNodes())
			if (equalsOrContains(n, node))
				return true;

		return false;
	}

	private static boolean searchDFS(CFG graph, Statement source, Statement destination) {

		if (containsNode(graph, source) && containsNode(graph, destination)) {
			Set<Statement> seen = new HashSet<>();
			recursiveDFS(graph, extractTargetNodeFromGraph(graph, source), destination, seen);
		}

		return false;
	}

	/**
	 * Yields {@code true} if the CFG contains all the statements.
	 * 
	 * @param graph the CFG to check
	 * @param nodes the statements to check
	 * 
	 * @return Yields {@code true} if the CFG contains all the statements
	 */
	private static boolean containsAllNodes(CFG graph, Statement... nodes) {
		boolean[] res = new boolean[nodes.length];
		for (Statement cfgNode : graph.getNodes())
			for (int i = 0; i < nodes.length; i++)
				if (!res[i] && equalsOrContains(cfgNode, nodes[i]))
					res[i] = true;
		for (boolean found : res)
			if (!found)
				return false;
		return true;
	}

	/**
	 * Recursive DFS search on a CFG to find if exist a path between two nodes.
	 * 
	 * @param graph       the CFG to check
	 * @param source      the source node
	 * @param destination the destination node
	 * @param seen        the set of nodes already seen
	 * 
	 * @return {@code true} if there is a path between the two nodes
	 */
	private static boolean recursiveDFS(CFG graph, Statement source, Statement destination, Set<Statement> seen) {
		if (!seen.contains(source)) {
			seen.add(source);

			if (equalsOrContains(source, destination))
				return true;

			Collection<Edge> edges = graph.getOutgoingEdges(source);
			Iterator<Edge> iter = edges.iterator();
			while (iter.hasNext()) {
				Edge e = iter.next();
				if (recursiveDFS(graph, e.getDestination(), destination, seen))
					return true;
			}
		}
		return false;
	}

	/**
	 * Given a condition, counts the number of matches in the CFG.
	 * 
	 * @param cfg       the CFG to check
	 * @param condition the condition
	 * 
	 * @return the number of matches
	 */
	public static int countMatchInCFGNodes(CFG cfg, Function<Statement, Boolean> condition) {
		int res = 0;
		for (Statement node : cfg.getNodes()) {
			if (matchNodeOrSubExpressions(node, condition))
				res++;
		}
		return res;
	}

	/**
	 * Given a condition, it checks if there is a match in the nodes of CFG.
	 * 
	 * @param cfg       the CFG to check
	 * @param condition the condition
	 * 
	 * @return {@code true} if there is a match
	 */
	public static boolean anyMatchInCFGNodes(CFG cfg, Function<Statement, Boolean> condition) {
		return cfg.getNodes().stream().anyMatch(n -> matchNodeOrSubExpressions(n, condition));
	}

	/**
	 * Given a condition, it checks if the condition match with all nodes of
	 * CFG.
	 * 
	 * @param cfg       the CFG to check
	 * @param condition the condition
	 * 
	 * @return {@code true} if all nodes match the condition
	 */
	public static boolean allMatchInCFGNodes(CFG cfg, Function<Statement, Boolean> condition) {
		return cfg.getNodes().stream().allMatch(n -> matchNodeOrSubExpressions(n, condition));
	}

	/**
	 * Checks whether the statement of one of its sub expression matches the
	 * condition.
	 * 
	 * @param st        the statement
	 * @param condition the condition to be matched
	 * 
	 * @return {@code true} if the statement of one of its sub expression
	 *             matches the condition, {@code false} otherwise.
	 */
	public static boolean matchNodeOrSubExpressions(Statement st, Function<Statement, Boolean> condition) {
		Set<Statement> seen = new HashSet<>();
		return matchNodeOrSubExpressionsRecursive(st, condition, seen);
	}

	private static boolean matchNodeOrSubExpressionsRecursive(Statement st, Function<Statement, Boolean> condition,
			Set<Statement> seen) {
		if (seen.contains(st))
			return false;
		seen.add(st);

		if (condition.apply(st).booleanValue()) {
			return true;
		} else if (st instanceof NaryExpression) {
			NaryExpression nExpr = (NaryExpression) st;
			for (Expression subExp : nExpr.getSubExpressions()) {
				if (matchNodeOrSubExpressions(subExp, condition))
					return true;
			}
		} else if (st instanceof GoMultiAssignment) {
			GoMultiAssignment multiAssign = (GoMultiAssignment) st;
			if (matchNodeOrSubExpressions(multiAssign.getExpressionToAssign(), condition))
				return true;
		}

		return false;
	}

	/**
	 * Yields the path in {@code cfg} between {@code source} and
	 * {@code destination}.
	 * 
	 * @param cfg         the cfg
	 * @param source      the source node
	 * @param destination the destination node
	 * 
	 * @return the path in {@code cfg} between {@code source} and
	 *             {@code destination}
	 */
	public static CodeGraph<CFG, Statement, Edge> getPath(CFG cfg, Statement source, Statement destination) {
		if (containsNode(cfg, source) && containsNode(cfg, destination))
			return getSearchGraphDFS(cfg, source, destination);

		return null;
	}

	/**
	 * Yields a code graph that contains a path between two nodes.
	 * 
	 * @param graph       the CFG to check
	 * @param source      the source node
	 * @param destination the destination node
	 * 
	 * @return the graph with the path
	 */
	private static CodeGraph<CFG, Statement, Edge> getSearchGraphDFS(CFG graph, Statement source,
			Statement destination) {

		if (containsNode(graph, source) && containsNode(graph, destination)) {
			Set<Statement> seen = new HashSet<>();
			CFG res = new CFG(graph.getDescriptor());
			getSearchGraphRecursiveDFS(graph, extractTargetNodeFromGraph(graph, source), destination, seen, res);
			return res;
		}
		return null;
	}

	/**
	 * Yields a code graph that contains a path between two nodes.
	 * 
	 * @param graph       the CFG to check
	 * @param source      the source node
	 * @param destination the destination statement
	 * @param seen        the set of nodes aready seen
	 * @param res         the collection of code graphs found
	 * 
	 * @return the destination statement or the node that contain it
	 */
	private static Statement getSearchGraphRecursiveDFS(CFG graph, Statement source, Statement destination,
			Set<Statement> seen, CodeGraph<CFG, Statement, Edge> res) {
		if (!seen.contains(source)) {
			seen.add(source);

			if (equalsOrContains(source, destination)) {
				res.addNode(source);
				return source;
			}

			Collection<Edge> edges = graph.getOutgoingEdges(source);
			Iterator<Edge> iter = edges.iterator();
			while (iter.hasNext()) {
				Edge e = iter.next();
				Statement st = getSearchGraphRecursiveDFS(graph, e.getDestination(), destination, seen, res);
				if (st != null) {
					res.addNode(e.getDestination());
					res.addEdge(new SequentialEdge(e.getDestination(), st));
					return e.getDestination();
				}
			}
		}
		return null;
	}
}
