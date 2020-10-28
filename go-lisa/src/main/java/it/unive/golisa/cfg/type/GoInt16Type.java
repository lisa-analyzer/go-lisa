package it.unive.golisa.cfg.type;

import it.unive.lisa.cfg.type.NumericType;

/**
 * Go 16 bits int type. 
 * 
 * It implements the singleton design pattern, that is 
 * the instances of this type are unique. The unique instance of
 * this type can be retrieved by {@link GoInt16Type#INSTANCE}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoInt16Type implements NumericType {

	/**
	 * Unique instance of GoInt16 type. 
	 */
	public static final GoInt16Type INSTANCE = new GoInt16Type();
	
	private GoInt16Type() {}

	@Override
	public String toString() {
		return "int16";
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof GoInt16Type;
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
	public boolean is32Bits() {
		return false;
	}

	@Override
	public boolean is64its() {
		return false;
	}

	@Override
	public boolean isUnsigned() {
		return false;
	}
}