package it.unive.golisa.cfg.type.numeric.signed;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.type.GoType;
import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.NumericType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * Go 32 bits int type. It implements the singleton design pattern, that is the
 * instances of this type are unique. The unique instance of this type can be
 * retrieved by {@link GoInt32Type#INSTANCE}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoInt32Type implements NumericType, GoType {

	/**
	 * Unique instance of GoInt32 type.
	 */
	public static final GoInt32Type INSTANCE = new GoInt32Type();

	private GoInt32Type() {
	}

	@Override
	public String toString() {
		return "int32";
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
		return true;
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
		return other instanceof GoInt32Type || other instanceof GoIntType || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoInt32Type || other instanceof GoUntypedInt || other instanceof GoIntType ? this
				: Untyped.INSTANCE;
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoInteger(cfg, location, 0);
	}

	@Override
	public Set<Type> allInstances(TypeSystem type) {
		return Collections.singleton(this);
	}

	@Override
	public boolean isIntegral() {
		return true;
	}
}