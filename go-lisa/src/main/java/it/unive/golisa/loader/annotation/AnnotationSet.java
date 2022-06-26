package it.unive.golisa.loader.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

public abstract class AnnotationSet {

	protected enum Kind {
		METHOD, PARAM
	}

	protected static final Map<Kind, Map<String, Set<String>>> CODE_MEMBER_ANNOTATIONS = new HashMap<>();
	protected static final Map<Kind, Map<String, Set<String>>> CONSTRUCTORS_ANNOTATIONS = new HashMap<>();
	protected static final Map<Kind,
			Map<String, Set<Pair<String, Integer>>>> CONSTRUCTOR_PARAMETER_ANNOTATIONS = new HashMap<>();

	public abstract Set<? extends CodeAnnotation> getAnnotationsForCodeMembers();

	public abstract Set<? extends CodeAnnotation> getAnnotationsForConstructors();

	public abstract Set<? extends CodeAnnotation> getAnnotationsForGlobals();
}
