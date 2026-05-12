package it.unive.golisa.checker.utils.graph.edges;

import it.unive.golisa.checker.utils.graph.nodes.StandardNode;

public class CalleeEdge extends LabeledEdge {

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
