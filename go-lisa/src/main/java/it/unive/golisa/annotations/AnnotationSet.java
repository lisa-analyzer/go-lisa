package it.unive.golisa.annotations;

import java.util.Set;

public abstract class AnnotationSet{

	public abstract Set<? extends CodeAnnotation> getAnnotationsForCodeMembers();
	public abstract Set<? extends CodeAnnotation> getAnnotationsForConstructors();
	public abstract Set<? extends CodeAnnotation> getAnnotationsForGlobals();
}
