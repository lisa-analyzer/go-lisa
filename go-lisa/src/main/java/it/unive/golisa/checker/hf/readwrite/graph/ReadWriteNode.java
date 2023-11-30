package it.unive.golisa.checker.hf.readwrite.graph;

import it.unive.golisa.checker.hf.readwrite.graph.edges.ReadWriteEdge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;
import it.unive.lisa.util.datastructures.graph.code.CodeNode;

public class ReadWriteNode implements CodeNode<ReadWriteGraph, ReadWriteNode, ReadWriteEdge>{

	private final ReadWriteGraph graph;
	private final Statement st;
	private int offset;
	/**
	 * Builds the node.
	 * 
	 * @param graph the parent graph
	 * @param cm    the code member represented by this node
	 */
	public ReadWriteNode(ReadWriteGraph graph, Statement st) {
		this.graph = graph;
		this.st = st;
		offset = st.getOffset();
	}
	
	@Override
	public <V> boolean accept(GraphVisitor<ReadWriteGraph, ReadWriteNode, ReadWriteEdge, V> visitor, V tool) {
		return visitor.visit(tool, graph, this);
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((st == null) ? 0 : st.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReadWriteNode other = (ReadWriteNode) obj;
		if (st == null) {
			if (other.st != null)
				return false;
		} else if (!st.equals(other.st))
			return false;
		return true;
	}
	
	public ReadWriteGraph getGraph() {
		return graph;
	}

	public Statement getStatement() {
		return st;
	}

	@Override
	public String toString() {
		return st.toString();
	}

	@Override
	public int compareTo(ReadWriteNode o) {
		int cmp;
		if ((cmp = st.getLocation().compareTo(o.st.getLocation())) != 0)
			return cmp;
		if ((cmp = getClass().getName().compareTo(o.getClass().getName())) != 0)
			return cmp;
		return compareToSameClassAndLocation(o);
	}

	private int compareToSameClassAndLocation(ReadWriteNode o) {
		
		return 0;
	}

	@Override
	public int setOffset(int offset) {
		
		return this.offset = offset;
	}

	@Override
	public int getOffset() {
		return offset;
	}

}
