package it.unive.golisa.loader.annotation;

import it.unive.lisa.program.annotations.Annotation;

/**
* 
* 
*
*/
public class MethodParameterAnnotation extends MethodAnnotation {

	protected final int param;

	public MethodParameterAnnotation(Annotation annotation, String unit, String name, int param) {
		super(annotation, unit, name);
		this.param = param;
	}

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
