package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

public class TendermintCoreNonDeterminismAnnotationSet extends NonDeterminismAnnotationSet{


	public TendermintCoreNonDeterminismAnnotationSet() {
		super("tendermint-core");
	}

	static {

		Map<String, Set<Pair<String, Integer>>> map2 = new HashMap<>();

		// Tendermint Core API

		SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS.put(Kind.PARAM, map2);
	}
}
