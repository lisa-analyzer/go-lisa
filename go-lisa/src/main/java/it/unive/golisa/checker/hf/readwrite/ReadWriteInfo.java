package it.unive.golisa.checker.hf.readwrite;

import java.util.Arrays;
import java.util.Objects;

import it.unive.golisa.checker.hf.readwrite.ReadWriteHFUtils.KeyType;
import it.unive.golisa.checker.hf.readwrite.ReadWriteHFUtils.TypeInstruction;

/**
 * Read write information
 */
public class ReadWriteInfo {

	/**
	 * The type of instruction.
	 */
	private final TypeInstruction instructionType;
	
	/**
	 * The signature of instruction.
	 */
	private final String signature;
	
	/**
	 * The type of key.
	 */
	private final KeyType keyType;
	
	/**
	 * The key parameters.
	 */
	private final int[] keyParameters;
	
	/**
	 * The  collection parameter.
	 */
	private final Integer collectionParam;

	/**
	 * Builds the information.
	 * 
	 * @param instructionType the instruction type
	 * @param signature the signature of instruction
	 * @param keyType the key type
	 * @param keyParameters the key parameters 
	 */
	public ReadWriteInfo(TypeInstruction instructionType, String signature, KeyType keyType, int[] keyParameters) {
		this.instructionType = instructionType;
		this.signature = signature;
		this.keyType = keyType;
		this.keyParameters = keyParameters;
		this.collectionParam = null;
	}

	/**
	 * Builds the information.
	 * 
	 * @param instructionType the instruction type
	 * @param signature the signature of instruction
	 * @param keyType the key type
	 * @param keyParameters the key parameters 
	 * @param collectionParam the collection parameter
	 */
	public ReadWriteInfo(TypeInstruction instructionType, String signature, KeyType keyType, int[] keyParameters,
			int collectionParam) {
		this.instructionType = instructionType;
		this.signature = signature;
		this.keyType = keyType;
		this.keyParameters = keyParameters;
		this.collectionParam = Integer.valueOf(collectionParam);
	}

	/**
	 * Yields the instruction type.
	 * @return the instruction type
	 */
	public TypeInstruction getInstructionType() {
		return instructionType;
	}

	/**
	 * Yields the signature.
	 * @return the signature
	 */
	public String getSignature() {
		return signature;
	}

	/**
	 * Yields the key type.
	 * @return the key type
	 */
	public KeyType getKeyType() {
		return keyType;
	}
	/**
	 * Yields the key parameters.
	 * @return the key parameters
	 */
	public int[] getKeyParameters() {
		return keyParameters;
	}

	/**
	 * Yields the collection parameter.
	 * @return the collection parameter
	 */
	public Integer getCollectionParam() {
		return collectionParam;
	}

	/**
	 * Yields {@code true} if the instruction has a collection parameter.
	 * @return {@code true} if the instruction has a collection parameter
	 */
	public boolean hasCollection() {
		return collectionParam != null;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(keyParameters);
		result = prime * result + Objects.hash(collectionParam, instructionType, keyType, signature);
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReadWriteInfo other = (ReadWriteInfo) obj;
		return Objects.equals(collectionParam, other.collectionParam) && instructionType == other.instructionType
				&& Arrays.equals(keyParameters, other.keyParameters) && keyType == other.keyType
				&& Objects.equals(signature, other.signature);
	}

}
