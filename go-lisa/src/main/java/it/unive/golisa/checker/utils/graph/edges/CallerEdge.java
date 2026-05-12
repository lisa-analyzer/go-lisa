package it.unive.golisa.checker.utils.graph.edges;

import it.unive.golisa.checker.utils.graph.nodes.StandardNode;

public class CallerEdge extends LabeledEdge {

	public CallerEdge(StandardNode source, StandardNode destination) {
		super(source, destination);
	}

	@Override
	public LabeledEdge newInstance(StandardNode source, StandardNode destination) {
		return new CallerEdge(source, destination);
	}

	@Override
	public String getEdgeLabel() {
		return "caller";
	}

	@Override
	public CallerEdge clone() throws CloneNotSupportedException {
		return new CallerEdge(getSource(), getDestination());
	}

	@Override
	public boolean isErrorHandling() {
		return false;
	}
}
