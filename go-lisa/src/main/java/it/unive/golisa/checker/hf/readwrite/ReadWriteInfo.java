package it.unive.golisa.checker.hf.readwrite;

import java.util.Arrays;
import java.util.Objects;

import it.unive.golisa.checker.hf.readwrite.ReadWriteHFUtils.KeyType;
import it.unive.golisa.checker.hf.readwrite.ReadWriteHFUtils.TypeInstruction;

public class ReadWriteInfo {

	private final TypeInstruction instructionType;
	private final String signature;
	private final KeyType keyType;
	private final int[] keyParameters;
	private final Integer collectionParam;

	public ReadWriteInfo(TypeInstruction instructionType, String signature, KeyType keyType, int[] keyParameters) {
		this.instructionType = instructionType;
		this.signature = signature;
		this.keyType = keyType;
		this.keyParameters = keyParameters;
		this.collectionParam = null;
	}

	public ReadWriteInfo(TypeInstruction instructionType, String signature, KeyType keyType, int[] keyParameters,
			int collectionParam) {
		this.instructionType = instructionType;
		this.signature = signature;
		this.keyType = keyType;
		this.keyParameters = keyParameters;
		this.collectionParam = Integer.valueOf(collectionParam);
	}

	public TypeInstruction getInstructionType() {
		return instructionType;
	}

	public String getSignature() {
		return signature;
	}

	public KeyType getKeyType() {
		return keyType;
	}

	public int[] getKeyParameters() {
		return keyParameters;
	}

	public Integer getCollectionParam() {
		return collectionParam;
	}

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
