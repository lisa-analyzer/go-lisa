package it.unive.golisa.analysis.utilities;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import it.unive.lisa.program.cfg.statement.call.Call;
import it.unive.lisa.program.cfg.statement.call.Call.CallType;

public class PrivacySignatures {
	
	public static final Map<Pair<String, CallType>, Set<String>> publicInputs;
	public static final Map<Pair<String, CallType>, Set<String>> privateInputs;
	public static final Map<Pair<String, CallType>, Set<String>> publicReadStates;
	public static final Map<Pair<String, CallType>, Set<String>> publicWriteStatesAndResponses;
	public static final Map<Pair<String, CallType>, Set<String>>privateReadStates;
	public static final Map<Pair<String, CallType>, Set<String>> privateWriteStates;
	
	public static final Map<Pair<String, CallType>, Set<Pair<String, Integer>>> publicWriteStatesAndResponsesWithCriticalParams;
	public static final Map<Pair<String, CallType>, Set<Pair<String, Integer>>> privateWriteStatesWithCriticalParams;
	
	static {
		publicInputs = new HashMap<>();
		publicInputs.put(Pair.of("ChaincodeStub", CallType.INSTANCE), Set.of("GetArgs", "GetStringArgs", "GetFunctionAndParameters", "GetArgsSlice"));
		publicInputs.put(Pair.of("ChaincodeStubInterface", CallType.INSTANCE), Set.of("GetArgs", "GetStringArgs", "GetFunctionAndParameters", "GetArgsSlice"));
		
		privateInputs = new HashMap<>();
		privateInputs.put(Pair.of("ChaincodeStub", CallType.INSTANCE), Set.of("GetTransient"));
		privateInputs.put(Pair.of("ChaincodeStubInterface", CallType.INSTANCE), Set.of("GetTransient"));
		
		publicReadStates = new HashMap<>();
		publicReadStates.put(Pair.of("ChaincodeStub", CallType.INSTANCE), Set.of("GetState", "GetStateValidationParameter", "GetStateByRange", "GetStateByRangeWithPagination", "GetStateByPartialCompositeKey", "GetHistoryForKey"));
		publicReadStates.put(Pair.of("ChaincodeStubInterface", CallType.INSTANCE), Set.of("GetState", "GetStateValidationParameter", "GetStateByRange", "GetStateByRangeWithPagination", "GetStateByPartialCompositeKey", "GetHistoryForKey"));
		
		publicWriteStatesAndResponses = new HashMap<>();
		publicWriteStatesAndResponses.put(Pair.of("ChaincodeStub", CallType.INSTANCE), Set.of("PutState", "DelState", "SetStateValidationParameter"));
		publicWriteStatesAndResponses.put(Pair.of("ChaincodeStubInterface", CallType.INSTANCE), Set.of("PutState", "DelState", "SetStateValidationParameter"));
		publicWriteStatesAndResponses.put(Pair.of("shim", CallType.STATIC), Set.of("Success", "Error"));
		
		privateReadStates = new HashMap<>();
		privateReadStates.put(Pair.of("ChaincodeStub", CallType.INSTANCE), Set.of("GetPrivateData", "GetPrivateDataValidationParameter", "GetPrivateDataByRange", "GetPrivateDataQueryResult", "GetPrivateDataHash", "GetPrivateDataByPartialCompositeKey"));
		privateReadStates.put(Pair.of("ChaincodeStubInterface", CallType.INSTANCE), Set.of("GetPrivateData", "GetPrivateDataValidationParameter", "GetPrivateDataByRange", "GetPrivateDataQueryResult", "GetPrivateDataHash", "GetPrivateDataByPartialCompositeKey"));
	
		
		privateWriteStates = new HashMap<>();
		privateWriteStates.put(Pair.of("ChaincodeStub", CallType.INSTANCE), Set.of("PutPrivateData", "DelPrivateData", "PurgePrivateData",  "SetPrivateDataValidationParameter"));
		privateWriteStates.put(Pair.of("ChaincodeStubInterface", CallType.INSTANCE), Set.of("PutPrivateData", "DelPrivateData", "PurgePrivateData",  "SetPrivateDataValidationParameter"));
		
		
		publicWriteStatesAndResponsesWithCriticalParams = new HashMap<>();
		publicWriteStatesAndResponsesWithCriticalParams.put(Pair.of("ChaincodeStub", CallType.INSTANCE), Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
				Pair.of("SetStateValidationParameter", 1), Pair.of("SetStateValidationParameter", 2), Pair.of("SetStateValidationParameter", 3)));
		publicWriteStatesAndResponsesWithCriticalParams.put(Pair.of("ChaincodeStubInterface", CallType.INSTANCE), Set.of(Pair.of("PutState", 1), Pair.of("PutState", 2), Pair.of("DelState", 1),
				Pair.of("SetStateValidationParameter", 1), Pair.of("SetStateValidationParameter", 2), Pair.of("SetStateValidationParameter", 3)));
		publicWriteStatesAndResponsesWithCriticalParams.put(Pair.of("shim", CallType.STATIC), Set.of(Pair.of("Success", 0), Pair.of("Error", 0)));

		
		privateWriteStatesWithCriticalParams = new HashMap<>();
		
		privateWriteStatesWithCriticalParams.put(Pair.of("ChaincodeStub", CallType.INSTANCE), Set.of(Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("PutPrivateData", 3),
				Pair.of("DelPrivateData", 1), Pair.of("DelPrivateData", 2),
				Pair.of("PurgePrivateData", 1), Pair.of("PurgePrivateData", 2),
				Pair.of("SetPrivateDataValidationParameter", 1), Pair.of("SetPrivateDataValidationParameter", 2), Pair.of("SetPrivateDataValidationParameter", 3)));

		privateWriteStatesWithCriticalParams.put(Pair.of("ChaincodeStubInterface", CallType.INSTANCE), Set.of(Pair.of("PutPrivateData", 1), Pair.of("PutPrivateData", 2), Pair.of("PutPrivateData", 3),
				Pair.of("DelPrivateData", 1), Pair.of("DelPrivateData", 2),
				Pair.of("PurgePrivateData", 1), Pair.of("PurgePrivateData", 2),
				Pair.of("SetPrivateDataValidationParameter", 1), Pair.of("SetPrivateDataValidationParameter", 2), Pair.of("SetPrivateDataValidationParameter", 3)));
		}



	public static boolean isReadPrivateState(Call call) {
		if(privateReadStates.entrySet().stream().anyMatch(e -> 
			e.getKey().getRight().equals(call.getCallType()) &&
			e.getValue().stream().anyMatch( e2 -> e2.equals(call.getTargetName()))))
				return true;
		return false;
	}
	
	public static boolean isWritePrivateState(Call call) {
		if(privateWriteStates.entrySet().stream().anyMatch(e -> 
			e.getKey().getRight().equals(call.getCallType()) &&
			e.getValue().stream().anyMatch( e2 -> e2.equals(call.getTargetName()))))
			return true;

		return false;
	}
	
}
