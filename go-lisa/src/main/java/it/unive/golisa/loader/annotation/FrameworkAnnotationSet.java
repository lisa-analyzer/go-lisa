package it.unive.golisa.loader.annotation;

/**
 * The class represents the annotation set of a target framework
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public abstract class FrameworkAnnotationSet extends AnnotationSet {

	private final String framework;

	/**
	 * Builds an instance of the annotation set of a target framework
	 * 
	 * @param framework the target framework
	 */
	public FrameworkAnnotationSet(String framework) {
		this.framework = framework;
	}

	public String getFramework() {
		return framework;
	}

}
