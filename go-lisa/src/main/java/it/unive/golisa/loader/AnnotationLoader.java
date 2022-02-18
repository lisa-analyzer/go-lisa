package it.unive.golisa.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.unive.golisa.loader.annotation.AnnotationSet;
import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.MethodAnnotation;
import it.unive.golisa.loader.annotation.MethodParameterAnnotation;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.NativeCFG;

import org.apache.commons.lang3.tuple.Pair;

public class AnnotationLoader implements Loader {

	protected final List<AnnotationSet> annotationSets;
	
	protected final  Set<Pair<CodeAnnotation, CFGDescriptor>> appliedAnnotations;
	

	public AnnotationLoader() {
		annotationSets = new ArrayList<>();
		appliedAnnotations = new HashSet<>();
	}

	public AnnotationLoader(AnnotationSet... annotationSets) {
		this();
		this.annotationSets.addAll(Arrays.asList(annotationSets));
	}

	public void addAnnotationSet(AnnotationSet... annotationSet) {
		for(AnnotationSet as : annotationSet)
			this.annotationSets.add(as);
	}

	@Override
	public void load(Program program) {
		Collection<CodeMember> codeMembers = program.getAllCodeMembers();
		Collection<NativeCFG> constructs = program.getAllConstructs();
		Collection<Global> globals = program.getAllGlobals();

		for (CodeMember cm : codeMembers)
			for (AnnotationSet set : annotationSets)
				for (CodeAnnotation ca : set.getAnnotationsForCodeMembers())
					checkAndAddAnnotation(cm.getDescriptor(), ca);

		for (NativeCFG c : constructs) {
			for (AnnotationSet set : annotationSets)
				for (CodeAnnotation ca : set.getAnnotationsForConstructors())
					checkAndAddAnnotation(c.getDescriptor(), ca);
		}

		for (Global g : globals) {
			// TODO
		}
	}
	
	public Set<Pair<CodeAnnotation, CFGDescriptor>> getAppliedAnnotations(){
		return appliedAnnotations;
	}

	private void checkAndAddAnnotation(CFGDescriptor descriptor, CodeAnnotation ca) {
		if (ca instanceof MethodAnnotation) {
			MethodAnnotation ma = (MethodAnnotation) ca;
			if (descriptor.getUnit().getName().equals(ma.getUnit())
					&& descriptor.getName().equals(ma.getName())) {
				if (ca instanceof MethodParameterAnnotation) {
					MethodParameterAnnotation mpa = (MethodParameterAnnotation) ca;
					descriptor.getFormals()[mpa.getParam()].addAnnotation(mpa.getAnnotation());
				} else
					descriptor.addAnnotation(ma.getAnnotation());
				
				appliedAnnotations.add(Pair.of(ca, descriptor));
			}
		}
	}
}
