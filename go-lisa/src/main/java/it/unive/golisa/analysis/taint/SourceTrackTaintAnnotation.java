package it.unive.golisa.analysis.taint;

import java.util.HashSet;
import java.util.Set;

import it.unive.lisa.program.annotations.Annotation;

import it.unive.lisa.program.cfg.statement.Statement;

public class SourceTrackTaintAnnotation extends Annotation {
	
	private final Set<Statement> sources;
	
	public SourceTrackTaintAnnotation(String annotationName) {
		super(annotationName);
		sources = new HashSet<>();
	}

	public Set<Statement> getSources() {
		return new HashSet<>(sources);
	}

	public void addSources(Set<Statement> sources) {
		this.sources.addAll(sources);
	}
	
	public void addSource(Statement source) {
		this.sources.add(source);
	}
	
	public void cleanSources() {
		sources.clear();
	}

}
