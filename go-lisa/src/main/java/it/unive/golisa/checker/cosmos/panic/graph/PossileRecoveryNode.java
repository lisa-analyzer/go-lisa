package it.unive.golisa.checker.cosmos.panic.graph;

import it.unive.golisa.cfg.statement.GoDefer;
import it.unive.golisa.checker.utils.graph.GraphForCheckers;
import it.unive.golisa.checker.utils.graph.nodes.StandardNode;

/**
 * The possible recovery node.
 */
public class PossileRecoveryNode extends StandardNode {

	/**
	 * Builds the node.
	 * 
	 * @param graph the parent graph
	 * @param st    the defer node
	 */
	public PossileRecoveryNode(GraphForCheckers graph, GoDefer st) {
		super(graph, st);
	}


	@Override
	public PossileRecoveryNode clone() throws CloneNotSupportedException {

		return new PossileRecoveryNode(getGraph(), (GoDefer) getStatement());
	}

}
