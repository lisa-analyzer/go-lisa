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
 * Go 64 bits unsigned int type. 
 * 
 * It implements the singleton design pattern, that is 
 * the instances of this type are unique. The unique instance of
 * this type can be retrieved by {@link GoUInt64Type#INSTANCE}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoUInt64Type implements NumericType, GoType {

	/**
	 * Unique instance of GoInt64 type. 
	 */
	public static final GoUInt64Type INSTANCE = new GoUInt64Type();
	
	private GoUInt64Type() {}

	@Override
	public String toString() {
		return "uint64";
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof GoUInt64Type;
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
		return true;
	}
	
	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoUInt64Type || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoUInt64Type ? this : Untyped.INSTANCE;
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