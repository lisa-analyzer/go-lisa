package it.unive.golisa.checker.utils.graph.edges;

import it.unive.golisa.checker.utils.graph.nodes.StandardNode;

/**
 * The callee edge.
 */
public class CalleeEdge extends LabeledEdge {

	/**
	 * Builds the callee edge.
	 * 
	 * @param source the source node
	 * @param destination the destination node
	 */
	public CalleeEdge(StandardNode source, StandardNode destination) {
		super(source, destination);
	}

	@Override
	public LabeledEdge newInstance(StandardNode source, StandardNode destination) {
		return new CalleeEdge(source, destination);
	}

	@Override
	public String getEdgeLabel() {
		return "callee";
	}

	@Override
	public CalleeEdge clone() throws CloneNotSupportedException {
		return new CalleeEdge(getSource(), getDestination());
	}

	@Override
	public boolean isErrorHandling() {
		return false;
	}
	
}
