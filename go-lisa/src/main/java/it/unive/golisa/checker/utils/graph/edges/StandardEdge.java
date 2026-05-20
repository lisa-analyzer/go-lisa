package it.unive.golisa.checker.utils.graph.edges;

import it.unive.golisa.checker.utils.graph.nodes.StandardNode;

/**
 * The standard edge.
 */
public class StandardEdge extends LabeledEdge {

	/**
	 * Builds the standard edge.
	 * 
	 * @param source      the source node
	 * @param destination the destination node
	 */
	public StandardEdge(StandardNode source, StandardNode destination) {
		super(source, destination);
	}

	@Override
	public LabeledEdge newInstance(StandardNode source, StandardNode destination) {
		return new StandardEdge(source, destination);
	}

	@Override
	public String getEdgeLabel() {
		return "";
	}

	@Override
	public StandardEdge clone() throws CloneNotSupportedException {
		return new StandardEdge(getSource(), getDestination());
	}

	@Override
	public boolean isErrorHandling() {
		return false;
	}

}
