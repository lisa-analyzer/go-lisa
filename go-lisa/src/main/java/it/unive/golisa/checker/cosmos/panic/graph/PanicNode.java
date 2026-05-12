package it.unive.golisa.checker.cosmos.panic.graph;

import it.unive.golisa.cfg.expression.GoPanic;
import it.unive.golisa.checker.utils.graph.GraphForCheckers;
import it.unive.golisa.checker.utils.graph.nodes.StandardNode;

/**
 * The panic node.
 */
public class PanicNode extends StandardNode {

	/**
	 * Builds the node.
	 * 
	 * @param graph the parent graph
	 * @param st    the the panic statement
	 */
	public PanicNode(GraphForCheckers graph, GoPanic st) {
		super(graph, st);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj);
	}

	@Override
	public PanicNode clone() throws CloneNotSupportedException {

		return new PanicNode(getGraph(), (GoPanic) getStatement());
	}

	

}
