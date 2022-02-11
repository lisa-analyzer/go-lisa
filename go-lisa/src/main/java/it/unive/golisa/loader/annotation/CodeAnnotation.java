package it.unive.golisa.loader.annotation;

import it.unive.lisa.program.annotations.Annotation;

public class CodeAnnotation {

	public final Annotation annotation;

	public CodeAnnotation(Annotation annotation) {
		this.annotation = annotation;
	}

	public Annotation getAnnotation() {
		return annotation;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((annotation == null) ? 0 : annotation.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CodeAnnotation other = (CodeAnnotation) obj;
		if (annotation == null) {
			if (other.annotation != null)
				return false;
		} else if (!annotation.equals(other.annotation))
			return false;
		return true;
	}

}
