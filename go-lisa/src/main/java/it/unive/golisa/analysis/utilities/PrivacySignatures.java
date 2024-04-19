package it.unive.golisa.analysis.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PrivacySignatures {
	
	public static final Map<String, Set<String>> publicInputs;
	public static final Map<String, Set<String>> privateInputs;
	public static final Map<String, Set<String>> publicReadStates;
	public static final Map<String, Set<String>> publicWriteStatesAndResponses;
	public static final Map<String, Set<String>> privateReadStates;
	public static final Map<String, Set<String>> privateWriteStates;
	
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
	}

}
