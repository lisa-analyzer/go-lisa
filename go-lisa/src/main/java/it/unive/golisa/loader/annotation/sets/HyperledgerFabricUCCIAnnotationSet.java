package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

/**
 * The class represents the set of annotations for the UCCI analysis
 * related to Hyperledger Fabric.
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class HyperledgerFabricUCCIAnnotationSet extends UCCIAnnotationSet {

	/**
	 * Builds an instance of an annotation set for non-determinism related to
	 * Hyperledger Fabric.
	 */
	public HyperledgerFabricUCCIAnnotationSet() {
		super("hyperledger-fabric");
	}

	static {
		
		Map<String, Set<String>> map1 = new HashMap<>();

		map1.put("ChaincodeStub", Set.of("GetArgs", "GetStringArgs", "GetFunctionAndParameters", "GetArgsSlice"));

		map1.put("ChaincodeStubInterface", Set.of("GetArgs", "GetStringArgs", "GetFunctionAndParameters", "GetArgsSlice"));
		
		SOURCE_ANNOTATIONS_PHASE_1.put(Kind.METHOD, map1);
		
		Map<String, Set<Pair<String, Integer>>> map2 = new HashMap<>();
		
		map2.put("ChaincodeStub", Set.of(Pair.of("InvokeChaincode", 1), Pair.of("InvokeChaincode", 2)));

		map2.put("ChaincodeStubInterface", Set.of(Pair.of("InvokeChaincode", 1), Pair.of("InvokeChaincode", 2)));
		
		SINK_ANNOTATIONS_PHASE_1.put(Kind.PARAM, map2);
		
		Map<String, Set<Pair<String, Integer>>> map3 = new HashMap<>();

		map3.put("ChaincodeStub", Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
				Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("DelPrivateData", 1)));

		map3.put("ChaincodeStubInterface",
				Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
						Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("DelPrivateData", 1)));

		map3.put("shim", Set.of(Pair.of("Success", 0), Pair.of("Error", 0)));
		
		SINK_ANNOTATIONS_PHASE_2.put(Kind.PARAM, map3);
		
	}
}
