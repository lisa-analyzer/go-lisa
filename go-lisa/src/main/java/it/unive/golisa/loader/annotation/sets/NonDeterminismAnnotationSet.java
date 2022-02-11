package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.golisa.analysis.ni.IntegrityNIDomain;
import it.unive.golisa.analysis.taint.TaintDomain;
import it.unive.golisa.checker.IntegrityNIChecker;
import it.unive.golisa.checker.TaintChecker;
import it.unive.golisa.loader.annotation.AnnotationSet;
import it.unive.golisa.loader.annotation.CodeAnnotation;
import it.unive.golisa.loader.annotation.MethodAnnotation;
import it.unive.golisa.loader.annotation.MethodParameterAnnotation;

public class NonDeterminismAnnotationSet extends AnnotationSet {

	private enum Kind {
		METHOD, PARAM
	}

	private static final Map<Kind, Map<String, Set<String>>> CODE_MEMBER_ANNOTATIONS = new HashMap<>();
	private static final Map<Kind, Map<String, Set<String>>> CONSTRUCTORS_ANNOTATIONS = new HashMap<>();
	private static final Map<Kind,
			Map<String, Set<Pair<String, Integer>>>> CONSTRUCTOR_PARAMETER_ANNOTATIONS = new HashMap<>();

	static {
		HashMap<String, Set<String>> map = new HashMap<>();

		// Go time API
		map.put("time", Set.of("Now", "Since", "Until"));

		// Go random API
		map.put("math/rand", Set.of("ExpFloat64", "Float32", "Float64", "Int", "Int31", "Int31n", "Int63",
				"Int63n", "Intn", "NormFloat64", "Perm", "Read", "Shuffle", "Uint32", "Uint64"));
		map.put("crypto/rand", Set.of("Int", "Prime", "Read"));

		// Go os API
		map.put("os/file", Set.of("Create", "CreateTemp", "NewFile", "Open", "OpenFile"));
		map.put("os", Set.of("Create", "CreateTemp", "NewFile", "Open", "OpenFile", "Executable", "Exit",
				"Getenv", "IsNotExist", "MkdirAll", "ReadFile", "RemoveAll", "Setenv", "Unsetenv"));

		// Go io API
		map.put("os/ioutil", Set.of("ReadAll", "ReadDir", "ReadFile", "TempDir", "TempFile", "WriteFile"));

		CODE_MEMBER_ANNOTATIONS.put(Kind.METHOD, map);

		map = new HashMap<>();

		// Go random API
		map.put("Rand", Set.of("ExpFloat64", "Float32", "Float64", "Int", "Int31", "Int31n", "Int63", "Int63n", "Intn",
				"NormFloat64", "Perm", "Read", "Shuffle", "Uint32", "Uint64"));

		CONSTRUCTORS_ANNOTATIONS.put(Kind.METHOD, map);

		Map<String, Set<Pair<String, Integer>>> map2 = new HashMap<>();

		// Hyperledger Fabric API
		map2.put("ChaincodeStub", Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
				Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("DelPrivateData", 1)));

		map2.put("ChaincodeStubInterface",
				Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
						Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("DelPrivateData", 1)));

		// Tendermint Core API

		CONSTRUCTOR_PARAMETER_ANNOTATIONS.put(Kind.PARAM, map2);
	}

	@Override
	public Set<? extends CodeAnnotation> getAnnotationsForCodeMembers() {
		Set<CodeAnnotation> set = new HashSet<>();

		// sources
		for (Entry<Kind, Map<String, Set<String>>> entry : CODE_MEMBER_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<String, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, target.getKey(), mtd));
						set.add(new MethodAnnotation(IntegrityNIDomain.LOW_ANNOTATION, target.getKey(), mtd));
					});

		return set;
	}

	@Override
	public Set<? extends CodeAnnotation> getAnnotationsForConstructors() {
		Set<CodeAnnotation> set = new HashSet<>();

		// sources
		for (Entry<Kind, Map<String, Set<String>>> entry : CONSTRUCTORS_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.METHOD)
				for (Entry<String, Set<String>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(new MethodAnnotation(TaintDomain.TAINTED_ANNOTATION, target.getKey(), mtd));
						set.add(new MethodAnnotation(IntegrityNIDomain.LOW_ANNOTATION, target.getKey(), mtd));
					});

		// sinks
		for (Entry<Kind, Map<String, Set<Pair<String, Integer>>>> entry : CONSTRUCTOR_PARAMETER_ANNOTATIONS.entrySet())
			if (entry.getKey() == Kind.PARAM)
				for (Entry<String, Set<Pair<String, Integer>>> target : entry.getValue().entrySet())
					target.getValue().forEach(mtd -> {
						set.add(new MethodParameterAnnotation(TaintChecker.SINK_ANNOTATION, target.getKey(),
								mtd.getLeft(), mtd.getRight()));
						set.add(new MethodParameterAnnotation(IntegrityNIChecker.SINK_ANNOTATION, target.getKey(),
								mtd.getLeft(), mtd.getRight()));
					});

		return set;
	}

	@Override
	public Set<? extends CodeAnnotation> getAnnotationsForGlobals() {
		return new HashSet<>();
	}

}
