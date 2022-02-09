package it.unive.golisa.analysis.taint.annotation;

import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.TaintChecker;
import java.util.HashSet;
import java.util.Set;

public class NonDeterminismAnnotationSet extends AnnotationSet {

	public NonDeterminismAnnotationSet() {

	}

	@Override
	public Set<? extends CodeAnnotation> getAnnotationsForCodeMembers() {
		Set<CodeAnnotation> set = new HashSet<>();

		// sources

		// Go time API

		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "time", "Now"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "time", "Since"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "time", "Until"));

		// Go random API

		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "math/rand", "ExpFloat64"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "math/rand", "Float32"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "math/rand", "Float64"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "math/rand", "Int"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "math/rand", "Int31"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "math/rand", "Int31n"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "math/rand", "Int63"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "math/rand", "Int63n"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "math/rand", "Intn"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "math/rand", "NormFloat64"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "math/rand", "Perm"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "math/rand", "Read"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "math/rand", "Shuffle"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "math/rand", "Uint32"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "math/rand", "Uint64"));

		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "crypto/rand", "Read"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "crypto/rand", "Int"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "crypto/rand", "Prime"));

		// Go os API

		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os/file", "Create"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os/file", "CreateTemp"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os/file", "NewFile"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os/file", "Open"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os/file", "OpenFile"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os", "Create"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os", "CreateTemp"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os", "NewFile"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os", "Open"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os", "OpenFile"));

		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os", "Executable"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os", "Exit"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os", "Getenv"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os", "IsNotExist"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os", "MkdirAll"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os", "ReadFile"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os", "RemoveAll"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os", "Setenv"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os", "Unsetenv"));

		// Go io API

		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os/ioutil", "ReadAll"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os/ioutil", "ReadDir"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os/ioutil", "ReadFile"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os/ioutil", "TempDir"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os/ioutil", "TempFile"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "os/ioutil", "WriteFile"));

		return set;
	}

	@Override
	public Set<? extends CodeAnnotation> getAnnotationsForConstructors() {
		Set<CodeAnnotation> set = new HashSet<>();

		// sources

		// Go random API

		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "Rand", "ExpFloat64"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "Rand", "Float32"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "Rand", "Float64"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "Rand", "Int"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "Rand", "Int31"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "Rand", "Int31n"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "Rand", "Int63"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "Rand", "Int63n"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "Rand", "Intn"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "Rand", "NormFloat64"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "Rand", "Perm"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "Rand", "Read"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "Rand", "Shuffle"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "Rand", "Uint32"));
		set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, "Rand", "Uint64"));

		// sinks

		// Hyperledger Fabric API
		set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "ChaincodeStub", "PutState", 1));
		set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "ChaincodeStub", "PutState", 2));
		set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "ChaincodeStubInterface", "PutState", 1));
		set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "ChaincodeStubInterface", "PutState", 2));
		set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "ChaincodeStub", "PutPrivateData", 1));
		set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "ChaincodeStub", "PutPrivateData", 2));
		set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "ChaincodeStubInterface", "PutPrivateData",
				1));
		set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "ChaincodeStubInterface", "PutPrivateData",
				2));
		set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "ChaincodeStub", "DelState", 1));
		set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "ChaincodeStubInterface", "DelState", 1));
		set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "ChaincodeStub", "DelPrivateData", 1));
		set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, "ChaincodeStubInterface", "DelPrivateData",
				1));

		// Tendermint Core API

		return set;
	}

	@Override
	public Set<? extends CodeAnnotation> getAnnotationsForGlobals() {
		return new HashSet<>();
	}

}
