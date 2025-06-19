package it.unive.golisa.checker.cosmos.panic.graph;

import it.unive.golisa.cfg.statement.GoDefer;
import it.unive.golisa.checker.utils.graph.GraphForCheckers;
import it.unive.golisa.checker.utils.graph.nodes.StandardNode;

public class PossileRecoveryNode extends StandardNode {

	/**
	 * Builds the node.
	 * 
	 * @param graph the parent graph
	 * @param cm    the code member represented by this node
	 */
	public PossileRecoveryNode(GraphForCheckers graph, GoDefer st) {
		super(graph, st);
	}

}
