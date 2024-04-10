package it.unive.golisa.checker.hf.readwrite.graph.edges;

import it.unive.golisa.checker.hf.readwrite.graph.ReadWriteNode;

public class DeferEdge extends ReadWriteEdge {

	public DeferEdge(ReadWriteNode source, ReadWriteNode destination) {
		super(source, destination);
	}

	@Override
	public ReadWriteEdge newInstance(ReadWriteNode source, ReadWriteNode destination) {
		return new DeferEdge(source, destination);
	}

	@Override
	public String getEdgeSymbol() {
		return "defer";
	}

}
