package it.unive.golisa.cfg.type.numeric.signed;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.NumericType;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.cfg.type.Untyped;

/**
 * Go int type. The int type, in Go, is a machine dependent type 
 * since his size (32 or 64 bits) depends on the type of architecture that it is used.
 * 
 * It implements the singleton design pattern, that is 
 * the instances of this type are unique. The unique instance of
 * this type can be retrieved by {@link GoIntType#INSTANCE}.
 * 
 * @link https://www.golang-book.com/books/intro/3
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoIntType implements NumericType, GoType {

	/**
	 * Unique instance of GoInt type. 
	 */
	public static final GoIntType INSTANCE = new GoIntType();
	
	private GoIntType() {}

	@Override
	public String toString() {
		return "int";
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof GoIntType;
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
		return false;
	}
	
	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoIntType || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoIntType ? this : Untyped.INSTANCE;
	}
	
	@Override
	public Expression defaultValue(CFG cfg) {
		return new GoInteger(cfg, 0);
	}
	
	@Override
	public boolean isGoInteger() {
		return true;
	}
}
