package it.unive.golisa.checker.utils.graph.edges;

import it.unive.golisa.checker.utils.graph.GraphForCheckers;
import it.unive.golisa.checker.utils.graph.nodes.StandardNode;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;
import it.unive.lisa.util.datastructures.graph.code.CodeEdge;
import java.util.Objects;

public abstract class LabeledEdge implements CodeEdge<GraphForCheckers, StandardNode, LabeledEdge>, Cloneable {

	private final StandardNode source;
	private final StandardNode destination;

	public LabeledEdge(StandardNode source, StandardNode destination) {
		this.source = source;
		this.destination = destination;
	}

	@Override
	public StandardNode getSource() {
		return source;
	}

	@Override
	public StandardNode getDestination() {
		return destination;
	}

	@Override
	public <V> boolean accept(GraphVisitor<GraphForCheckers, StandardNode, LabeledEdge, V> visitor, V tool) {
		return visitor.visit(tool, source.getGraph(), this);
	}

	@Override
	public String toString() {
		return source + " " + getEdgeLabel() + " " + destination;
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
		LabeledEdge other = (LabeledEdge) obj;
		return destination.equals(other.destination) && source.equals(other.source);
	}

	@Override
	public int compareTo(LabeledEdge o) {
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

	public abstract String getEdgeLabel();

	@Override
	public LabeledEdge clone() throws CloneNotSupportedException {
		return newInstance(source, destination);
	}
	
	

}
