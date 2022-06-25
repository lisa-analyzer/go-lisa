package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

public class CosmosSDKNonDeterminismAnnotationSet extends NonDeterminismAnnotationSet {

	public CosmosSDKNonDeterminismAnnotationSet() {
		super("cosmos-sdk");
	}

	static {

		Map<String, Set<String>> map = new HashMap<>();

		map.put("errors", Set.of("SuccessABCICode", "Register", "ABCIInfo", "Redact",
				"UndefinedCodespace", "ABCIError", "New", "Wrap",
				"Wrapf", "Recover", "WithType"));

		map.put("sdkerrors", Set.of("SuccessABCICode", "Register", "ABCIInfo", "Redact",
				"UndefinedCodespace", "ABCIError", "New", "Wrap",
				"Wrapf", "Recover", "WithType"));

		SINK_CODE_MEMBER_ANNOTATIONS.put(Kind.METHOD, map);

		map = new HashMap<>();

		map.put("Error", Set.of("SuccessABCICode", "Register", "ABCIInfo", "Redact",
				"UndefinedCodespace", "ABCIError", "New", "Wrap",
				"Wrapf", "Recover", "WithType"));

		SINK_CONSTRUCTORS_ANNOTATIONS.put(Kind.METHOD, map);

		Map<String, Set<Pair<String, Integer>>> map2 = new HashMap<>();

		map2.put("Store", Set.of(Pair.of("Set", 1), Pair.of("Set", 2), Pair.of("Delete", 1)));

		SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS.put(Kind.PARAM, map2);
	}
}
