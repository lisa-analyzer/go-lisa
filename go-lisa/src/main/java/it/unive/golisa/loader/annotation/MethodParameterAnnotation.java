package it.unive.golisa.loader.annotation;

import it.unive.lisa.program.annotations.Annotation;

/**
 * The class represents a method parameter annotation.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class MethodParameterAnnotation extends MethodAnnotation {

	/**
	 * The parameter position.
	 */
	protected final int param;

	/**
	 * Builds an instance of method parameter annotation.
	 * 
	 * @param annotation the annotation
	 * @param unit       the unit of method
	 * @param name       the method name
	 * @param param      the param position
	 */
	public MethodParameterAnnotation(Annotation annotation, String unit, String name, int param) {
		super(annotation, unit, name);
		this.param = param;
	}

	/**
	 * Yields the param position.
	 * 
	 * @return the param position
	 */
	public int getParam() {
		return param;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + param;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MethodParameterAnnotation other = (MethodParameterAnnotation) obj;
		if (param != other.param)
			return false;
		return true;
	}

}
