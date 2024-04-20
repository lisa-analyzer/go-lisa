package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.lisa.program.cfg.statement.call.Call.CallType;

/**
 * The class represents the set of annotations for the non-determinism analysis
 * related to Cosmos SDK.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class CosmosSDKNonDeterminismAnnotationSet extends NonDeterminismAnnotationSet {

	/**
	 * Builds an instance of an annotation set for non-determinism related to
	 * Cosmos SDK.
	 */
	public CosmosSDKNonDeterminismAnnotationSet() {
		super("cosmos-sdk");
	}

	static {

		Map<Pair<String, CallType>, Set<String>> map = new HashMap<>();

		map.put(Pair.of("errors", CallType.STATIC), Set.of("SuccessABCICode", "Register", "ABCIInfo", "Redact",
				"UndefinedCodespace", "ABCIError", "New", "Wrap",
				"Wrapf", "Recover", "WithType"));

		map.put(Pair.of("sdkerrors", CallType.STATIC), Set.of("SuccessABCICode", "Register", "ABCIInfo", "Redact",
				"UndefinedCodespace", "ABCIError", "New", "Wrap",
				"Wrapf", "Recover", "WithType"));

		SINK_CODE_MEMBER_ANNOTATIONS.put(Kind.METHOD, map);

		map = new HashMap<>();

		map.put(Pair.of("Error", CallType.INSTANCE), Set.of("SuccessABCICode", "Register", "ABCIInfo", "Redact",
				"UndefinedCodespace", "ABCIError", "New", "Wrap",
				"Wrapf", "Recover", "WithType"));

		SINK_CONSTRUCTORS_ANNOTATIONS.put(Kind.METHOD, map);

		Map<Pair<String, CallType>, Set<Pair<String, Integer>>> map2 = new HashMap<>();

		map2.put(Pair.of("Store", CallType.INSTANCE), Set.of(Pair.of("Set", 1), Pair.of("Set", 2), Pair.of("Delete", 1)));

		SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS.put(Kind.PARAM, map2);
	}
}
