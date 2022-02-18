package it.unive.golisa.loader.annotation;


public abstract class FrameworkAnnotationSet extends AnnotationSet {
	
	private final String framework;
	
	public FrameworkAnnotationSet(String framework){
		this.framework=framework;
	}

	public String getFramework() {
		return framework;
	}
	
}
