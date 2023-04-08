package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.analysis.taint.TaintDomainForPhase1;
import it.unive.golisa.checker.IntegrityNIChecker;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.checker.UCCICheckerPhase1;
import it.unive.golisa.checker.UCCICheckerPhase2;
import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.FrameworkAnnotationSet;
import it.unive.golisa.loader.annotation.MethodAnnotation;
import it.unive.golisa.loader.annotation.MethodParameterAnnotation;

/**
 * The class represents the set of annotations for the UCCI analysis.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class UCCIAnnotationSet extends FrameworkAnnotationSet {

	/**
	 * Builds an instance of annotation set for non-determinism related to a
	 * framework.
	 * 
	 * @param framework the target framework
	 */
	public UCCIAnnotationSet(String framework) {
		super(framework);
	}

	/**
	 * The source code member annotations.
	 */
	protected static final Map<Kind, Map<String, Set<String>>> SOURCE_ANNOTATIONS_PHASE_1 = new HashMap<>();

	/**
	 * The sink code member annotations.
	 */
	/**
	 * The sink code constructor parameter annotations.
	 */
	protected static final Map<Kind,
			Map<String, Set<Pair<String, Integer>>>> SINK_ANNOTATIONS_PHASE_1 = new HashMap<>();

	/**
	 * The source code constructor annotations.
	 */
	/**
	 * The sink code constructor parameter annotations.
	 */
	protected static final Map<Kind,
			Map<String, Set<Pair<String, Integer>>>> SINK_ANNOTATIONS_PHASE_2 = new HashMap<>();


	@Override
	public Set<? extends CodeAnnotation> getAnnotationsForCodeMembers() {
		Set<CodeAnnotation> set = new HashSet<>();

		// sources
		for (Entry<Kind, Map<String, Set<String>>> entry : SOURCE_ANNOTATIONS_PHASE_1.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<String, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(new MethodAnnotation(TaintDomainForPhase1.TAINTED_ANNOTATION_PHASE1, target.getKey(), mtd));
					});

		// sinks
		for (Entry<Kind, Map<String, Set<Pair<String, Integer>>>> entry : SINK_ANNOTATIONS_PHASE_1
				.entrySet())
			if (entry.getKey() == Kind.PARAM)
				for (Entry<String, Set<Pair<String, Integer>>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(new MethodParameterAnnotation(UCCICheckerPhase1.SINK_ANNOTATION_PHASE1, target.getKey(),
								mtd.getLeft(), mtd.getRight()));
					});


		for (Entry<Kind, Map<String, Set<Pair<String, Integer>>>> entry : SINK_ANNOTATIONS_PHASE_2
				.entrySet())
			if (entry.getKey() == Kind.PARAM)
				for (Entry<String, Set<Pair<String, Integer>>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(new MethodParameterAnnotation(UCCICheckerPhase2.SINK_ANNOTATION_PHASE2, target.getKey(),
								mtd.getLeft(), mtd.getRight()));
					});

		
		return set;
	}

	/**
	 * Yields the annotation set of sources.
	 * 
	 * @return the annotation set of sources
	 */
	public Set<? extends CodeAnnotation> getAnnotationForSources() {
		Set<CodeAnnotation> set = new HashSet<>();

		for (Entry<Kind, Map<String, Set<String>>> entry : SOURCE_ANNOTATIONS_PHASE_1.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<String, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(new MethodAnnotation(TaintDomainForPhase1.TAINTED_ANNOTATION_PHASE1, target.getKey(), mtd));
					});
		return set;
	}

	/**
	 * Yields the annotation set of destinations (sinks).
	 * 
	 * @return the annotation set of destinations (sinks).
	 */
	public Set<? extends CodeAnnotation> getAnnotationForDestinations() {
		Set<CodeAnnotation> set = new HashSet<>();

		for (Entry<Kind, Map<String, Set<Pair<String, Integer>>>> entry : SINK_ANNOTATIONS_PHASE_1
				.entrySet())
			if (entry.getKey() == Kind.PARAM)
				for (Entry<String, Set<Pair<String, Integer>>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, target.getKey(),
								mtd.getLeft(), mtd.getRight()));
						set.add(new MethodParameterAnnotation(IntegrityNIChecker.SINK_ANNOTATION, target.getKey(),
								mtd.getLeft(), mtd.getRight()));
					});

		for (Entry<Kind, Map<String, Set<Pair<String, Integer>>>> entry : SINK_ANNOTATIONS_PHASE_2
				.entrySet())
			if (entry.getKey() == Kind.PARAM)
				for (Entry<String, Set<Pair<String, Integer>>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, target.getKey(),
								mtd.getLeft(), mtd.getRight()));
						set.add(new MethodParameterAnnotation(IntegrityNIChecker.SINK_ANNOTATION, target.getKey(),
								mtd.getLeft(), mtd.getRight()));
					});
		
		return set;
	}

	@Override
	public Set<? extends CodeAnnotation> getAnnotationsForConstructors() {
		return Set.of();
	}

	@Override
	public Set<? extends CodeAnnotation> getAnnotationsForGlobals() {
		return Set.of();
	}
}
