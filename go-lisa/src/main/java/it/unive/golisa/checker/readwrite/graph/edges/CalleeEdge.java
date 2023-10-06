package it.unive.golisa.checker.readwrite.graph.edges;

import it.unive.golisa.checker.readwrite.graph.ReadWriteNode;

public class CalleeEdge extends ReadWriteEdge {

	public CalleeEdge(ReadWriteNode source, ReadWriteNode destination) {
		super(source, destination);
	}

	@Override
	public ReadWriteEdge newInstance(ReadWriteNode source, ReadWriteNode destination) {
		return new CalleeEdge(source, destination);
	}

	@Override
	public String getEdgeSymbol() {
		return "callee";
	}

}
