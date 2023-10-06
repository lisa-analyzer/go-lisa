package it.unive.golisa.checker.readwrite.graph.edges;

import it.unive.golisa.checker.readwrite.graph.ReadWriteNode;

public class StandardEdge extends ReadWriteEdge {

	public StandardEdge(ReadWriteNode source, ReadWriteNode destination) {
		super(source, destination);
	}

	@Override
	public ReadWriteEdge newInstance(ReadWriteNode source, ReadWriteNode destination) {
		return new StandardEdge(source, destination);
	}

	@Override
	public String getEdgeSymbol() {
		return "";
	}

}
