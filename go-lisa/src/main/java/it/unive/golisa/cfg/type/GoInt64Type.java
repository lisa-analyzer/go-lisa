package it.unive.golisa.cfg.type;

import it.unive.lisa.cfg.type.NumericType;

/**
 * Go 64 bits int type. 
 * 
 * It implements the singleton design pattern, that is 
 * the instances of this type are unique. The unique instance of
 * this type can be retrieved by {@link GoInt64Type#INSTANCE}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoInt64Type implements NumericType {

	/**
	 * Unique instance of GoInt64 type. 
	 */
	public static final GoInt64Type INSTANCE = new GoInt64Type();
	
	private GoInt64Type() {}

	@Override
	public String toString() {
		return "int64";
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof GoInt32Type;
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
		return true;
	}

	@Override
	public boolean isUnsigned() {
		return false;
	}
}