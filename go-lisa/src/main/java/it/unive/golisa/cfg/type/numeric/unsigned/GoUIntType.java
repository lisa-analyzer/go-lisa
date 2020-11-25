package it.unive.golisa.cfg.type.numeric.unsigned;

import it.unive.lisa.cfg.type.NumericType;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.cfg.type.Untyped;

/**
 * Go unsigned int type. The unsigned int type, in Go, is a machine dependent type 
 * since his size (32 or 64 bits) depends on the type of architecture that it is used.
 * 
 * It implements the singleton design pattern, that is 
 * the instances of this type are unique. The unique instance of
 * this type can be retrieved by {@link GoUIntType#INSTANCE}.
 * 
 * @link https://www.golang-book.com/books/intro/3
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoUIntType implements NumericType {

	/**
	 * Unique instance of GoInt type. 
	 */
	public static final GoUIntType INSTANCE = new GoUIntType();
	
	private GoUIntType() {}

	@Override
	public String toString() {
		return "uint";
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof GoUIntType;
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
		// TODO the format depends on the type of architecture that it is used.
		return false;
	}

	@Override
	public boolean is64Bits() {
		// TODO the format depends on the type of architecture that it is used.
		return false;
	}

	@Override
	public boolean isUnsigned() {
		return true;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoUIntType || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoUIntType ? this : Untyped.INSTANCE;
	}
}
