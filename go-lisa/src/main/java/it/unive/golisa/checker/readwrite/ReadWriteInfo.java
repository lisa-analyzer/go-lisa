package it.unive.golisa.checker.readwrite;


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

	
	
}

