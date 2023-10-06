package it.unive.golisa.checker.readwrite.graph;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

import it.unive.lisa.outputs.serializableGraph.SerializableEdge;
import it.unive.lisa.outputs.serializableGraph.SerializableGraph;
import it.unive.lisa.outputs.serializableGraph.SerializableNode;
import it.unive.lisa.outputs.serializableGraph.SerializableNodeDescription;
import it.unive.lisa.outputs.serializableGraph.SerializableValue;
import it.unive.lisa.program.cfg.statement.NaryExpression;
import it.unive.lisa.program.cfg.statement.NaryStatement;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.util.datastructures.graph.BaseGraph;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;

public class ReadWriteGraph2 extends BaseGraph<ReadWriteGraph2, ReadWriteNode, ReadWriteEdge> {
	
	private final String name;
	
	

	public ReadWriteGraph2(String name) {
		this.name = name;
	}

	public SerializableGraph toSerializableGraph() {
		return fromReadWriteGraph(this, null);
	}
	
	/**
	 * Builds a {@link SerializableGraph} starting from the given {@link ReadWriteGraph2},
	 * using the given function to generate extra descriptions for each
	 * statement.
	 * 
	 * @param source               the source graph
	 * @param descriptionGenerator the function that can generate descriptions
	 *                                 from statements
	 *
	 * @return the serializable version of that graph
	 */
	public static SerializableGraph fromReadWriteGraph(ReadWriteGraph2 source,
			BiFunction<ReadWriteGraph2, Statement, SerializableValue> descriptionGenerator) {
		String name = source.getName();
		String desc;
		// if (source instanceof AnalyzedCFG<?, ?, ?, ?> && !((AnalyzedCFG<?, ?, ?, ?>) source).getId().isStartingId())
		//	desc = ((AnalyzedCFG<?, ?, ?, ?>) source).getId().toString();
		// else
			desc = null;

		SortedSet<SerializableNode> nodes = new TreeSet<>();
		SortedSet<SerializableNodeDescription> descrs = new TreeSet<>();
		SortedSet<SerializableEdge> edges = new TreeSet<>();
		
		id = 0;
		mapIds = new HashMap<Statement, Integer>();
		
		for (ReadWriteNode node : source.adjacencyMatrix.getNodes()) {
			Map<Statement, List<Statement>> inners = new IdentityHashMap<>();
			node.accept(new InnerNodeExtractor(), inners);
			for (Statement inner : inners.keySet())
				addNode(source, nodes, descrs, inner, inners.getOrDefault(inner, Collections.emptyList()),
						descriptionGenerator);
			
		//	addNode(source, nodes, descrs, node.getStatement(), inners.getOrDefault(node.getStatement(), Collections.emptyList()),
		//			descriptionGenerator);
		}

		for (ReadWriteNode src : source.adjacencyMatrix.getNodes())
			for (ReadWriteNode dest : source.followersOf(src))
				for (ReadWriteEdge edge : source.getEdgesConnecting(src, dest))
					edges.add(new SerializableEdge(mapIds.get(src.getStatement()), mapIds.get(dest.getStatement()), edge.getType().toString()));

		return new SerializableGraph(name, desc, nodes, edges, descrs);
	}

	public String getName() {
		
		return name;
	}
	
	private static int id;
	private static Map<Statement, Integer> mapIds;  
	
	private static void addNode(
			ReadWriteGraph2 source,
			SortedSet<SerializableNode> nodes,
			SortedSet<SerializableNodeDescription> descrs,
			Statement node,
			List<Statement> inners,
			BiFunction<ReadWriteGraph2, Statement, SerializableValue> descriptionGenerator) {
		
		mapIds.put(node, id);
		id++;
		
		List<Integer> innerIds = inners.stream().map(st ->  id++).collect(Collectors.toList());
		SerializableNode n = new SerializableNode(mapIds.get(node), innerIds, node.toString()+"\n"+"Location: "+node.getLocation());
		nodes.add(n);
		if (descriptionGenerator != null) {
			SerializableValue value = descriptionGenerator.apply(source, node);
			if (value != null)
				descrs.add(new SerializableNodeDescription(mapIds.get(node), value));
		}


		
	}

	private static class InnerNodeExtractor
			implements GraphVisitor<ReadWriteGraph2, ReadWriteNode, ReadWriteEdge, Map<Statement, List<Statement>>> {

		@Override
		public boolean visit(Map<Statement, List<Statement>> tool, ReadWriteGraph2 graph, ReadWriteNode node) {
			List<Statement> inners = tool.computeIfAbsent(node.getStatement(), st -> new LinkedList<>());
			if (node.getStatement() instanceof NaryStatement)
				inners.addAll(Arrays.asList(((NaryStatement) node.getStatement()).getSubExpressions()));
			else if (node.getStatement() instanceof NaryExpression)
				inners.addAll(Arrays.asList(((NaryExpression) node.getStatement()).getSubExpressions()));
			return true;
		}
	}
	
	
	
}
