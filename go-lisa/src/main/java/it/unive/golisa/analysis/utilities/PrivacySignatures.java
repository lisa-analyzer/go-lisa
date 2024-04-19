package it.unive.golisa.analysis.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

public class PrivacySignatures {
	
	public static final Map<String, Set<String>> publicInputs;
	public static final Map<String, Set<String>> privateInputs;
	public static final Map<String, Set<String>> publicReadStates;
	public static final Map<String, Set<String>> publicWriteStatesAndResponses;
	public static final Map<String, Set<String>> privateReadStates;
	public static final Map<String, Set<String>> privateWriteStates;
	
	public static final Map<String, Set<Pair<String, Integer>>> publicWriteStatesAndResponsesWithCriticalParams;
	public static final Map<String, Set<Pair<String, Integer>>> privateWriteStatesWithCriticalParams;
	
	static {
		publicInputs = new HashMap<>();
		publicInputs.put("ChaincodeStub", Set.of("GetArgs", "GetStringArgs", "GetFunctionAndParameters", "GetArgsSlice"));
		publicInputs.put("ChaincodeStubInterface", Set.of("GetArgs", "GetStringArgs", "GetFunctionAndParameters", "GetArgsSlice"));
		
		privateInputs = new HashMap<>();
		privateInputs.put("ChaincodeStub", Set.of("GetTransient"));
		privateInputs.put("ChaincodeStubInterface", Set.of("GetTransient"));
		
		publicReadStates = new HashMap<>();
		publicReadStates.put("ChaincodeStub", Set.of("GetState", "GetStateValidationParameter", "GetStateByRange", "GetStateByRangeWithPagination", "GetStateByPartialCompositeKey", "GetHistoryForKey"));
		publicReadStates.put("ChaincodeStubInterface", Set.of("GetState", "GetStateValidationParameter", "GetStateByRange", "GetStateByRangeWithPagination", "GetStateByPartialCompositeKey", "GetHistoryForKey"));
		
		publicWriteStatesAndResponses = new HashMap<>();
		publicWriteStatesAndResponses.put("ChaincodeStub", Set.of("PutState", "DelState", "SetStateValidationParameter"));
		publicWriteStatesAndResponses.put("ChaincodeStubInterface", Set.of("PutState", "DelState", "SetStateValidationParameter"));
		publicWriteStatesAndResponses.put("shim", Set.of("Success", "Error"));
		
		privateReadStates = new HashMap<>();
		privateReadStates.put("ChaincodeStub", Set.of("GetPrivateData", "GetPrivateDataValidationParameter", "GetPrivateDataByRange", "GetPrivateDataQueryResult", "GetPrivateDataHash", "GetPrivateDataByPartialCompositeKey"));
		privateReadStates.put("ChaincodeStubInterface", Set.of("GetPrivateData", "GetPrivateDataValidationParameter", "GetPrivateDataByRange", "GetPrivateDataQueryResult", "GetPrivateDataHash", "GetPrivateDataByPartialCompositeKey"));
	
		
		privateWriteStates = new HashMap<>();
		privateWriteStates.put("ChaincodeStub", Set.of("PutPrivateData", "DelPrivateData", "PurgePrivateData",  "SetPrivateDataValidationParameter"));
		privateWriteStates.put("ChaincodeStubInterface", Set.of("PutPrivateData", "DelPrivateData", "PurgePrivateData",  "SetPrivateDataValidationParameter"));
		
		
		publicWriteStatesAndResponsesWithCriticalParams = new HashMap<>();
		publicWriteStatesAndResponsesWithCriticalParams.put("ChaincodeStub", Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
				Pair.of("SetStateValidationParameter", 1), Pair.of("SetStateValidationParameter", 2), Pair.of("SetStateValidationParameter", 3)));
		publicWriteStatesAndResponsesWithCriticalParams.put("ChaincodeStubInterface", Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
				Pair.of("SetStateValidationParameter", 1), Pair.of("SetStateValidationParameter", 2), Pair.of("SetStateValidationParameter", 3)));
		publicWriteStatesAndResponsesWithCriticalParams.put("shim", Set.of(Pair.of("Success", 0), Pair.of("Error", 0)));

		
		privateWriteStatesWithCriticalParams = new HashMap<>();
		
		privateWriteStatesWithCriticalParams.put("ChaincodeStub", Set.of(Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("PutPrivateData", 3),
				Pair.of("DelPrivateData", 1), Pair.of("DelPrivateData", 2),
				Pair.of("PurgePrivateData", 1), Pair.of("PurgePrivateData", 2),
				Pair.of("SetPrivateDataValidationParameter", 1), Pair.of("SetPrivateDataValidationParameter", 2), Pair.of("SetPrivateDataValidationParameter", 3)));

		privateWriteStatesWithCriticalParams.put("ChaincodeStubInterface", Set.of(Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("PutPrivateData", 3),
				Pair.of("DelPrivateData", 1), Pair.of("DelPrivateData", 2),
				Pair.of("PurgePrivateData", 1), Pair.of("PurgePrivateData", 2),
				Pair.of("SetPrivateDataValidationParameter", 1), Pair.of("SetPrivateDataValidationParameter", 2), Pair.of("SetPrivateDataValidationParameter", 3)));
		}
	
}
