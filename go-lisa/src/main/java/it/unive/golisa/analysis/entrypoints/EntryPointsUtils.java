package it.unive.golisa.analysis.entrypoints;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.sets.NonDeterminismAnnotationSet;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;

public class EntryPointsUtils {

	public static boolean containsPossibleEntryPointsForAnalysis(Set<Pair<CodeAnnotation, CFGDescriptor>> appliedAnnotations, NonDeterminismAnnotationSet... annotationSets) {
		
		boolean atLeastOneSource = false;
		boolean atLeastOneDestination = false;
		for(NonDeterminismAnnotationSet as : annotationSets) {
			Set<? extends CodeAnnotation> sources = as.getAnnotationForSources();
			Set<? extends CodeAnnotation> destinations = as.getAnnotationForDestinations();
			
			if(!atLeastOneSource && appliedAnnotations.stream().anyMatch(e -> sources.contains(e.getLeft())))
				atLeastOneSource = true;
			if(!atLeastOneDestination && appliedAnnotations.stream().anyMatch(e -> destinations.contains(e.getLeft())))
				atLeastOneDestination = true;
			
			if(atLeastOneSource && atLeastOneDestination)
				break;
		}
		
		return atLeastOneSource && atLeastOneDestination;
	}
	
	private static Set<CFGDescriptor> getDescriptorOfPossibleEntryPointsForAnalysis(Set<Pair<CodeAnnotation, CFGDescriptor>> appliedAnnotations, NonDeterminismAnnotationSet... annotationSets) {
		
		Set<CFGDescriptor> descriptors = new HashSet<>();
		for(NonDeterminismAnnotationSet as : annotationSets) {
			Set<? extends CodeAnnotation> sources = as.getAnnotationForSources();
			appliedAnnotations.stream()
						.forEach(e -> { if(sources.contains(e.getLeft()))
										 descriptors.add(e.getRight());	
						});	
		}
		
		return descriptors;
	}
	
		public static Set<CFG> computeEntryPointSetFromPossibleEntryPointsForAnalysis(Program program, Set<Pair<CodeAnnotation, CFGDescriptor>> appliedAnnotations, NonDeterminismAnnotationSet... annotationSets) {
			
			Set<CFG> set = new HashSet<>();
			
			Set<CFGDescriptor> descriptors = getDescriptorOfPossibleEntryPointsForAnalysis(appliedAnnotations, annotationSets);
			
			for(CFG cfg : program.getAllCFGs()) {
				LinkedList<Statement> possibleEntries = new LinkedList<>();
				cfg.accept(new PossibleEntryPointExtractor(descriptors), possibleEntries);
				if(!possibleEntries.isEmpty())
					set.add(cfg);
			}

			return set;
		
	}
		
		private static class PossibleEntryPointExtractor implements GraphVisitor<CFG, Statement, Edge, Collection<Statement>> {

			final Set<CFGDescriptor> descriptors;
			
			public PossibleEntryPointExtractor(Set<CFGDescriptor> descriptors) {
				this.descriptors = descriptors;
			}
			@Override
			public boolean visit(Collection<Statement> tool, CFG graph) {
				return true;
			}

			@Override
			public boolean visit(Collection<Statement> tool, CFG graph, Statement node) {

				if (matchSignatureDescriptor(graph, node, tool))
					tool.add(node);

				return true;
			}

			private boolean matchSignatureDescriptor(CFG graph, Statement node, Collection<Statement> tool) {
				if(node instanceof Call)
					if(descriptors.stream().anyMatch( d -> d.getFullName().equals(((Call) node).getFullTargetName())
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
