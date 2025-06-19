package it.unive.golisa.checker.cosmos.panic.graph;

import it.unive.golisa.cfg.expression.GoPanic;
import it.unive.golisa.checker.utils.graph.GraphForCheckers;
import it.unive.golisa.checker.utils.graph.nodes.StandardNode;

public class PanicNode extends StandardNode {

	/**
	 * Builds the node.
	 * 
	 * @param graph the parent graph
	 * @param cm    the code member represented by this node
	 */
	public PanicNode(GraphForCheckers graph, GoPanic st) {
		super(graph, st);
	}

}
