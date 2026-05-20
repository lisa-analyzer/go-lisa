package it.unive.golisa.checker.cosmos.panic.graph;

import it.unive.golisa.cfg.statement.GoDefer;
import it.unive.golisa.checker.utils.graph.GraphForCheckers;
import it.unive.golisa.checker.utils.graph.nodes.StandardNode;

/**
 * The recovery node.
 */
public class RecoveryNode extends StandardNode {

	/**
	 * Builds the node.
	 * 
	 * @param graph the parent graph
	 * @param st    the defer of recovery
	 */
	public RecoveryNode(GraphForCheckers graph, GoDefer st) {
		super(graph, st);
	}

	@Override
	public RecoveryNode clone() throws CloneNotSupportedException {

		return new RecoveryNode(getGraph(), (GoDefer) getStatement());
	}
}
