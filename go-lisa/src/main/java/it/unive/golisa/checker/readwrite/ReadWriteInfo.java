package it.unive.golisa.checker.readwrite;


import java.util.Arrays;
import java.util.Objects;

import it.unive.golisa.checker.readwrite.ReadWriteHFUtils.KeyType;
import it.unive.golisa.checker.readwrite.ReadWriteHFUtils.TypeInstruction;

public class ReadWriteInfo {

	private final TypeInstruction instructionType;
	private final String signature;
	private final KeyType keyType;
	private final int[] keyParameters;
	
	public ReadWriteInfo(TypeInstruction instructionType, String signature, KeyType keyType, int[] keyParameters) {
		this.instructionType = instructionType;
		this.signature = signature;
		this.keyType = keyType;
		this.keyParameters = keyParameters;
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.hashCode(keyParameters);
		result = prime * result + Objects.hash(instructionType, keyType, signature);
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
		return instructionType == other.instructionType && Arrays.equals(keyParameters, other.keyParameters)
				&& keyType == other.keyType && signature.equals(other.signature);
	}

	
	
}

