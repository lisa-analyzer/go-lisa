package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class GoNonDeterminismAnnotationSet extends NonDeterminismAnnotationSet{


	public GoNonDeterminismAnnotationSet() {
		super("go-runtimes");
	}

	static {
		
		Map<String, Set<String>> map = new HashMap<>();

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

		SOURCE_CODE_MEMBER_ANNOTATIONS.put(Kind.METHOD, map);

		map = new HashMap<>();

		// Go random API
		map.put("Rand", Set.of("ExpFloat64", "Float32", "Float64", "Int", "Int31", "Int31n", "Int63", "Int63n", "Intn",
				"NormFloat64", "Perm", "Read", "Shuffle", "Uint32", "Uint64"));

		SOURCE_CONSTRUCTORS_ANNOTATIONS.put(Kind.METHOD, map);
	}
}
