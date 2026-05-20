package it.unive.golisa.checker.hf.readwrite;

import it.unive.lisa.program.cfg.statement.call.Call;
import java.util.List;

/**
 * Utility class fore read-write analysis.
 */
public class ReadWriteHFUtils {

	/**
	 * The type of instruction.
	 */
	protected enum TypeInstruction {
		/**
		 * Read state operation.
		 */
		READ,

		/**
		 * Write state operation.
		 */
		WRITE,
	}

	/**
	 * The key type of the instruction.
	 */
	protected enum KeyType {

		/**
		 * Instruction with a single key.
		 */
		SINGLE,
		/**
		 * Instruction with a range of keys.
		 */
		RANGE,
		/**
		 * Instruction with a composite key.
		 */
		COMPOSITE
	}

	private static final List<ReadWriteInfo> signatures = List.of(
			new ReadWriteInfo(TypeInstruction.READ, "GetState", KeyType.SINGLE, new int[] { 0 }),
			new ReadWriteInfo(TypeInstruction.READ, "GetStateValidationParameter", KeyType.SINGLE, new int[] { 0 }),
			new ReadWriteInfo(TypeInstruction.READ, "GetStateByRange", KeyType.RANGE, new int[] { 0, 1 }),
			new ReadWriteInfo(TypeInstruction.READ, "GetStateByRangeWithPagination", KeyType.RANGE, new int[] { 0, 1 }),
			new ReadWriteInfo(TypeInstruction.READ, "GetStateByPartial", KeyType.COMPOSITE, new int[] { 1 }),
			new ReadWriteInfo(TypeInstruction.READ, "GetHistoryForKey", KeyType.SINGLE, new int[] { 0 }),
			new ReadWriteInfo(TypeInstruction.READ, "GetPrivateData", KeyType.SINGLE, new int[] { 1 }, 0),
			new ReadWriteInfo(TypeInstruction.READ, "GetPrivateDataValidationParameter", KeyType.SINGLE,
					new int[] { 1 }, 0),
			new ReadWriteInfo(TypeInstruction.READ, "GetPrivateDataByRange", KeyType.RANGE, new int[] { 1, 2 }, 0),
			new ReadWriteInfo(TypeInstruction.READ, "GetPrivateDataHash", KeyType.SINGLE, new int[] { 1 }, 0),
			new ReadWriteInfo(TypeInstruction.READ, "GetPrivateDataByPartialCompositeKey", KeyType.COMPOSITE,
					new int[] { 1 }, 0),
			new ReadWriteInfo(TypeInstruction.WRITE, "PutState", KeyType.SINGLE, new int[] { 0 }),
			new ReadWriteInfo(TypeInstruction.WRITE, "DelState", KeyType.SINGLE, new int[] { 0 }),
			new ReadWriteInfo(TypeInstruction.WRITE, "SetStateValidationParameter", KeyType.SINGLE, new int[] { 0 }),
			new ReadWriteInfo(TypeInstruction.WRITE, "PutPrivateData", KeyType.SINGLE, new int[] { 1 }, 0),
			new ReadWriteInfo(TypeInstruction.WRITE, "DelPrivateData", KeyType.SINGLE, new int[] { 1 }, 0),
			new ReadWriteInfo(TypeInstruction.WRITE, "PurgePrivateData", KeyType.SINGLE, new int[] { 1 }, 0),
			new ReadWriteInfo(TypeInstruction.WRITE, "SetPrivateDataValidationParameter", KeyType.SINGLE,
					new int[] { 1 }, 0));

	/**
	 * Yields {@code true} if the call is a read or write operation.
	 * 
	 * @param call the call to check
	 * 
	 * @return {@code true} if the call is a read or write operation
	 */
	public static boolean isReadOrWriteCall(Call call) {
		return signatures.stream().anyMatch(e -> e.getSignature().equals(call.getTargetName()));
	}

	/**
	 * Yields {@code true} if the call is a write operation.
	 * 
	 * @param call the call to check
	 * 
	 * @return {@code true} if the call is a write operation
	 */
	public static boolean isWriteCall(Call call) {
		return signatures.stream().anyMatch(e -> e.getSignature().equals(call.getTargetName())
				&& e.getInstructionType().equals(TypeInstruction.WRITE));
	}

	/**
	 * Yields {@code true} if the call is a read operation.
	 * 
	 * @param call the call to check
	 * 
	 * @return {@code true} if the call is a read operation
	 */
	public static boolean isReadCall(Call call) {
		return signatures.stream().anyMatch(e -> e.getSignature().equals(call.getTargetName())
				&& e.getInstructionType().equals(TypeInstruction.READ));
	}

	/**
	 * Yields the read-write information of a call.
	 * 
	 * @param call the call
	 * 
	 * @return the read-write info
	 */
	public static ReadWriteInfo getReadWriteInfo(Call call) {
		for (ReadWriteInfo e : signatures) {
			if (e.getSignature().equals(call.getTargetName()))
				return e;
		}
		return null;
	}

}
