package it.unive.golisa.cfg.type.numeric.unsigned;

import java.util.Collection;
import java.util.Collections;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.NumericType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

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
public class GoUIntType implements NumericType, GoType {

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
		return this == other;
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

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoInteger(cfg, location, 0);
	}
	
	@Override
	public boolean isIntegral() {
		return true;
	}
	
	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(this);
	}
}
