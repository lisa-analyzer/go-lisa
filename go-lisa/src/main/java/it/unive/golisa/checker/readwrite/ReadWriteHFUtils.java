package it.unive.golisa.checker.readwrite;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;

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

	private static final List<Pair<TypeInstruction, Triple<String, KeyType, int[]>>> signatures = List.of(
			Pair.of(TypeInstruction.READ ,Triple.of("GetState",KeyType.SINGLE, new int[]{0})),
			Pair.of(TypeInstruction.READ ,Triple.of("GetStateValidationParameter", KeyType.SINGLE, new int[]{0})),
			Pair.of(TypeInstruction.READ ,Triple.of("GetStateByRange", KeyType.RANGE, new int[]{0,1})),
			Pair.of(TypeInstruction.READ ,Triple.of("GetStateByRangeWithPagination", KeyType.RANGE, new int[]{0,1})),
			Pair.of(TypeInstruction.READ ,Triple.of("GetStateByPartial", KeyType.COMPOSITE, new int[]{1})),
			Pair.of(TypeInstruction.READ ,Triple.of("GetHistoryForKey", KeyType.SINGLE, new int[]{0})),
			Pair.of(TypeInstruction.READ ,Triple.of("GetPrivateData", KeyType.SINGLE, new int[]{1})),
			Pair.of(TypeInstruction.READ ,Triple.of("GetPrivateDataValidationParameter", KeyType.SINGLE, new int[]{1})),
			Pair.of(TypeInstruction.READ ,Triple.of("GetPrivateDataByRange", KeyType.RANGE, new int[]{1,2})),
			Pair.of(TypeInstruction.READ ,Triple.of("GetPrivateDataHash", KeyType.SINGLE, new int[]{1})),
			Pair.of(TypeInstruction.READ ,Triple.of("GetPrivateDataByPartialCompositeKey", KeyType.COMPOSITE, new int[]{2})),
			Pair.of(TypeInstruction.WRITE, Triple.of("PutState", KeyType.SINGLE, new int[]{0})),
			Pair.of(TypeInstruction.WRITE, Triple.of("DelState", KeyType.SINGLE, new int[]{0})),
			Pair.of(TypeInstruction.WRITE, Triple.of("SetStateValidationParameter", KeyType.SINGLE, new int[]{1})),
			Pair.of(TypeInstruction.WRITE, Triple.of("PutPrivateData", KeyType.SINGLE, new int[]{1})),
			Pair.of(TypeInstruction.WRITE, Triple.of("DelPrivateData", KeyType.SINGLE, new int[]{1})),
			Pair.of(TypeInstruction.WRITE, Triple.of("PurgePrivateData", KeyType.SINGLE, new int[]{1})),
			Pair.of(TypeInstruction.WRITE, Triple.of("SetPrivateDataValidationParameter", KeyType.SINGLE, new int[]{1})));
	
	public static boolean isReadOrWriteCall(UnresolvedCall call) {
		return signatures.stream().anyMatch(e -> e.getRight().getLeft().equals(call.getTargetName()));
	}
	
	public static Pair<TypeInstruction, Triple<String, KeyType, int[]>> getReadWriteInfo(UnresolvedCall call) {
		for(Pair<TypeInstruction, Triple<String, KeyType, int[]>> e : signatures) {
			if(e.getRight().getLeft().equals(call.getTargetName()))
				return e;
		}
		return null;
	}	

}
