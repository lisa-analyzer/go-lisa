package it.unive.golisa.cfg.type.numeric.signed;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.NumericType;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.cfg.type.Untyped;

/**
 * Go 64 bits int type. 
 * 
 * It implements the singleton design pattern, that is 
 * the instances of this type are unique. The unique instance of
 * this type can be retrieved by {@link GoInt64Type#INSTANCE}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoInt64Type implements NumericType, GoType {

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
		return other instanceof GoInt64Type;
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
		return false;
	}

	@Override
	public boolean is64Bits() {
		return true;
	}

	@Override
	public boolean isUnsigned() {
		return false;
	}
	
	
	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoInt64Type || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoInt64Type ? this : Untyped.INSTANCE;
	}
	
	@Override
	public Expression defaultValue(CFG cfg) {
		return new GoInteger(cfg, 0);
	}
	
	@Override
	public boolean isIntegerType() {
		return true;
	}
}