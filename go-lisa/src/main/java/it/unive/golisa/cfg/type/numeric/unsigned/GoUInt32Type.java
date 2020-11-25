package it.unive.golisa.cfg.type.numeric.unsigned;

import it.unive.lisa.cfg.type.NumericType;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.cfg.type.Untyped;

/**
 * Go 32 bits unsigned int type. 
 * 
 * It implements the singleton design pattern, that is 
 * the instances of this type are unique. The unique instance of
 * this type can be retrieved by {@link GoUInt32Type#INSTANCE}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoUInt32Type implements NumericType {

	/**
	 * Unique instance of GoInt32 type. 
	 */
	public static final GoUInt32Type INSTANCE = new GoUInt32Type();
	
	private GoUInt32Type() {}

	@Override
	public String toString() {
		return "uint32";
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof GoUInt32Type;
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
		return false;
	}
	
	@Override
	public boolean is32Bits() {
		return true;
	}

	@Override
	public boolean is64Bits() {
		return false;
	}

	@Override
	public boolean isUnsigned() {
		return true;
	}
	
	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoUInt32Type || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoUInt32Type ? this : Untyped.INSTANCE;
	}
}