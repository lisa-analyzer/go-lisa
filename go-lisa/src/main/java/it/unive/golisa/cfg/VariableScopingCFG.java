package it.unive.golisa.cfg;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import it.unive.golisa.cfg.statement.block.IdInfo;
import it.unive.golisa.frontend.GoCodeMemberVisitor;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.ProgramPoint;
import it.unive.lisa.program.cfg.VariableTableEntry;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.NoOp;
import it.unive.lisa.program.cfg.statement.Ret;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.util.collections.workset.LIFOWorkingSet;
import it.unive.lisa.util.collections.workset.VisitOnceWorkingSet;
import it.unive.lisa.util.datastructures.graph.code.NodeList;

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

	/**
	 * Builds the control flow graph.
	 * 
	 * @param descriptor      the descriptor of this cfg
	 * @param entrypoints     the statements of this cfg that will be reachable
	 *                            from other cfgs
	 * @param adjacencyMatrix the matrix containing all the statements and the
	 *                            edges that will be part of this cfg
	 */
	public VariableScopingCFG(CodeMemberDescriptor descriptor, Collection<Statement> entrypoints,
			NodeList<CFG, Statement, Edge> adjacencyMatrix) {
		super(descriptor, entrypoints, adjacencyMatrix);
		scopingMap = new HashMap<>();
	}

	/**
	 * Builds the control flow graph.
	 * 
	 * @param descriptor the descriptor of this cfg
	 */
	public VariableScopingCFG(CodeMemberDescriptor descriptor) {
		super(descriptor);
		scopingMap = new HashMap<>();
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
		visibleIds.entrySet().forEach(e -> {
			scope.putIfAbsent(e.getKey(), new HashSet<>());
			for (IdInfo info : e.getValue())
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

	/**
	 * Yields the variable table entry concerning {@code variableName}.
	 * 
	 * @param variableName the variable to be search
	 * @param location     the location
	 * 
	 * @return the variable table entry about {@code variableName}
	 */
	public VariableTableEntry getVariableTableEntryIfExist(String variableName, CodeLocation location) {
		for (VariableTableEntry table : getDescriptor().getVariables())
			if (table.getName().equals(variableName)
					|| table.getLocation().equals(location))
				return table;
		return null;
	}

	@Override
	public void simplify() {
		// we remove all edges connecting return statements with noops
		list.getEdges().stream()
				.filter(e -> GoCodeMemberVisitor.isReturnStmt(e.getSource()) && e.getDestination() instanceof NoOp)
				.forEach(e -> list.removeEdge(e));
		// now remove all isolated noops
		list.getNodes().stream()
				.filter(n -> n instanceof NoOp && list.getIngoingEdges(n).isEmpty()
						&& list.getOutgoingEdges(n).isEmpty())
				.forEach(n -> {
					preSimplify(n);
					list.removeNode(n);
				});
		// we might have stray noop connected to a ret
		List<Statement> stray = list.getNodes().stream()
				.filter(n -> n instanceof Ret)
				.map(n -> allNonNoopPredecessorsAreReturns(list, n))
				.filter(l -> l != null)
				.flatMap(l -> l.stream())
				.collect(Collectors.toList());
		stray.forEach(n -> {
			preSimplify(n);
			list.removeNode(n);
		});

		super.simplify();
	}

	private Collection<Statement> allNonNoopPredecessorsAreReturns(NodeList<CFG, Statement, Edge> block,
			Statement last) {
		VisitOnceWorkingSet<Statement> ws = VisitOnceWorkingSet.mk(LIFOWorkingSet.mk());
		ws.push(last);
		while (!ws.isEmpty()) {
			Statement current = ws.pop();
			if (!(current instanceof NoOp) && current != last)
				return null;
			block.predecessorsOf(current).forEach(ws::push);
		}
		return ws.getSeen();
	}
}
