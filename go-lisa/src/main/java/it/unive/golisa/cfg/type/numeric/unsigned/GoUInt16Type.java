package it.unive.golisa.cfg.type.numeric.unsigned;

import it.unive.golisa.cfg.literal.GoInteger;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.NumericType;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.cfg.type.Untyped;

/**
 * Go 16 bits unsigned int type. 
 * 
 * It implements the singleton design pattern, that is 
 * the instances of this type are unique. The unique instance of
 * this type can be retrieved by {@link GoUInt16Type#INSTANCE}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoUInt16Type implements NumericType, GoType {

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
	
	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoUInt16Type || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoUInt16Type ? this : Untyped.INSTANCE;
	}
	
	@Override
	public Expression defaultValue(CFG cfg) {
		return new GoInteger(cfg, 0);
	}
}