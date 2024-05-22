package it.unive.golisa.cfg.type.numeric.floating;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoFloat;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.NumericType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * Go 64 bits float type. It implements the singleton design pattern, that is
 * the instances of this type are unique. The unique instance of this type can
 * be retrieved by {@link GoFloat64Type#INSTANCE}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoFloat64Type implements NumericType, Type {

	/**
	 * Unique instance of Float64Type type.
	 */
	public static final GoFloat64Type INSTANCE = new GoFloat64Type();

	private GoFloat64Type() {
	}

	@Override
	public String toString() {
		return "float64";
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
		return other instanceof GoFloat64Type || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoFloat64Type ? this : Untyped.INSTANCE;
	}

	@Override
	public Expression defaultValue(CFG cfg, CodeLocation location) {
		return new GoFloat(cfg, location, 0.0);
	}

	@Override
	public Set<Type> allInstances(TypeSystem type) {
		return Collections.singleton(this);
	}

	@Override
	public boolean isIntegral() {
		return false;
	}
}
