package it.unive.golisa.loader.annotation.sets;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.IntegrityNIChecker;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.FrameworkAnnotationSet;
import it.unive.golisa.loader.annotation.MethodAnnotation;
import it.unive.golisa.loader.annotation.MethodParameterAnnotation;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

/**
 * The class represents the set of annotations for the non-determinism analysis.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class NonDeterminismAnnotationSet extends FrameworkAnnotationSet {

	/**
	 * Builds an instance of annotation set for non-determinism related to a
	 * framework.
	 * 
	 * @param framework the target framework
	 */
	public NonDeterminismAnnotationSet(String framework) {
		super(framework);
	}

	/**
	 * The source code member annotations.
	 */
	protected static final Map<Kind, Map<String, Set<String>>> SOURCE_CODE_MEMBER_ANNOTATIONS = new HashMap<>();

	/**
	 * The sink code member annotations.
	 */
	protected static final Map<Kind, Map<String, Set<String>>> SINK_CODE_MEMBER_ANNOTATIONS = new HashMap<>();

	/**
	 * The source code constructor annotations.
	 */
	protected static final Map<Kind, Map<String, Set<String>>> SOURCE_CONSTRUCTORS_ANNOTATIONS = new HashMap<>();

	/**
	 * The sink code constructor annotations.
	 */
	protected static final Map<Kind, Map<String, Set<String>>> SINK_CONSTRUCTORS_ANNOTATIONS = new HashMap<>();

	/**
	 * The source code constructor parameter annotations.
	 */
	protected static final Map<Kind,
			Map<String, Set<Pair<String, Integer>>>> SOURCE_CONSTRUCTOR_PARAMETER_ANNOTATIONS = new HashMap<>();

	/**
	 * The sink code constructor parameter annotations.
	 */
	protected static final Map<Kind,
			Map<String, Set<Pair<String, Integer>>>> SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS = new HashMap<>();

	@Override
	public Set<? extends CodeAnnotation> getAnnotationsForCodeMembers() {
		Set<CodeAnnotation> set = new HashSet<>();

		// sources
		for (Entry<Kind, Map<String, Set<String>>> entry : SOURCE_CODE_MEMBER_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<String, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, target.getKey(), mtd));
						set.add(new MethodAnnotation(IntegrityNIDomain.LOW_ANNOTATION, target.getKey(), mtd));
					});

		// sink
		for (Entry<Kind, Map<String, Set<String>>> entry : SINK_CODE_MEMBER_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<String, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(new MethodAnnotation(TaintChecker.SINK_ANNOTATION, target.getKey(), mtd));
						set.add(new MethodAnnotation(IntegrityNIChecker.SINK_ANNOTATION, target.getKey(), mtd));
					});

		return set;
	}

	@Override
	public Set<? extends CodeAnnotation> getAnnotationsForConstructors() {
		Set<CodeAnnotation> set = new HashSet<>();

		// sources
		for (Entry<Kind, Map<String, Set<String>>> entry : SOURCE_CONSTRUCTORS_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<String, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, target.getKey(), mtd));
						set.add(new MethodAnnotation(IntegrityNIDomain.LOW_ANNOTATION, target.getKey(), mtd));
					});

		// sinks
		for (Entry<Kind, Map<String, Set<String>>> entry : SINK_CONSTRUCTORS_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<String, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(new MethodAnnotation(TaintChecker.SINK_ANNOTATION, target.getKey(), mtd));
						set.add(new MethodAnnotation(IntegrityNIChecker.SINK_ANNOTATION, target.getKey(), mtd));
					});

		for (Entry<Kind, Map<String, Set<Pair<String, Integer>>>> entry : SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS
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
	public Set<? extends CodeAnnotation> getAnnotationsForGlobals() {
		return new HashSet<>();
	}

	/**
	 * Yields the annotation set of sources.
	 * 
	 * @return the annotation set of sources
	 */
	public Set<? extends CodeAnnotation> getAnnotationForSources() {
		Set<CodeAnnotation> set = new HashSet<>();

		for (Entry<Kind, Map<String, Set<String>>> entry : SOURCE_CODE_MEMBER_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<String, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, target.getKey(), mtd));
						set.add(new MethodAnnotation(IntegrityNIDomain.LOW_ANNOTATION, target.getKey(), mtd));
					});

		for (Entry<Kind, Map<String, Set<String>>> entry : SOURCE_CONSTRUCTORS_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<String, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, target.getKey(), mtd));
						set.add(new MethodAnnotation(IntegrityNIDomain.LOW_ANNOTATION, target.getKey(), mtd));
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

		for (Entry<Kind, Map<String, Set<Pair<String, Integer>>>> entry : SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS
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
}
