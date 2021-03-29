package it.unive.golisa.cfg.type.numeric.floating;

import java.util.Collection;
import java.util.Collections;

import it.unive.golisa.cfg.expression.literal.GoFloat;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.NumericType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * Go 64 bits float type. 
 * 
 * It implements the singleton design pattern, that is 
 * the instances of this type are unique. The unique instance of
 * this type can be retrieved by {@link GoFloat64Type#INSTANCE}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoFloat64Type implements NumericType, GoType {

	/**
	 * Unique instance of Float64Type type. 
	 */
	public static final GoFloat64Type INSTANCE = new GoFloat64Type();

	private GoFloat64Type() {}

	@Override
	public String toString() {
		return "float64";
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof GoFloat64Type;
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
	public Expression defaultValue(CFG cfg) {
		return new GoFloat(cfg, 0.0);
	}
	
	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(this);
	}

	@Override
	public boolean isIntegral() {
		return false;
	}
}
