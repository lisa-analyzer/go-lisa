package it.unive.golisa.checker.hf.readwrite.graph.edges;

import java.util.Objects;

import it.unive.golisa.checker.hf.readwrite.graph.ReadWriteGraph;
import it.unive.golisa.checker.hf.readwrite.graph.ReadWriteNode;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;
import it.unive.lisa.util.datastructures.graph.code.CodeEdge;

public abstract class ReadWriteEdge implements CodeEdge<ReadWriteGraph, ReadWriteNode, ReadWriteEdge> {

	private final ReadWriteNode source;
	private final ReadWriteNode destination;

	public ReadWriteEdge(ReadWriteNode source, ReadWriteNode destination) {
		this.source = source;
		this.destination = destination;
	}

	@Override
	public ReadWriteNode getSource() {
		return source;
	}

	@Override
	public ReadWriteNode getDestination() {
		return destination;
	}

	@Override
	public <V> boolean accept(GraphVisitor<ReadWriteGraph, ReadWriteNode, ReadWriteEdge, V> visitor, V tool) {
		return visitor.visit(tool, source.getGraph(), this);
	}

	@Override
	public String toString() {
		return source + " " + getEdgeSymbol() + " " + destination;
	}

	@Override
	public int hashCode() {
		return Objects.hash(destination, source);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReadWriteEdge other = (ReadWriteEdge) obj;
		return Objects.equals(destination, other.destination) && Objects.equals(source, other.source);
	}

	@Override
	public int compareTo(ReadWriteEdge o) {
		int cmp;
		if ((cmp = source.compareTo(o.source)) != 0)
			return cmp;
		if ((cmp = destination.compareTo(o.destination)) != 0)
			return cmp;
		return getClass().getName().compareTo(o.getClass().getName());
	}

	@Override
	public boolean isUnconditional() {
		return false;
	}

	public abstract String getEdgeSymbol();

}
