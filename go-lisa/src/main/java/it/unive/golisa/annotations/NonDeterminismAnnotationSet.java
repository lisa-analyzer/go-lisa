package it.unive.golisa.annotations;

import java.util.HashSet;
import java.util.Set;

import it.unive.golisa.analysis.Taint;
import it.unive.golisa.checker.TaintChecker;

public class NonDeterminismAnnotationSet extends AnnotationSet {

	
	public NonDeterminismAnnotationSet() {

	}

	@Override
	public Set<? extends CodeAnnotation> getAnnotationsForCodeMembers() {
		Set<CodeAnnotation> set = new HashSet<>();
		
		//sources
		
			//Go time API
		
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "time", "Now"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "time", "Since"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "time", "Until"));
			
			//Go random API
			
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "math/rand", "ExpFloat64"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "math/rand", "Float32"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "math/rand", "Float64"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "math/rand", "Int"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "math/rand", "Int31"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "math/rand", "Int31n"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "math/rand", "Int63"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "math/rand", "Int63n"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "math/rand", "Intn"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "math/rand", "NormFloat64"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "math/rand", "Perm"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "math/rand", "Read"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "math/rand", "Shuffle"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "math/rand", "Uint32"));
			set.add(new MethodAnnotation(Taint.TAINTED_ANNOTATION, "math/rand", "Uint64"));
			
		//sinks
			
			//Hyperledger Fabric API
			
			set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "shim", "PutState", 0));
			set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "shim", "PutState", 1));
			
			set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "shim", "PutPrivateData", 0));
			set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "shim", "PutPrivateData", 1));
			
			set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "shim", "DelState", 0));
			
			set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "shim", "DelPrivateData", 0));
			
			
		return set;
	}

	@Override
	public Set<? extends CodeAnnotation> getAnnotationsForConstructors() {
		return new HashSet<>();
	}

	@Override
	public Set<? extends CodeAnnotation> getAnnotationsForGlobals() {
		return new HashSet<>();
	}
	
}
