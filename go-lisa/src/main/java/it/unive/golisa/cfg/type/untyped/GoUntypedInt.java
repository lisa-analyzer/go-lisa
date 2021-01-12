package it.unive.golisa.cfg.type.untyped;

import java.util.Collection;
import java.util.Collections;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.NumericType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

public class GoUntypedInt implements GoType, NumericType {

	/**
	 * Unique instance of GoUntypedInt type. 
	 */
	public static final GoUntypedInt INSTANCE = new GoUntypedInt();
	
	private GoUntypedInt() {}

	@Override
	public String toString() {
		return "int(untyped)";
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof GoUntypedInt;
	}
	
	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
	
	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoType && other.isNumericType() || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoType && other.isNumericType() ? other : Untyped.INSTANCE;
	}

	@Override
	public Expression defaultValue(CFG cfg) {
		return new GoInteger(cfg, 0);
	}

	@Override
	public boolean isGoInteger() {
		return true;
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
	public Collection<Type> allInstances() {
		return Collections.singleton(this);
	}
}
