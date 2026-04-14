package it.unive.golisa.checker.utils.graph.edges;

import it.unive.golisa.checker.utils.graph.nodes.StandardNode;

public class DeferEdge extends LabeledEdge {

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
	
}
