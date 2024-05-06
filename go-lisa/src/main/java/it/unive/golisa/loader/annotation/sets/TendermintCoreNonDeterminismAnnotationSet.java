package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.lisa.program.cfg.statement.call.Call.CallType;

/**
 * The class represents the set of annotations for the non-determinism analysis
 * related to Tendermint Core.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class TendermintCoreNonDeterminismAnnotationSet extends NonDeterminismAnnotationSet {

	/**
	 * Builds an instance of an annotation set for non-determinism related to
	 * Tendermint Core.
	 */
	public TendermintCoreNonDeterminismAnnotationSet() {
		super("tendermint-core");
	}

	static {

		Map<Pair<String, CallType>, Set<String>> map = new HashMap<>();

		map = new HashMap<>();

		map.put(Pair.of("types", CallType.STATIC), Set.of("ResponseBeginBlockTx", "ResponseDeliverTx", "ResponseEndBlockTx", "ResponseCommitTx",
				"ResponseCheckTx"));

		SINK_CONSTRUCTORS_ANNOTATIONS.put(Kind.METHOD, map);

		Map<Pair<String, CallType>, Set<Pair<String, Integer>>> map2 = new HashMap<>();

		map2.put(Pair.of("ResponseBeginBlockTx", CallType.INSTANCE),
				Set.of(Pair.of("Marshal", 1), Pair.of("Merge", 1), Pair.of("XXX_Marshal", 1), Pair.of("XXX_Merge", 1)));
		map2.put(Pair.of("ResponseDeliverTx", CallType.INSTANCE),
				Set.of(Pair.of("Marshal", 1), Pair.of("Merge", 1), Pair.of("XXX_Marshal", 1), Pair.of("XXX_Merge", 1)));
		map2.put(Pair.of("ResponseEndBlockTx", CallType.INSTANCE),
				Set.of(Pair.of("Marshal", 1), Pair.of("Merge", 1), Pair.of("XXX_Marshal", 1), Pair.of("XXX_Merge", 1)));
		map2.put(Pair.of("ResponseCommitTx", CallType.INSTANCE),
				Set.of(Pair.of("Marshal", 1), Pair.of("Merge", 1), Pair.of("XXX_Marshal", 1), Pair.of("XXX_Merge", 1)));
		map2.put(Pair.of("ResponseCheckTx", CallType.INSTANCE),
				Set.of(Pair.of("Marshal", 1), Pair.of("Merge", 1), Pair.of("XXX_Marshal", 1), Pair.of("XXX_Merge", 1)));

		SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS.put(Kind.PARAM, map2);
	}
}