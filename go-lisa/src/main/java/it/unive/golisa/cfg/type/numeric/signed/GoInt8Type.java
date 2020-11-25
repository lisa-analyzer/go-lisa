package it.unive.golisa.cfg.type.numeric.signed;

import it.unive.lisa.cfg.type.NumericType;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.cfg.type.Untyped;

/**
 * Go 8 bits int type. 
 * 
 * It implements the singleton design pattern, that is 
 * the instances of this type are unique. The unique instance of
 * this type can be retrieved by {@link GoInt8Type#INSTANCE}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoInt8Type implements NumericType {

	/**
	 * Unique instance of GoInt8 type. 
	 */
	public static final GoInt8Type INSTANCE = new GoInt8Type();
	
	private GoInt8Type() {}

	@Override
	public String toString() {
		return "int8";
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof GoInt8Type;
	}
	
	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
	
	@Override
	public boolean is8Bits() {
		return true;
	}
	
	@Override
	public boolean is16Bits() {
		return false;
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
		return false;
	}
	
	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoInt8Type || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoInt8Type ? this : Untyped.INSTANCE;
	}
}