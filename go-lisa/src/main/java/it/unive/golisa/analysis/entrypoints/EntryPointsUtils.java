package it.unive.golisa.analysis.entrypoints;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

import it.unive.golisa.cfg.utils.CFGUtils;
import it.unive.golisa.loader.annotation.AnnotationSet;
import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.MethodAnnotation;
import it.unive.golisa.loader.annotation.MethodParameterAnnotation;
import it.unive.golisa.loader.annotation.sets.TaintAnnotationSet;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;
import it.unive.lisa.program.cfg.edge.Edge;
import it.unive.lisa.program.cfg.statement.Statement;
import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;
import it.unive.lisa.util.datastructures.graph.GraphVisitor;

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
	 * @param appliedAnnotations the set of annotations to check
	 * @param annotationSets     the set of annotation to find (sink/source)
	 * 
	 * @return {@code true} if exist at least a source and a sink annotation,
	 *             otherwise {@code false}.
	 */
	public static boolean containsPossibleEntryPointsForAnalysis(Program program, 
			Set<Pair<CodeAnnotation, CodeMemberDescriptor>> appliedAnnotations,
			TaintAnnotationSet... annotationSets) {

		boolean atLeastOneSource = false;
		boolean atLeastOneDestination = false;
		for (TaintAnnotationSet as : annotationSets) {
			Set<Pair<CallType,? extends CodeAnnotation>> sources = as.getAnnotationForSources();
			Set<Pair<CallType,? extends CodeAnnotation>> destinations = as.getAnnotationForDestinations();

			if (!atLeastOneSource && appliedAnnotations.stream().anyMatch(e -> sources.contains(
					Pair.of(e.getRight().isInstance() ? CallType.INSTANCE : CallType.STATIC,e.getLeft()))))
				atLeastOneSource = true;
			if (!atLeastOneDestination && appliedAnnotations.stream().anyMatch(e -> destinations.contains(
					Pair.of(e.getRight().isInstance() ? CallType.INSTANCE : CallType.STATIC,e.getLeft()))))
				atLeastOneDestination = true;

			if (atLeastOneSource && atLeastOneDestination)
				break;
		}

		if(atLeastOneSource && atLeastOneDestination)
			return true;
		
		for (TaintAnnotationSet as : annotationSets) {
			Set<Pair<CallType,? extends CodeAnnotation>> sources = as.getAnnotationForSources();
			Set<Pair<CallType,? extends CodeAnnotation>> destinations = as.getAnnotationForDestinations();
			
			for (CFG cfg : program.getAllCFGs()) {

				if (!atLeastOneSource) {
					atLeastOneSource = CFGUtils.anyMatchInCFGNodes(cfg, e ->  e instanceof Call && sources.stream().anyMatch(src -> src.getLeft().equals(((Call)e).getCallType()) &&
							( src.getRight()instanceof MethodAnnotation && ((MethodAnnotation) src.getRight()).getName().equals(((Call)e).getTargetName()))
							|| 	(src.getRight()instanceof MethodParameterAnnotation && ((MethodParameterAnnotation) src.getRight()).getName().equals(((Call)e).getTargetName()))
							));
				}
				
				if (!atLeastOneDestination)
					atLeastOneDestination = CFGUtils.anyMatchInCFGNodes(cfg, e ->  e instanceof Call && destinations.stream().anyMatch(dst -> dst.getLeft().equals(((Call)e).getCallType()) &&
							( dst.getRight()instanceof MethodAnnotation && ((MethodAnnotation) dst.getRight()).getName().equals(((Call)e).getTargetName()))
							|| 	(dst.getRight()instanceof MethodParameterAnnotation && ((MethodParameterAnnotation) dst.getRight()).getName().equals(((Call)e).getTargetName()))
							));
	
				if (atLeastOneSource && atLeastOneDestination)
					break;
			}
			
			if (atLeastOneSource && atLeastOneDestination)
				break;
		}
		
		return atLeastOneSource && atLeastOneDestination;
		
	}


	/**
	 * Yields the descriptor set of possible entry points for the analysis.
	 * @param program 
	 * 
	 * @param appliedAnnotations the applied annotations
	 * @param annotationSets     the set of annotation related to the analysis
	 *                               of non-determinism
	 * 
	 * @return the set of descriptors
	 */
	private static Set<CodeMemberDescriptor> getDescriptorOfPossibleEntryPointsForAnalysis(
			Program program, Set<Triple<CallType, ? extends CodeAnnotation, CodeMemberDescriptor>> appliedAnnotations,
			AnnotationSet... annotationSets) {

		Set<CodeMemberDescriptor> descriptors = new HashSet<>();
		
		for (AnnotationSet as : annotationSets) {
			if (as instanceof TaintAnnotationSet) {
				Set<Pair<CallType,? extends CodeAnnotation>> sources = ((TaintAnnotationSet) as).getAnnotationForSources();
				appliedAnnotations.stream()
						.forEach(e -> {
							if (sources.contains(Pair.of(e.getRight().isInstance() ? CallType.INSTANCE : CallType.STATIC,e.getLeft())))
								descriptors.add(e.getRight());
						});
			}
		}
			
		for (AnnotationSet as : annotationSets) {
			if (as instanceof TaintAnnotationSet) {
				Set<Pair<CallType,? extends CodeAnnotation>> sources = ((TaintAnnotationSet) as).getAnnotationForSources();
				for (CFG cfg : program.getAllCFGs()) {

						boolean atLeastOneSource = CFGUtils.anyMatchInCFGNodes(cfg, e ->  e instanceof Call && sources.stream().anyMatch(src -> src.getLeft().equals(((Call)e).getCallType()) &&
								( src.getRight()instanceof MethodAnnotation && ((MethodAnnotation) src.getRight()).getName().equals(((Call)e).getTargetName()))
								|| 	(src.getRight()instanceof MethodParameterAnnotation && ((MethodParameterAnnotation) src.getRight()).getName().equals(((Call)e).getTargetName()))
								));
					
						if(atLeastOneSource)
							descriptors.add(cfg.getDescriptor());
				}
			}
		}
		
		return descriptors;

		
	}

	/**
	 * Compute the entry points from the possible entry points for the analysis.
	 * 
	 * @param program            the program the applied annotations
	 * @param appliedAnnotations the set of applied annotations
	 * @param annotationSets     the set of annotations related to the analysis
	 *                               of non-determinism
	 * 
	 * @return the set of entry points
	 */
	public static Set<CFG> computeEntryPointSetFromPossibleEntryPointsForAnalysis(Program program,
			Set<Triple<CallType, ? extends CodeAnnotation, CodeMemberDescriptor>> appliedAnnotations, Set<Call> sourcesRaw,
			AnnotationSet... annotationSets) {

		Set<CFG> set = new HashSet<>();

		Set<CodeMemberDescriptor> descriptors = getDescriptorOfPossibleEntryPointsForAnalysis(program, appliedAnnotations,
				annotationSets);

		for (CFG cfg : program.getAllCFGs()) {
			LinkedList<Statement> possibleEntries = new LinkedList<>();
			cfg.accept(new PossibleEntryPointExtractor(descriptors), possibleEntries);
			if (!possibleEntries.isEmpty() || descriptors.contains(cfg.getDescriptor()) )
				set.add(cfg);
		}

		for (Unit unit : program.getUnits())
			for (CodeMember cfg : unit.getCodeMembers()) {
				if (cfg instanceof CFG) {
					LinkedList<Statement> possibleEntries = new LinkedList<>();
					((CFG) cfg).accept(new PossibleEntryPointExtractor(descriptors), possibleEntries);
					if (!possibleEntries.isEmpty() || descriptors.contains(cfg.getDescriptor()))
						set.add((CFG) cfg);
				}
			}
		
		if(sourcesRaw != null)
			for(Call call : sourcesRaw)
				set.add(call.getCFG()); 
		
		return set;

	}

	/**
	 * The class represents the extractor of possible entry points.
	 */
	private static class PossibleEntryPointExtractor
			implements GraphVisitor<CFG, Statement, Edge, Collection<Statement>> {

		final Set<CodeMemberDescriptor> descriptors;

		/**
		 * Builds an instance of the extractor of possible entry points
		 * 
		 * @param descriptors the descriptors
		 */
		public PossibleEntryPointExtractor(Set<CodeMemberDescriptor> descriptors) {
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
		 * with the descriptors.
		 * 
		 * @param node the statement
		 * @param tool the tool
		 * 
		 * @return {@code true} when the signature match, otherwise
		 *             {@code false}
		 */
		private boolean matchSignatureDescriptor(Statement node, Collection<Statement> tool) {
			
			if (node instanceof Call) {
				Call c = (Call) node;
				if(c.getCallType() == CallType.STATIC) {
					if (descriptors.stream().anyMatch(d -> d.getFullName().equals(((Call) node).getFullTargetName())
							&& d.getFormals().length == ((Call) node).getParameters().length))
						return true;
				} else if(c.getCallType() == CallType.INSTANCE) {
					if (descriptors.stream().anyMatch(d -> 
					d.getName().equals(((Call) node).getTargetName())
							&& d.getFormals().length == ((Call) node).getParameters().length))
						return true;
				} if (c.getCallType() == CallType.UNKNOWN) {
					if(descriptors.stream().anyMatch(d -> d.getFullName().equals(((Call) node).getFullTargetName())
							&& d.getFormals().length == ((Call) node).getParameters().length)
							|| descriptors.stream().anyMatch(d -> d.getName().equals(((Call) node).getTargetName())
									&& d.getFormals().length == ((Call) node).getParameters().length)) {
						return true;
					}
				}
			
			}
			return false;
		}

		@Override
		public boolean visit(Collection<Statement> tool, CFG graph, Edge edge) {
			return true;
		}
	}

}