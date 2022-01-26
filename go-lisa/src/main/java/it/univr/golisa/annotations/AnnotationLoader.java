package it.univr.golisa.annotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import it.unive.lisa.program.Global;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.cfg.CFGDescriptor;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.NativeCFG;

public class AnnotationLoader {

	protected final List<AnnotationSet> annotationSets;

	public AnnotationLoader() {
		annotationSets = new ArrayList<>();
	}
	
	public AnnotationLoader(AnnotationSet ... annotationSets) {
		this.annotationSets = Arrays.asList(annotationSets);
	}
	
	public void addAnnotationSet(AnnotationSet annotationSet) {
		this.annotationSets.add(annotationSet);
	}

	public void load(Program program) {

		Collection<CodeMember> codeMembers = program.getAllCodeMembers();
		Collection<NativeCFG> constructs = program.getAllConstructs();
		Collection<Global> globals = program.getAllGlobals();
		
		for(CodeMember cm : codeMembers)
			for( AnnotationSet set : annotationSets)
				for(CodeAnnotation ca : set.getAnnotationsForCodeMembers())
					checkAndAddAnnotation(cm.getDescriptor(), ca);
		
		for(NativeCFG c : constructs) 
			for( AnnotationSet set : annotationSets)
				for(CodeAnnotation ca : set.getAnnotationsForConstructors())
					checkAndAddAnnotation(c.getDescriptor(), ca);
		
		for(Global g : globals) {
			//TODO
		}
	}

	private void checkAndAddAnnotation(CFGDescriptor descriptor, CodeAnnotation ca) {
		if(ca instanceof MethodAnnotation) {
			MethodAnnotation ma = (MethodAnnotation) ca;
			if(descriptor.getUnit().toString().equals(ma.getUnit())
					&& descriptor.getName().equals(ma.getName()))
				if(ca instanceof MethodParameterAnnotation) {
					MethodParameterAnnotation mpa = (MethodParameterAnnotation) ca;
					if(mpa.getParam() < descriptor.getFormals().length)
						descriptor.getFormals()[mpa.getParam()].addAnnotation(mpa.getAnnotation());
				}else
					descriptor.addAnnotation(ma.getAnnotation());
		}
	}
}
