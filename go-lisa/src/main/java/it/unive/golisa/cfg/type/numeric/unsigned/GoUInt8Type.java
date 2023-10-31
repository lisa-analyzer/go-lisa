package it.unive.golisa.cfg.type.numeric.unsigned;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.NumericType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;
import java.util.Collections;
import java.util.Set;

/**
 * Go 8 bits unsigned int type. It implements the singleton design pattern, that
 * is the instances of this type are unique. The unique instance of this type
 * can be retrieved by {@link GoUInt8Type#INSTANCE}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoUInt8Type implements NumericType, Type {

	/**
	 * Unique instance of GoInt8 type.
	 */
	public static final GoUInt8Type INSTANCE = new GoUInt8Type();

	private GoUInt8Type() {
	}

	@Override
	public String toString() {
		return "uint8";
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
		return true;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoUInt8Type || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoUInt8Type ? this : Untyped.INSTANCE;
	}

	@Override
	public Expression defaultValue(CFG cfg, CodeLocation location) {
		return new GoInteger(cfg, location, 0);
	}

	@Override
	public boolean isIntegral() {
		return true;
	}

	@Override
	public Set<Type> allInstances(TypeSystem type) {
		return Collections.singleton(this);
	}
}