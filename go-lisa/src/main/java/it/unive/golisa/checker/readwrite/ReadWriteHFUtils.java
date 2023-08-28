package it.unive.golisa.checker.readwrite;

import java.util.List;

import it.unive.lisa.program.cfg.statement.call.UnresolvedCall;

public class ReadWriteHFUtils {

	protected enum TypeInstruction {
		READ,
		WRITE,
	}
	
	protected enum KeyType {
		SINGLE,
		RANGE,
		COMPOSITE
	}

	private static final List<ReadWriteInfo> signatures = List.of(
			new ReadWriteInfo(TypeInstruction.READ , "GetState",KeyType.SINGLE, new int[]{0}),
			new ReadWriteInfo(TypeInstruction.READ , "GetStateValidationParameter", KeyType.SINGLE, new int[]{0}),
			new ReadWriteInfo(TypeInstruction.READ , "GetStateByRange", KeyType.RANGE, new int[]{0,1}),
			new ReadWriteInfo(TypeInstruction.READ , "GetStateByRangeWithPagination", KeyType.RANGE, new int[]{0,1}),
			new ReadWriteInfo(TypeInstruction.READ , "GetStateByPartial", KeyType.COMPOSITE, new int[]{1}),
			new ReadWriteInfo(TypeInstruction.READ , "GetHistoryForKey", KeyType.SINGLE, new int[]{0}),
			new ReadWriteInfo(TypeInstruction.READ , "GetPrivateData", KeyType.SINGLE, new int[]{1}),
			new ReadWriteInfo(TypeInstruction.READ , "GetPrivateDataValidationParameter", KeyType.SINGLE, new int[]{1}),
			new ReadWriteInfo(TypeInstruction.READ , "GetPrivateDataByRange", KeyType.RANGE, new int[]{1,2}),
			new ReadWriteInfo(TypeInstruction.READ , "GetPrivateDataHash", KeyType.SINGLE, new int[]{1}),
			new ReadWriteInfo(TypeInstruction.READ , "GetPrivateDataByPartialCompositeKey", KeyType.COMPOSITE, new int[]{2}),
			new ReadWriteInfo(TypeInstruction.WRITE, "PutState", KeyType.SINGLE, new int[]{0}),
			new ReadWriteInfo(TypeInstruction.WRITE, "DelState", KeyType.SINGLE, new int[]{0}),
			new ReadWriteInfo(TypeInstruction.WRITE, "SetStateValidationParameter", KeyType.SINGLE, new int[]{1}),
			new ReadWriteInfo(TypeInstruction.WRITE, "PutPrivateData", KeyType.SINGLE, new int[]{1}),
			new ReadWriteInfo(TypeInstruction.WRITE, "DelPrivateData", KeyType.SINGLE, new int[]{1}),
			new ReadWriteInfo(TypeInstruction.WRITE, "PurgePrivateData", KeyType.SINGLE, new int[]{1}),
			new ReadWriteInfo(TypeInstruction.WRITE, "SetPrivateDataValidationParameter", KeyType.SINGLE, new int[]{1}));
	
	public static boolean isReadOrWriteCall(UnresolvedCall call) {
		return signatures.stream().anyMatch(e -> e.getSignature().equals(call.getTargetName()));
	}
	public static boolean isWriteCall(UnresolvedCall call) {
		return signatures.stream().anyMatch(e -> e.getSignature().equals(call.getTargetName()) && e.getInstructionType().equals(TypeInstruction.WRITE));
	}
	public static boolean isReadCall(UnresolvedCall call) {
		return signatures.stream().anyMatch(e -> e.getSignature().equals(call.getTargetName())&& e.getInstructionType().equals(TypeInstruction.READ));
	}
	
	public static ReadWriteInfo getReadWriteInfo(UnresolvedCall call) {
		for(ReadWriteInfo e : signatures) {
			if(e.getSignature().equals(call.getTargetName()))
				return e;
		}
		return null;
	}	

}
