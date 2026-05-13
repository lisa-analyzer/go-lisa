package it.unive.golisa.checker.utils.graph.edges;

import it.unive.golisa.checker.utils.graph.nodes.StandardNode;

/**
 * The defer edge.
 */
public class DeferEdge extends LabeledEdge {

	/**
	 * Builds the defer edge.
	 * 
	 * @param source      the source node
	 * @param destination the destination node
	 */
	public DeferEdge(StandardNode source, StandardNode destination) {
		super(source, destination);
	}

	@Override
	public LabeledEdge newInstance(StandardNode source, StandardNode destination) {
		return new DeferEdge(source, destination);
	}

	@Override
	public String getEdgeLabel() {
		return "defer";
	}

	@Override
	public DeferEdge clone() throws CloneNotSupportedException {
		return new DeferEdge(getSource(), getDestination());
	}

	@Override
	public boolean isErrorHandling() {
		return false;
	}

}
