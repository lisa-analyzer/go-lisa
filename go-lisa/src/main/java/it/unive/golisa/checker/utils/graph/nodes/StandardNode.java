package it.unive.golisa.checker.utils.graph.nodes;

import it.unive.golisa.checker.utils.graph.GraphForCheckers;
import it.unive.golisa.checker.utils.graph.edges.LabeledEdge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;
import it.unive.lisa.util.datastructures.graph.code.CodeNode;

/**
 * The standard node.
 */
public class StandardNode implements CodeNode<GraphForCheckers, StandardNode, LabeledEdge>, Cloneable {

	/**
	 * The parent graph.
	 */
	private final GraphForCheckers graph;

	/**
	 * The statement represented by this node.
	 */
	private final Statement st;

	/**
	 * Builds the node.
	 * 
	 * @param graph the parent graph
	 * @param st    the statement represented by this node
	 */
	public StandardNode(GraphForCheckers graph, Statement st) {
		this.graph = graph;
		this.st = st;
	}

	@Override
	public <V> boolean accept(GraphVisitor<GraphForCheckers, StandardNode, LabeledEdge, V> visitor, V tool) {
		return visitor.visit(tool, graph, this);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((st == null) ? 0 : st.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		StandardNode other = (StandardNode) obj;
		if (st == null) {
			if (other.st != null)
				return false;
		} else if (!st.equals(other.st))
			return false;
		return true;
	}

	/**
	 * Yields the parent graph.
	 * 
	 * @return the graph
	 */
	public GraphForCheckers getGraph() {
		return graph;
	}

	/**
	 * Yields the statement.
	 * 
	 * @return the statement
	 */
	public Statement getStatement() {
		return st;
	}

	@Override
	public String toString() {
		return st.toString();
	}

	@Override
	public int compareTo(StandardNode o) {
		int cmp;
		if ((cmp = st.getLocation().compareTo(o.st.getLocation())) != 0)
			return cmp;
		if ((cmp = getClass().getName().compareTo(o.getClass().getName())) != 0)
			return cmp;
		return compareToSameClassAndLocation(o);
	}

	private int compareToSameClassAndLocation(StandardNode o) {

		return 0;
	}

	@Override
	public StandardNode clone() throws CloneNotSupportedException {
		return new StandardNode(graph, st);
	}

}
