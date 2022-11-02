package it.unive.golisa.cfg.type.numeric.unsigned;

import java.util.Collection;
import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.NumericType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * uintptr is an integer type that is large enough to hold the bit pattern of
 * any pointer. {@link GoUIntPrtType#INSTANCE}.
 * 
 * @link https://pkg.go.dev/builtin#uintptr
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class GoUIntPrtType implements NumericType, GoType {

	/**
	 * Unique instance of GoUIntPrtType type.
	 */
	public static final GoUIntPrtType INSTANCE = new GoUIntPrtType();

	private GoUIntPrtType() {
	}

	@Override
	public String toString() {
		return "uintprt";
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
		return true;
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoUIntPrtType || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoUIntPrtType ? this : Untyped.INSTANCE;
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
	public Set<Type> allInstances(TypeSystem type) {
		return Collections.singleton(this);
	}
}
