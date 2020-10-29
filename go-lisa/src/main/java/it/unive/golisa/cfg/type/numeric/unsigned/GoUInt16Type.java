package it.unive.golisa.cfg.type.numeric.unsigned;

import it.unive.lisa.cfg.type.NumericType;

/**
 * Go 16 bits int type. 
 * 
 * It implements the singleton design pattern, that is 
 * the instances of this type are unique. The unique instance of
 * this type can be retrieved by {@link GoUInt16Type#INSTANCE}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoUInt16Type implements NumericType {

	/**
	 * Unique instance of GoInt16 type. 
	 */
	public static final GoUInt16Type INSTANCE = new GoUInt16Type();
	
	private GoUInt16Type() {}

	@Override
	public String toString() {
		return "uint16";
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof GoUInt16Type;
	}
	
	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
	
	@Override
	public boolean is8Bits() {
		return false;
	}

	@Override
	public boolean is16Bits() {
		return true;
	}
	
	@Override
	public boolean is32Bits() {
		return false;
	}

	@Override
	public boolean is64Bits() {
		return false;
	}

	@Override
	public boolean isUnsigned() {
		return true;
	}
}