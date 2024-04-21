package it.unive.golisa.loader.annotation.sets;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.lisa.program.cfg.statement.call.Call.CallType;

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
		
		Map<Pair<String, CallType>, Set<String>> map1 = new HashMap<>();

		map1.put(Pair.of("ChaincodeStub", CallType.INSTANCE), Set.of("GetArgs", "GetStringArgs", "GetFunctionAndParameters", "GetArgsSlice", "GetTransient"));

		map1.put(Pair.of("ChaincodeStubInterface", CallType.INSTANCE), Set.of("GetArgs", "GetStringArgs", "GetFunctionAndParameters", "GetArgsSlice", "GetTransient"));
		
		SOURCE_ANNOTATIONS_PHASE_1.put(Kind.METHOD, map1);
		
		Map<Pair<String, CallType>, Set<Pair<String, Integer>>> map2 = new HashMap<>();
		
		map2.put(Pair.of("ChaincodeStub", CallType.INSTANCE), Set.of(Pair.of("InvokeChaincode", 1)));

		map2.put(Pair.of("ChaincodeStubInterface", CallType.INSTANCE), Set.of(Pair.of("InvokeChaincode", 1)));
		
		SINK_ANNOTATIONS_PHASE_1.put(Kind.PARAM, map2);
		
		Map<Pair<String, CallType>, Set<Pair<String, Integer>>> map3 = new HashMap<>();

		map3.put(Pair.of("ChaincodeStub", CallType.INSTANCE), Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
				Pair.of("SetStateValidationParameter", 1), Pair.of("SetStateValidationParameter", 2), Pair.of("SetStateValidationParameter", 3),
				Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("PutPrivateData", 3),
				Pair.of("DelPrivateData", 1), Pair.of("DelPrivateData", 2),
				Pair.of("PurgePrivateData", 1), Pair.of("PurgePrivateData", 2),
				Pair.of("SetPrivateDataValidationParameter", 1), Pair.of("SetPrivateDataValidationParameter", 2), Pair.of("SetPrivateDataValidationParameter", 3)));

		map3.put(Pair.of("ChaincodeStubInterface", CallType.INSTANCE), Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
				Pair.of("SetStateValidationParameter", 1), Pair.of("SetStateValidationParameter", 2), Pair.of("SetStateValidationParameter", 3),
				Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("PutPrivateData", 3),
				Pair.of("DelPrivateData", 1), Pair.of("DelPrivateData", 2),
				Pair.of("PurgePrivateData", 1), Pair.of("PurgePrivateData", 2),
				Pair.of("SetPrivateDataValidationParameter", 1), Pair.of("SetPrivateDataValidationParameter", 2), Pair.of("SetPrivateDataValidationParameter", 3)));

		map3.put(Pair.of("shim", CallType.STATIC), Set.of(Pair.of("Success", 0), Pair.of("Error", 0)));
		
		SINK_ANNOTATIONS_PHASE_2.put(Kind.PARAM, map3);
		
	}
}
