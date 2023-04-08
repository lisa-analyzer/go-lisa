package it.unive.golisa.loader.annotation;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

/**
 * The class represents a set of annotations.
 *
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public abstract class AnnotationSet {

	/**
	 * The kind of annotation.
	 */
	public enum Kind {
		/**
		 * Method annotation.
		 */
		METHOD,

		/**
		 * Parameter annotation.
		 */
		PARAM
	}

	/**
	 * The annotations for the code member.
	 */
	protected static final Map<Kind, Map<String, Set<String>>> CODE_MEMBER_ANNOTATIONS = new HashMap<>();

	/**
	 * The annotations for the constructors.
	 */
	protected static final Map<Kind, Map<String, Set<String>>> CONSTRUCTORS_ANNOTATIONS = new HashMap<>();

	/**
	 * The annotations of constructor parameter.
	 */
	protected static final Map<Kind,
			Map<String, Set<Pair<String, Integer>>>> CONSTRUCTOR_PARAMETER_ANNOTATIONS = new HashMap<>();

	/**
	 * Yields the annotations for code members.
	 * 
	 * @return the annotations for code members
	 */
	public abstract Set<? extends CodeAnnotation> getAnnotationsForCodeMembers();

	/**
	 * Yields the annotations for constructors.
	 * 
	 * @return the annotations for constructors
	 */
	public abstract Set<? extends CodeAnnotation> getAnnotationsForConstructors();

	/**
	 * Yields the annotations for globals.
	 * 
	 * @return the annotations for globals
	 */
	public abstract Set<? extends CodeAnnotation> getAnnotationsForGlobals();
}
