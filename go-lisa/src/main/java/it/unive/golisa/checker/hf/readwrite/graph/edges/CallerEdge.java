package it.unive.golisa.checker.hf.readwrite.graph.edges;

import it.unive.golisa.checker.hf.readwrite.graph.ReadWriteNode;

public class CallerEdge extends ReadWriteEdge {

	public CallerEdge(ReadWriteNode source, ReadWriteNode destination) {
		super(source, destination);
	}

	@Override
	public ReadWriteEdge newInstance(ReadWriteNode source, ReadWriteNode destination) {
		return new CallerEdge(source, destination);
	}

	@Override
	public String getEdgeSymbol() {
		return "caller";
	}

}
