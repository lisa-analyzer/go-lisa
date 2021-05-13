package it.unive.golisa.cfg;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.util.datastructures.graph.AdjacencyMatrix;

/**
 * A control flow graph, that has {@link Statement}s as nodes and {@link Edge}s
 * as edges. It also can contains a mapping between the statements and the IDs visible in those statements
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class VariableScopingCFG extends CFG {

	/**
	 * The mapping between the statements and the IDs visible in those statements
	 */
	private final Map<Statement, Map<String, VariableRef>> scoopingMap;
	
	/**
	 * Builds the control flow graph.
	 * 
	 * @param descriptor      the descriptor of this cfg
	 * @param entrypoints     the statements of this cfg that will be reachable
	 *                            from other cfgs
	 * @param adjacencyMatrix the matrix containing all the statements and the
	 *                            edges that will be part of this cfg
	 */
	public VariableScopingCFG(CFGDescriptor descriptor, Collection<Statement> entrypoints,
			AdjacencyMatrix<Statement, Edge, CFG> adjacencyMatrix) {
		super(descriptor, entrypoints, adjacencyMatrix);
		scoopingMap = new HashMap<>();
	}
	
	/**
	 * Builds the control flow graph.
	 * 
	 * @param descriptor the descriptor of this cfg
	 */
	public VariableScopingCFG(CFGDescriptor descriptor) {
		super(descriptor);
		scoopingMap = new HashMap<>();
	}
	
	/**
	 * Adds the given node to the set of nodes, and collect  IDs visible in that node
	 * 
	 * @param node the node to add
	 * 
	 * @param visibleIds the IDs visible to collect
	 */
	public void addNode(Statement node, Map<String, VariableRef> visibleIds) {
		scoopingMap.put(node, new HashMap<>(visibleIds));
		super.addNode(node);
	}

	
	/**
	 * Yields the IDs visible from a statement
	 * 
	 * @param node the node to add
	 * 
	 * @return the visible IDs  
	 */
	public Map<String, VariableRef> getVisibleIds(Statement node){
		return scoopingMap.get(node);
	}
}
