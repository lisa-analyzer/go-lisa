package it.unive.golisa.loader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.loader.annotation.AnnotationSet;
import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.MethodAnnotation;
import it.unive.golisa.loader.annotation.MethodParameterAnnotation;
import it.unive.lisa.program.CompilationUnit;
import it.unive.lisa.program.Global;
import it.unive.lisa.program.Program;
import it.unive.lisa.program.Unit;
import it.unive.lisa.program.cfg.CodeMember;
import it.unive.lisa.program.cfg.CodeMemberDescriptor;

/**
 * The class represents an annotation loader for programs.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class AnnotationLoader implements Loader {

	/**
	 * The sets of annotations used by the loader.
	 */
	protected final List<AnnotationSet> annotationSets;

	/**
	 * The set of annotations applied after the calling of load method by the
	 * loader.
	 */
	protected final Set<Pair<CodeAnnotation, CodeMemberDescriptor>> appliedAnnotations;
	
	
	protected final Set<Pair<CodeAnnotation, CodeMemberDescriptor>> specificCodeMemberAnnotations;
	

	/**
	 * Builds an instance of annotation loader.
	 */
	public AnnotationLoader() {
		annotationSets = new ArrayList<>();
		appliedAnnotations = new HashSet<>();
		specificCodeMemberAnnotations = new HashSet<>();
	}

	/**
	 * Builds an instance of annotation loader.
	 * 
	 * @param annotationSets annotations sets to load
	 */
	public AnnotationLoader(AnnotationSet... annotationSets) {
		this();
		this.annotationSets.addAll(Arrays.asList(annotationSets));
	}

	/**
	 * The method add the annotation sets to load in a program.
	 * 
	 * @param annotationSets the entry annotation sets to add in the loader
	 */
	public void addAnnotationSet(AnnotationSet... annotationSets) {
		for (AnnotationSet as : annotationSets)
			this.annotationSets.add(as);
	}

	@Override
	public void load(Program program) {
		Collection<CodeMember> codeMembers = program.getCodeMembers();

		for (CodeMember cm : codeMembers) {
			for (AnnotationSet set : annotationSets)
				for (CodeAnnotation ca : set.getAnnotationsForCodeMembers())
					checkAndAddAnnotation(cm.getDescriptor(), ca);
			checkAndAddSpecificAnnotations(cm);
		}
		for (Unit unit : program.getUnits()) {
			for (CodeMember cm : unit.getCodeMembers()) {
				for (AnnotationSet set : annotationSets) {
					for (CodeAnnotation ca : set.getAnnotationsForCodeMembers())
						checkAndAddAnnotation(cm.getDescriptor(), ca);
					for (CodeAnnotation ca : set.getAnnotationsForConstructors())
						checkAndAddAnnotation(cm.getDescriptor(), ca);
				}
				checkAndAddSpecificAnnotations(cm);
			}

			if (unit instanceof CompilationUnit) {
				CompilationUnit cUnit = (CompilationUnit) unit;

				for (CodeMember cm : cUnit.getInstanceCodeMembers(true)) {					
					for (AnnotationSet set : annotationSets) {
						for (CodeAnnotation ca : set.getAnnotationsForCodeMembers())
							checkAndAddAnnotation(cm.getDescriptor(), ca);
						for (CodeAnnotation ca : set.getAnnotationsForConstructors())
							checkAndAddAnnotation(cm.getDescriptor(), ca);
					}
					checkAndAddSpecificAnnotations(cm);
				}
			}
		}

		Collection<Global> globals = program.getGlobals();
		for (Global g : globals) {
			// TODO
		}
	}
	
	
	private void checkAndAddSpecificAnnotations(CodeMember cm) {
		for(Pair<CodeAnnotation, CodeMemberDescriptor> e : specificCodeMemberAnnotations) {
			if(e.getRight().equals(cm.getDescriptor())) {
				cm.getDescriptor().addAnnotation(e.getLeft().getAnnotation());
				appliedAnnotations.add(Pair.of(e.getLeft(), cm.getDescriptor()));
			}
		}
		
	}
	
	private void checkAndRemoveSpecificAnnotations(CodeMember cm) {
		for(Pair<CodeAnnotation, CodeMemberDescriptor> e : specificCodeMemberAnnotations) {
			if(e.getRight().equals(cm.getDescriptor())) {
				cm.getDescriptor().getAnnotations().getAnnotations().remove(e.getLeft().getAnnotation());
				appliedAnnotations.remove(Pair.of(e.getLeft(), cm.getDescriptor()));
			}
		}
		
	}

	/**
	 * Yields the annotations applied after a load.
	 * 
	 * @return the annotations applied after a load
	 */
	public Set<Pair<CodeAnnotation, CodeMemberDescriptor>> getAppliedAnnotations() {
		return appliedAnnotations;
	}

	/**
	 * The method checks checks and adds an annotation to a descriptor.
	 * 
	 * @param descriptor the descriptor
	 * @param ca         the code annotation
	 */
	private void checkAndAddAnnotation(CodeMemberDescriptor descriptor, CodeAnnotation ca) {
		if (ca instanceof MethodAnnotation) {
			MethodAnnotation ma = (MethodAnnotation) ca;
			if (matchUnit(ma, descriptor) && descriptor.getName().equals(ma.getName())) {
				if (ca instanceof MethodParameterAnnotation) {
					MethodParameterAnnotation mpa = (MethodParameterAnnotation) ca;
					descriptor.getFormals()[mpa.getParam()].addAnnotation(mpa.getAnnotation());
				} else
					descriptor.addAnnotation(ma.getAnnotation());

				appliedAnnotations.add(Pair.of(ca, descriptor));
			}
		}
	}
	
	private void checkAndRemoveAnnotation(CodeMemberDescriptor descriptor, CodeAnnotation ca) {
		if (ca instanceof MethodAnnotation) {
			MethodAnnotation ma = (MethodAnnotation) ca;
			if (matchUnit(ma, descriptor) && descriptor.getName().equals(ma.getName())) {
				if (ca instanceof MethodParameterAnnotation) {
					MethodParameterAnnotation mpa = (MethodParameterAnnotation) ca;
					descriptor.getFormals()[mpa.getParam()].getAnnotations().getAnnotations().remove(mpa.getAnnotation());
				} else
					descriptor.getAnnotations().getAnnotations().remove(ma.getAnnotation());

				appliedAnnotations.remove(Pair.of(ca, descriptor));
			}
		}
	}

	private boolean matchUnit(MethodAnnotation ma, CodeMemberDescriptor descriptor) {
		return (ma.getUnit().equals(descriptor.getUnit().getName()))
				|| (ma.getUnit().contains("/") && ma.getUnit().endsWith(descriptor.getUnit().getName()));

	}

	public void addSpecificCodeMemberAnnotations(Set<Pair<CodeAnnotation, CodeMemberDescriptor>> specificCodeMemberAnnotations) {
		this.specificCodeMemberAnnotations.addAll(specificCodeMemberAnnotations);
		
	}
	
	@Override
	public void unload(Program program) {
		Collection<CodeMember> codeMembers = program.getCodeMembers();

		for (CodeMember cm : codeMembers) {
			for (AnnotationSet set : annotationSets)
				for (CodeAnnotation ca : set.getAnnotationsForCodeMembers())
					checkAndRemoveAnnotation(cm.getDescriptor(), ca);
			checkAndRemoveSpecificAnnotations(cm);
		}
		for (Unit unit : program.getUnits()) {
			for (CodeMember cm : unit.getCodeMembers()) {
				for (AnnotationSet set : annotationSets) {
					for (CodeAnnotation ca : set.getAnnotationsForCodeMembers())
						checkAndRemoveAnnotation(cm.getDescriptor(), ca);
					for (CodeAnnotation ca : set.getAnnotationsForConstructors())
						checkAndRemoveAnnotation(cm.getDescriptor(), ca);
				}
				checkAndRemoveSpecificAnnotations(cm);
			}

			if (unit instanceof CompilationUnit) {
				CompilationUnit cUnit = (CompilationUnit) unit;

				for (CodeMember cm : cUnit.getInstanceCodeMembers(true)) {					
					for (AnnotationSet set : annotationSets) {
						for (CodeAnnotation ca : set.getAnnotationsForCodeMembers())
							checkAndRemoveAnnotation(cm.getDescriptor(), ca);
						for (CodeAnnotation ca : set.getAnnotationsForConstructors())
							checkAndRemoveAnnotation(cm.getDescriptor(), ca);
					}
					checkAndRemoveSpecificAnnotations(cm);
				}
			}
		}

		Collection<Global> globals = program.getGlobals();
		for (Global g : globals) {
			// TODO
		}
		
	}
}
