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
import it.unive.lisa.program.annotations.Annotation;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;

/**
 * The class represents the set of annotations for the taint analysis.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class TaintAnnotationSet extends FrameworkAnnotationSet {

	
	private final Set<Annotation> sourceAnnotations;
	private final Set<Annotation> sinkAnnotations; 
	private final Set<Annotation> sanitizerAnnotations;
	
	/**
	 * Builds an instance of annotation set for non-determinism related to a
	 * framework.
	 * 
	 * @param framework the target framework
	 */
	public TaintAnnotationSet(String framework, Set<Annotation> sourceAnnotations, Set<Annotation>  sinkAnnotations,  Set<Annotation> sanitizerAnnotations) {
		super(framework);
		this.sourceAnnotations=sourceAnnotations;
		this.sinkAnnotations=sinkAnnotations;
		this.sanitizerAnnotations=sanitizerAnnotations;
	}
	
	public TaintAnnotationSet(String framework) {
		super(framework);
		this.sourceAnnotations= Set.of(TaintDomain.TAINTED_ANNOTATION, IntegrityNIDomain.LOW_ANNOTATION);
		this.sinkAnnotations= Set.of(TaintChecker.SINK_ANNOTATION, IntegrityNIChecker.SINK_ANNOTATION);
		this.sanitizerAnnotations= Set.of(TaintDomain.CLEAN_ANNOTATION, IntegrityNIDomain.HIGH_ANNOTATION);
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
	 * The sanitizer code member annotations.
	 */
	protected static final Map<Kind, Map<Pair<String, CallType>, Set<String>>> SANITIZER_CODE_MEMBER_ANNOTATIONS = new HashMap<>();

	/**
	 * The source code constructor annotations.
	 */
	protected static final Map<Kind, Map<Pair<String, CallType>, Set<String>>> SOURCE_CONSTRUCTORS_ANNOTATIONS = new HashMap<>();

	/**
	 * The sink code constructor annotations.
	 */
	protected static final Map<Kind, Map<Pair<String, CallType>, Set<String>>> SINK_CONSTRUCTORS_ANNOTATIONS = new HashMap<>();

	/**
	 * The source code constructor annotations.
	 */
	protected static final Map<Kind, Map<Pair<String, CallType>, Set<String>>> SANITIZER_CONSTRUCTORS_ANNOTATIONS = new HashMap<>();
	
	/**
	 * The source code constructor parameter annotations.
	 */
	protected static final Map<Kind,
	Map<Pair<String, CallType>, Set<Pair<String, Integer>>>>  SOURCE_CONSTRUCTOR_PARAMETER_ANNOTATIONS = new HashMap<>();

	/**
	 * The sink code constructor parameter annotations.
	 */
	protected static final Map<Kind,
			Map<Pair<String, CallType>, Set<Pair<String, Integer>>>> SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS = new HashMap<>();
	
	/**
	 * The sanitizer code constructor parameter annotations.
	 */
	protected static final Map<Kind,
		Map<Pair<String, CallType>, Set<Pair<String, Integer>>>> SANITIZER_CONSTRUCTOR_PARAMETER_ANNOTATIONS = new HashMap<>();

	@Override
	public Set<Pair<CallType,? extends CodeAnnotation>> getAnnotationsForCodeMembers() {
		Set<Pair<CallType,? extends CodeAnnotation>> set = new HashSet<>();

		// sources
		for (Entry<Kind, Map<Pair<String, CallType>, Set<String>>> entry : SOURCE_CODE_MEMBER_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<Pair<String, CallType>, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						for(Annotation a : sourceAnnotations)
							set.add(Pair.of(target.getKey().getRight(), new MethodAnnotation(a, target.getKey().getKey(), mtd)));
					});
		
		// sources
		for (Entry<Kind, Map<Pair<String, CallType>, Set<String>>> entry : SANITIZER_CODE_MEMBER_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<Pair<String, CallType>, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						for(Annotation a : sanitizerAnnotations)
							set.add(Pair.of(target.getKey().getRight(), new MethodAnnotation(a, target.getKey().getKey(), mtd)));
					});

		// sink
		for (Entry<Kind, Map<Pair<String, CallType>, Set<String>>> entry : SINK_CODE_MEMBER_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<Pair<String, CallType>, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						for(Annotation a : sinkAnnotations)
							set.add(Pair.of(target.getKey().getRight(), new MethodAnnotation(a, target.getKey().getKey(), mtd)));
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
						for(Annotation a : sourceAnnotations)
							set.add(Pair.of(target.getKey().getRight(), new MethodAnnotation(a, target.getKey().getKey(), mtd)));
					});

		// sources
		for (Entry<Kind, Map<Pair<String, CallType>, Set<String>>> entry : SANITIZER_CONSTRUCTORS_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<Pair<String, CallType>, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						for(Annotation a : sanitizerAnnotations)
							set.add(Pair.of(target.getKey().getRight(), new MethodAnnotation(a, target.getKey().getKey(), mtd)));
					});
		
		// sinks
		for (Entry<Kind, Map<Pair<String, CallType>, Set<String>>> entry : SINK_CONSTRUCTORS_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<Pair<String, CallType>, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						for(Annotation a : sinkAnnotations)
							set.add(Pair.of(target.getKey().getRight(), new MethodAnnotation(a, target.getKey().getKey(), mtd)));
					});

		for (Entry<Kind, Map<Pair<String, CallType>, Set<Pair<String, Integer>>>> entry : SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS
				.entrySet())
			if (entry.getKey() == Kind.PARAM)
				for (Entry<Pair<String, CallType>, Set<Pair<String, Integer>>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						for(Annotation a : sinkAnnotations)
							set.add(Pair.of(target.getKey().getRight(), new MethodParameterAnnotation(a, target.getKey().getKey(), mtd.getLeft(), mtd.getRight())));
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
	public Set<Pair<CallType,? extends CodeAnnotation>> getAnnotationForSources() {
		Set<Pair<CallType,? extends CodeAnnotation>> set = new HashSet<>();

		for (Entry<Kind, Map<Pair<String, CallType>, Set<String>>> entry : SOURCE_CODE_MEMBER_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<Pair<String, CallType>, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						for(Annotation a : sourceAnnotations)
							set.add(Pair.of(target.getKey().getRight(), new MethodAnnotation(a, target.getKey().getKey(), mtd)));
					});

		for (Entry<Kind, Map<Pair<String, CallType>, Set<String>>> entry : SOURCE_CONSTRUCTORS_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<Pair<String, CallType>, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						for(Annotation a : sourceAnnotations)
							set.add(Pair.of(target.getKey().getRight(), new MethodAnnotation(a, target.getKey().getKey(), mtd)));
					});

		return set;
	}

	/**
	 * Yields the annotation set of destinations (sinks).
	 * 
	 * @return the annotation set of destinations (sinks).
	 */
	public  Set<Pair<CallType,? extends CodeAnnotation>> getAnnotationForDestinations() {
		 Set<Pair<CallType,? extends CodeAnnotation>> set = new HashSet<>();

		for (Entry<Kind, Map<Pair<String, CallType>, Set<Pair<String, Integer>>>> entry : SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS
				.entrySet())
			if (entry.getKey() == Kind.PARAM)
				for (Entry<Pair<String, CallType>, Set<Pair<String, Integer>>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						for(Annotation a : sinkAnnotations)
							set.add(Pair.of(target.getKey().getRight(), new MethodParameterAnnotation(a, target.getKey().getKey(), mtd.getLeft(), mtd.getRight())));
					});

		return set;
	}
}