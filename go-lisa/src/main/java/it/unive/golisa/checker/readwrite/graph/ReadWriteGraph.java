package it.unive.golisa.checker.readwrite.graph;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiFunction;

import org.graphstream.graph.Edge;
import org.graphstream.graph.Element;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.stream.file.FileSinkDOT;

import it.unive.golisa.checker.readwrite.graph.edges.ReadWriteEdge;
import it.unive.golisa.checker.readwrite.graph.edges.StandardEdge;
import it.unive.lisa.outputs.DotGraph;
import it.unive.lisa.outputs.serializableGraph.SerializableEdge;
import it.unive.lisa.outputs.serializableGraph.SerializableGraph;
import it.unive.lisa.outputs.serializableGraph.SerializableNode;
import it.unive.lisa.outputs.serializableGraph.SerializableNodeDescription;
import it.unive.lisa.outputs.serializableGraph.SerializableValue;
import it.unive.lisa.util.datastructures.graph.code.CodeGraph;

public class ReadWriteGraph extends CodeGraph<ReadWriteGraph, ReadWriteNode, ReadWriteEdge> {

	private final String name;
	
	public ReadWriteGraph(String name) {
		super(new StandardEdge(null, null));
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public SerializableGraph toSerializableGraph(
			BiFunction<ReadWriteGraph, ReadWriteNode, SerializableValue> descriptionGenerator) {

		SortedSet<SerializableNode> nodes = new TreeSet<>();
		Map<ReadWriteNode, Integer> nodeIds = new HashMap<>();
		SortedSet<SerializableNodeDescription> descrs = new TreeSet<>();
		SortedSet<SerializableEdge> edges = new TreeSet<>();
		
		int counter = 0;
		for (ReadWriteNode node : getNodes()) {
			addNode(counter, nodes, descrs, node, descriptionGenerator);
			nodeIds.put(node, counter);
			counter++;
		}

		for (ReadWriteNode src : getNodes())
			for (ReadWriteNode dest : followersOf(src))
				for (ReadWriteEdge edge : list.getEdgesConnecting(src, dest))
						edges.add(new SerializableEdge(nodeIds.get(src), nodeIds.get(dest),
								edge.getClass().getSimpleName()));

		return new CustomSerializableGraph(name, null, nodes, edges, descrs);
	}


	private void addNode(
			int id,
			SortedSet<SerializableNode> nodes,
			SortedSet<SerializableNodeDescription> descrs,
			ReadWriteNode node,
			BiFunction<ReadWriteGraph, ReadWriteNode, SerializableValue> descriptionGenerator) {
		SerializableNode n = new SerializableNode(id, Collections.emptyList(), node.toString() +"\n\n"+ "Location: " + node.getStatement().getLocation());
		nodes.add(n);
		if (descriptionGenerator != null) {
			SerializableValue value = descriptionGenerator.apply(this, node);
			if (value != null)
				descrs.add(new SerializableNodeDescription(id, value));
		}
	}

	
	static class CustomSerializableGraph extends SerializableGraph {

		public CustomSerializableGraph(String name, String description, SortedSet<SerializableNode> nodes,
				SortedSet<SerializableEdge> edges, SortedSet<SerializableNodeDescription> descriptions) {
			super(name, description, nodes, edges, descriptions);
		}

		@Override
		public DotGraph toDot() {
			DotGraph graph = new CustomDotGraph(getName());

			Set<Integer> hasFollows = new HashSet<>();
			Set<Integer> hasPreds = new HashSet<>();
			Set<Integer> inners = new HashSet<>();
			Map<Integer, SerializableValue> labels = new HashMap<>();

			getEdges().forEach(e -> {
				hasFollows.add(e.getSourceId());
				hasPreds.add(e.getDestId());
			});

			getDescriptions().forEach(d -> labels.put(d.getNodeId(), d.getDescription()));
			getNodes().forEach(n -> inners.addAll(n.getSubNodes()));

			for (SerializableNode n : getNodes())
				if (!inners.contains(n.getId()))
					graph.addNode(n, !hasPreds.contains(n.getId()), !hasFollows.contains(n.getId()),
							labels.get(n.getId()));

			for (SerializableEdge e : getEdges())
				graph.addEdge(e);

			return graph;
		}
	}

	static class CustomDotGraph extends DotGraph {

		public CustomDotGraph(String title) {
			super(title);
		}

		@Override
		public void addEdge(SerializableEdge edge) {
			long id = edge.getSourceId();
			long id1 = edge.getDestId();

			Edge e = graph.addEdge(edgeName(id, id1, edge), nodeName(id), nodeName(id1), true);

			switch (edge.getKind()) {
			case "DeferEdge":
				e.setAttribute(COLOR, COLOR_RED);
				e.setAttribute(LABEL, "DEFER");
				e.setAttribute(STYLE, CONDITIONAL_EDGE_STYLE);
				break;
			case "CallerEdge":
				e.setAttribute(COLOR, COLOR_BLUE);
				e.setAttribute(LABEL, "CALLER");
				break;
			case "CalleeEdge":
				e.setAttribute(COLOR, COLOR_GRAY);
				e.setAttribute(LABEL, "CALLEE");
				break;
			case "StandardSEdge":
			default:
				e.setAttribute(COLOR, COLOR_BLACK);
				break;
			}
		}

		protected static String edgeName(long src, long dest, SerializableEdge edge) {
			return "edge-" + src + "-" + dest + "-" + edge.getKind();
		}

		private class CustomDotSink extends FileSinkDOT {

			@Override
			protected void outputHeader() throws IOException {
				out = (PrintWriter) output;
				out.printf("%s {%n", "digraph");
			}

			@Override
			protected String outputAttribute(String key, Object value, boolean first) {
				boolean quote = true;

				if (value instanceof Number || key.equals(LABEL))
					// labels that we output are always in html format
					// so no need to quote them
					quote = false;

				Object quoting = quote ? "\"" : "";
				return String.format("%s%s=%s%s%s", first ? "" : ",", key, quoting, value, quoting);
			}

			@Override
			protected String outputAttributes(Element e) {
				if (e.getAttributeCount() == 0)
					return "";

				Map<String, String> attrs = new HashMap<>();
				e.attributeKeys().forEach(key -> attrs.put(key, outputAttribute(key, e.getAttribute(key), true)));

				StringBuilder buffer = new StringBuilder("[");
				for (Entry<String, String> entry : attrs.entrySet())
					if (!entry.getKey().equals(LABEL))
						buffer.append(entry.getValue()).append(",");

				if (attrs.containsKey(LABEL))
					buffer.append(attrs.get(LABEL));

				String result = buffer.toString();
				if (result.endsWith(","))
					result = result.substring(0, result.length() - 1);

				return result + "]";
			}
		}

		@Override
		public void dump(Writer writer) throws IOException {
			FileSinkDOT sink = new CustomDotSink() {
				@Override
				protected void outputEndOfFile() throws IOException {
					LegendClusterSink legend = new LegendClusterSink();
					legend.setDirected(true);
					StringWriter sw = new StringWriter();
					legend.writeAll(new Legend().graph, sw);
					out.printf("%s%n", sw.toString());
					super.outputEndOfFile();
				}
			};
			sink.setDirected(true);
			sink.writeAll(graph, writer);
		}

		private class LegendClusterSink extends CustomDotSink {
			@Override
			protected void outputHeader() throws IOException {
				out = (PrintWriter) output;
				out.printf("%s {%n", "subgraph cluster_legend");
				out.printf("\tlabel=\"Legend\";%n");
				out.printf("\tstyle=dotted;%n");
				out.printf("\tnode [shape=plaintext];%n");
			}
		}

		private static final class Legend {
			private final org.graphstream.graph.Graph graph;

			private Legend() {
				graph = new MultiGraph("legend");
				org.graphstream.graph.Node l = graph.addNode("legend");
				StringBuilder builder = new StringBuilder();
				builder.append("<");
				builder.append("<table border=\"0\" cellpadding=\"2\" cellspacing=\"0\" cellborder=\"0\">");
				builder.append("<tr><td align=\"right\">node border&nbsp;</td><td align=\"left\"><font color=\"");
				builder.append(NORMAL_NODE_COLOR);
				builder.append("\">");
				builder.append(NORMAL_NODE_COLOR);
				builder.append("</font>, single</td></tr>");
				builder.append("<tr><td align=\"right\">write instruction border&nbsp;</td><td align=\"left\"><font color=\"");
				builder.append(SPECIAL_NODE_COLOR);
				builder.append("\">");
				builder.append(SPECIAL_NODE_COLOR);
				builder.append("</font>, single</td></tr>");
				builder.append("<tr><td align=\"right\">read instruction border&nbsp;</td><td align=\"left\"><font color=\"");
				builder.append(SPECIAL_NODE_COLOR);
				builder.append("\">");
				builder.append(SPECIAL_NODE_COLOR);
				builder.append("</font>, double</td></tr>");
				builder.append("<tr><td align=\"right\">sequential edge&nbsp;</td><td align=\"left\"><font color=\"");
				builder.append(COLOR_BLACK);
				builder.append("\">");
				builder.append(COLOR_BLACK);
				builder.append("</font>, solid</td></tr>");
				builder.append("<tr><td align=\"right\">caller edge&nbsp;</td><td align=\"left\"><font color=\"");
				builder.append(COLOR_BLUE);
				builder.append("\">");
				builder.append(COLOR_BLUE);
				builder.append("</font>, solid</td></tr>");
				builder.append("<tr><td align=\"right\">callee edge&nbsp;</td><td align=\"left\"><font color=\"");
				builder.append(COLOR_GRAY);
				builder.append("\">");
				builder.append(COLOR_GRAY);
				builder.append("</font>, solid</td></tr>");
				builder.append("<tr><td align=\"right\">defer edge&nbsp;</td><td align=\"left\"><font color=\"");
				builder.append(COLOR_RED);
				builder.append("\">");
				builder.append(COLOR_RED);
				builder.append("</font>, ");
				builder.append(CONDITIONAL_EDGE_STYLE);
				builder.append("</td></tr>");
				builder.append("</table>");
				builder.append(">");
				l.setAttribute("label", builder.toString());
			}
		}
	}
}
