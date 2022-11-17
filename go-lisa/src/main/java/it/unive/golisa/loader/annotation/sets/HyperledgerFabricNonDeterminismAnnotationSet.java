package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.lang3.tuple.Pair;

/**
 * The class represents the set of annotations for the non-determinism analysis
 * related to Hyperledger Fabric.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class HyperledgerFabricNonDeterminismAnnotationSet extends NonDeterminismAnnotationSet {

	/**
	 * Builds an instance of an annotation set for non-determinism related to
	 * Hyperledger Fabric.
	 */
	public HyperledgerFabricNonDeterminismAnnotationSet() {
		super("hyperledger-fabric");
	}

	static {
		Map<String, Set<Pair<String, Integer>>> map2 = new HashMap<>();

		// Hyperledger Fabric API
		map2.put("ChaincodeStub", Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
				Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("DelPrivateData", 1)));

		map2.put("ChaincodeStubInterface",
				Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
						Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("DelPrivateData", 1)));

		map2.put("shim", Set.of(Pair.of("Success", 0)));
		map2.put("shim", Set.of(Pair.of("Error", 0)));

		SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS.put(Kind.PARAM, map2);
	}
}
