package it.unive.golisa.cfg;

import it.unive.golisa.cfg.statement.block.IdInfo;
import it.unive.lisa.program.annotations.Annotations;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.VariableTableEntry;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.VariableRef;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.symbolic.value.Variable;
import it.unive.lisa.util.datastructures.graph.code.NodeList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A control flow graph, that has {@link Statement}s as nodes and {@link Edge}s
 * as edges. It also can contains a mapping between the statements and the IDs
 * visible in those statements.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class VariableScopingCFG extends CFG {

	/**
	 * The mapping between the statements and the IDs visible in those
	 * statements.
	 */
	private final Map<Statement, Map<String, Set<IdInfo>>> scopingMap;
	
	private final Map<VariableRef, Annotations> annotationsMap;
	

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
			NodeList<CFG, Statement, Edge> adjacencyMatrix) {
		super(descriptor, entrypoints, adjacencyMatrix);
		scopingMap = new HashMap<>();
		annotationsMap = new HashMap<>();
	}

	/**
	 * Builds the control flow graph.
	 * 
	 * @param descriptor the descriptor of this cfg
	 */
	public VariableScopingCFG(CFGDescriptor descriptor) {
		super(descriptor);
		scopingMap = new HashMap<>();
		annotationsMap = new HashMap<>();
	}

	/**
	 * Adds the given node to the set of nodes, and collect IDs visible in that
	 * node.
	 * 
	 * @param node       the node to add
	 * @param visibleIds the IDs visible to collect
	 */
	public void registerScoping(Statement node, Map<String, Set<IdInfo>> visibleIds) {
		Map<String, Set<IdInfo>> scope = new HashMap<>();
		visibleIds.entrySet().forEach(e -> 
		{
			scope.putIfAbsent(e.getKey(), new HashSet<>());
			for(IdInfo info : e.getValue())
				scope.get(e.getKey()).add(info);
		});
		scopingMap.put(node, scope);
	}

	/**
	 * Yields the IDs visible from a statement.
	 * 
	 * @param node the node to add
	 * 
	 * @return the visible IDs
	 */
	public Map<String, Set<IdInfo>> getVisibleIds(Statement node) {
		return scopingMap.get(node);
	}
	
	public Map<VariableRef, Annotations> getAnnotationsMap() {
		return annotationsMap;
	}

	@Override
	public Collection<Statement> getGuards(ProgramPoint pp) {
		// TODO remove this when the fix will be available in lisa
		Collection<Statement> guards = super.getGuards(pp);
		if (!guards.isEmpty())
			return guards;

		if (pp instanceof Call) {
			Call original = (Call) pp;
			while (original.getSource() != null)
				original = original.getSource();
			if (original != pp)
				return super.getGuards(original);
		}

		return guards;
	}
	
	public VariableTableEntry getVariableTableEntryIfExist(String variableName, CodeLocation location) {
		for(VariableTableEntry table : getDescriptor().getVariables())
			if(table.getName().equals(variableName) 
					|| table.getLocation().equals(location))
				return table;
		return null;
		
	}
}
