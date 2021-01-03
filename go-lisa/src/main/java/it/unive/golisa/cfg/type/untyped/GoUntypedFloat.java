package it.unive.golisa.cfg.type.untyped;

import it.unive.golisa.cfg.expression.literal.GoFloat;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.NumericType;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.cfg.type.Untyped;

public class GoUntypedFloat implements GoType, NumericType  {

	/**
	 * Unique instance of GoUntypedInt type. 
	 */
	public static final GoUntypedFloat INSTANCE = new GoUntypedFloat();

	private GoUntypedFloat() {}

	@Override
	public String toString() {
		return "float(untyped)";
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof GoUntypedFloat;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return (other instanceof GoType && ((GoType) other).isGoFloat()) || other.isUntyped() ? true : false;
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoType && ((GoType) other).isGoFloat() ? other : Untyped.INSTANCE;
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
	public Expression defaultValue(CFG cfg) {
		return new GoFloat(cfg, 0.0);
	}

	@Override
	public boolean isGoInteger() {
		return false;
	}

}
