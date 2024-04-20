package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.IntegrityNIChecker;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.FrameworkAnnotationSet;
import it.unive.golisa.loader.annotation.MethodAnnotation;
import it.unive.golisa.loader.annotation.MethodParameterAnnotation;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;

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
	protected static final Map<Kind, Map<Pair<String, CallType>, Set<String>>> SOURCE_CODE_MEMBER_ANNOTATIONS = new HashMap<>();

	/**
	 * The sink code member annotations.
	 */
	protected static final Map<Kind, Map<Pair<String, CallType>, Set<String>>> SINK_CODE_MEMBER_ANNOTATIONS = new HashMap<>();

	/**
	 * The source code constructor annotations.
	 */
	protected static final Map<Kind, Map<Pair<String, CallType>, Set<String>>> SOURCE_CONSTRUCTORS_ANNOTATIONS = new HashMap<>();

	/**
	 * The sink code constructor annotations.
	 */
	protected static final Map<Kind, Map<Pair<String, CallType>, Set<String>>> SINK_CONSTRUCTORS_ANNOTATIONS = new HashMap<>();

	/**
	 * The source code constructor parameter annotations.
	 */
	protected static final Map<Kind,
			Map<Pair<String, CallType>, Set<Pair<String, Integer>>>> SOURCE_CONSTRUCTOR_PARAMETER_ANNOTATIONS = new HashMap<>();

	/**
	 * The sink code constructor parameter annotations.
	 */
	protected static final Map<Kind,
			Map<Pair<String, CallType>, Set<Pair<String, Integer>>>> SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS = new HashMap<>();

	@Override
	public Set<Pair<CallType,? extends CodeAnnotation>> getAnnotationsForCodeMembers() {
		Set<Pair<CallType,? extends CodeAnnotation>> set = new HashSet<>();

		// sources
		for (Entry<Kind, Map<Pair<String, CallType>, Set<String>>> entry : SOURCE_CODE_MEMBER_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<Pair<String, CallType>, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(Pair.of(target.getKey().getRight(),new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, target.getKey().getLeft(), mtd)));
						set.add(Pair.of(target.getKey().getRight(),new MethodAnnotation(IntegrityNIDomain.LOW_ANNOTATION, target.getKey().getLeft(), mtd)));
					});

		// sink
		for (Entry<Kind, Map<Pair<String, CallType>, Set<String>>> entry : SINK_CODE_MEMBER_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<Pair<String, CallType>, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(Pair.of(target.getKey().getRight(),new MethodAnnotation(TaintChecker.SINK_ANNOTATION, target.getKey().getLeft(), mtd)));
						set.add(Pair.of(target.getKey().getRight(),new MethodAnnotation(IntegrityNIChecker.SINK_ANNOTATION, target.getKey().getLeft(), mtd)));
					});

		return set;
	}

	@Override
	public Set<Pair<CallType,? extends CodeAnnotation>> getAnnotationsForConstructors() {
		Set<Pair<CallType,? extends CodeAnnotation>> set = new HashSet<>();

		// sources
		for (Entry<Kind, Map<Pair<String, CallType>, Set<String>>> entry : SOURCE_CONSTRUCTORS_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<Pair<String, CallType>, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(Pair.of(target.getKey().getRight(),new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, target.getKey().getLeft(), mtd)));
						set.add(Pair.of(target.getKey().getRight(),new MethodAnnotation(IntegrityNIDomain.LOW_ANNOTATION, target.getKey().getLeft(), mtd)));
					});

		// sinks
		for (Entry<Kind, Map<Pair<String, CallType>, Set<String>>> entry : SINK_CONSTRUCTORS_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<Pair<String, CallType>, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(Pair.of(target.getKey().getRight(),new MethodAnnotation(TaintChecker.SINK_ANNOTATION, target.getKey().getLeft(), mtd)));
						set.add(Pair.of(target.getKey().getRight(),new MethodAnnotation(IntegrityNIChecker.SINK_ANNOTATION, target.getKey().getLeft(), mtd)));
					});

		for (Entry<Kind, Map<Pair<String, CallType>, Set<Pair<String, Integer>>>> entry : SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS
				.entrySet())
			if (entry.getKey() == Kind.PARAM)
				for (Entry<Pair<String, CallType>, Set<Pair<String, Integer>>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(Pair.of(target.getKey().getRight(), new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, target.getKey().getLeft(),
								mtd.getLeft(), mtd.getRight())));
						set.add(Pair.of(target.getKey().getRight(),new MethodParameterAnnotation(IntegrityNIChecker.SINK_ANNOTATION, target.getKey().getLeft(),
								mtd.getLeft(), mtd.getRight())));
					});

		return set;
	}

	@Override
	public Set<Pair<CallType,? extends CodeAnnotation>> getAnnotationsForGlobals() {
		return new HashSet<>();
	}

	/**
	 * Yields the annotation set of sources.
	 * 
	 * @return the annotation set of sources
	 */
	public Set<Pair<CallType,? extends CodeAnnotation>>  getAnnotationForSources() {
		Set<Pair<CallType,? extends CodeAnnotation>>  set = new HashSet<>();

		for (Entry<Kind, Map<Pair<String, CallType>, Set<String>>> entry : SOURCE_CODE_MEMBER_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<Pair<String, CallType>, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(Pair.of(target.getKey().getRight(),new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, target.getKey().getLeft(), mtd)));
						set.add(Pair.of(target.getKey().getRight(),new MethodAnnotation(IntegrityNIDomain.LOW_ANNOTATION, target.getKey().getLeft(), mtd)));
					});

		for (Entry<Kind, Map<Pair<String, CallType>, Set<String>>> entry : SOURCE_CONSTRUCTORS_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<Pair<String, CallType>, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(Pair.of(target.getKey().getRight(),new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, target.getKey().getLeft(), mtd)));
						set.add(Pair.of(target.getKey().getRight(),new MethodAnnotation(IntegrityNIDomain.LOW_ANNOTATION, target.getKey().getLeft(), mtd)));
					});

		return set;
	}

	/**
	 * Yields the annotation set of destinations (sinks).
	 * 
	 * @return the annotation set of destinations (sinks).
	 */
	public Set<Pair<CallType,? extends CodeAnnotation>>  getAnnotationForDestinations() {
		Set<Pair<CallType,? extends CodeAnnotation>>  set = new HashSet<>();

		for (Entry<Kind, Map<Pair<String, CallType>, Set<Pair<String, Integer>>>> entry : SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS
				.entrySet())
			if (entry.getKey() == Kind.PARAM)
				for (Entry<Pair<String, CallType>, Set<Pair<String, Integer>>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(Pair.of(target.getKey().getRight(), new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, target.getKey().getLeft(),
								mtd.getLeft(), mtd.getRight())));
						set.add(Pair.of(target.getKey().getRight(),new MethodParameterAnnotation(IntegrityNIChecker.SINK_ANNOTATION, target.getKey().getLeft(),
								mtd.getLeft(), mtd.getRight())));
					});
						
					


		return set;
	}
}
