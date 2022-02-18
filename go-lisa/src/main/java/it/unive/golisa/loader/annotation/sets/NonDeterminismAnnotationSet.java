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

public class NonDeterminismAnnotationSet extends FrameworkAnnotationSet{

	public NonDeterminismAnnotationSet(String framework) {
		super(framework);
	}
	
	protected static final Map<Kind, Map<String, Set<String>>> SOURCE_CODE_MEMBER_ANNOTATIONS = new HashMap<>();
	protected static final Map<Kind, Map<String, Set<String>>> SINK_CODE_MEMBER_ANNOTATIONS = new HashMap<>();

	protected static final Map<Kind, Map<String, Set<String>>> SOURCE_CONSTRUCTORS_ANNOTATIONS = new HashMap<>();
	protected static final Map<Kind, Map<String, Set<String>>> SINK_CONSTRUCTORS_ANNOTATIONS = new HashMap<>();

	protected static final Map<Kind, Map<String,  Set<Pair<String, Integer>>>> SOURCE_CONSTRUCTOR_PARAMETER_ANNOTATIONS = new HashMap<>();
	protected static final Map<Kind, Map<String,  Set<Pair<String, Integer>>>> SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS = new HashMap<>();

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
		
		for (Entry<Kind, Map<String, Set<Pair<String, Integer>>>> entry : SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS.entrySet())
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
	
	public Set<? extends CodeAnnotation> getAnnotationForDestinations() {
		Set<CodeAnnotation> set = new HashSet<>();

		for (Entry<Kind, Map<String, Set<Pair<String, Integer>>>> entry : SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS.entrySet())
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
