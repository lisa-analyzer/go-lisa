package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

public class HyperledgerFabricNonDeterminismAnnotationSet extends NonDeterminismAnnotationSet{


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

		SINK_CONSTRUCTOR_PARAMETER_ANNOTATIONS.put(Kind.PARAM, map2);
	}
}
