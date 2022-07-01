package it.unive.golisa.analysis.entrypoints;

import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.sets.NonDeterminismAnnotationSet;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

/**
 * The class contains utility methods to handle sets of entry points.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class EntryPointsUtils {

	/**
	 * The method checks if in an annotation set are present at least one source
	 * and one sink annotation for a non-determinism analysis.
	 * 
	 * @param appliedAnnotations, the set of annotations to check
	 * @param annotationSets,     the set of annotation to find (sink/source)
	 * 
	 * @return {@code true} if exist at least a source and a sink annotation,
	 *             otherwise {@code false}.
	 */
	public static boolean containsPossibleEntryPointsForAnalysis(
			Set<Pair<CodeAnnotation, CFGDescriptor>> appliedAnnotations,
			NonDeterminismAnnotationSet... annotationSets) {

		boolean atLeastOneSource = false;
		boolean atLeastOneDestination = false;
		for (NonDeterminismAnnotationSet as : annotationSets) {
			Set<? extends CodeAnnotation> sources = as.getAnnotationForSources();
			Set<? extends CodeAnnotation> destinations = as.getAnnotationForDestinations();

			if (!atLeastOneSource && appliedAnnotations.stream().anyMatch(e -> sources.contains(e.getLeft())))
				atLeastOneSource = true;
			if (!atLeastOneDestination && appliedAnnotations.stream().anyMatch(e -> destinations.contains(e.getLeft())))
				atLeastOneDestination = true;

			if (atLeastOneSource && atLeastOneDestination)
				break;
		}

		return atLeastOneSource && atLeastOneDestination;
	}

	/**
	 * Yields the descriptor set of possible entry points for the analysis
	 * 
	 * @param appliedAnnotations the applied annotations
	 * @param annotationSets     the set of annotation related to the analysis
	 *                               of non-determinism
	 * 
	 * @return the set of descriptors
	 */
	private static Set<CFGDescriptor> getDescriptorOfPossibleEntryPointsForAnalysis(
			Set<Pair<CodeAnnotation, CFGDescriptor>> appliedAnnotations,
			NonDeterminismAnnotationSet... annotationSets) {

		Set<CFGDescriptor> descriptors = new HashSet<>();
		for (NonDeterminismAnnotationSet as : annotationSets) {
			Set<? extends CodeAnnotation> sources = as.getAnnotationForSources();
			appliedAnnotations.stream()
					.forEach(e -> {
						if (sources.contains(e.getLeft()))
							descriptors.add(e.getRight());
					});
		}

		return descriptors;
	}

	/**
	 * Compute the entry points from the possible entry points for the analysis
	 * 
	 * @param program            the program the applied annotations
	 * @param appliedAnnotations the set of annotation related to the analysis
	 *                               of non-determinism
	 * @param annotationSets
	 * 
	 * @return the set of entry points
	 */
	public static Set<CFG> computeEntryPointSetFromPossibleEntryPointsForAnalysis(Program program,
			Set<Pair<CodeAnnotation, CFGDescriptor>> appliedAnnotations,
			NonDeterminismAnnotationSet... annotationSets) {

		Set<CFG> set = new HashSet<>();

		Set<CFGDescriptor> descriptors = getDescriptorOfPossibleEntryPointsForAnalysis(appliedAnnotations,
				annotationSets);

		for (CFG cfg : program.getAllCFGs()) {
			LinkedList<Statement> possibleEntries = new LinkedList<>();
			cfg.accept(new PossibleEntryPointExtractor(descriptors), possibleEntries);
			if (!possibleEntries.isEmpty())
				set.add(cfg);
		}

		for (CompilationUnit unit : program.getUnits())
			for (CFG cfg : unit.getAllCFGs()) {
				LinkedList<Statement> possibleEntries = new LinkedList<>();
				cfg.accept(new PossibleEntryPointExtractor(descriptors), possibleEntries);
				if (!possibleEntries.isEmpty())
					set.add(cfg);
			}
		return set;

	}

	/**
	 * The class represents the extractor of possible entry points
	 */
	private static class PossibleEntryPointExtractor
			implements GraphVisitor<CFG, Statement, Edge, Collection<Statement>> {

		final Set<CFGDescriptor> descriptors;

		/**
		 * Builds an instance of the extractor of possible entry points
		 * 
		 * @param descriptors the descriptors
		 */
		public PossibleEntryPointExtractor(Set<CFGDescriptor> descriptors) {
			this.descriptors = descriptors;
		}

		@Override
		public boolean visit(Collection<Statement> tool, CFG graph) {
			return true;
		}

		@Override
		public boolean visit(Collection<Statement> tool, CFG graph, Statement node) {

			if (matchSignatureDescriptor(node, tool))
				tool.add(node);

			return true;
		}

		/**
		 * Check if a node is a call, and in case checks if the signature match
		 * with te descriptors
		 * 
		 * @param node the statement
		 * @param tool the tool
		 * 
		 * @return {@code true} when the signature match, otherwise
		 *             {@code false}
		 */
		private boolean matchSignatureDescriptor(Statement node, Collection<Statement> tool) {
			if (node instanceof Call)
				if (descriptors.stream().anyMatch(d -> d.getFullName().equals(((Call) node).getFullTargetName())
						&& d.getFormals().length == ((Call) node).getParameters().length))
					return true;
			return false;
		}

		@Override
		public boolean visit(Collection<Statement> tool, CFG graph, Edge edge) {
			return true;
		}
	}
}
