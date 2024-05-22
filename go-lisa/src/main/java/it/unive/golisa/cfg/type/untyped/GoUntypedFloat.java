package it.unive.golisa.cfg.type.untyped;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoFloat;
import it.unive.golisa.cfg.type.numeric.floating.GoFloat32Type;
import it.unive.golisa.cfg.type.numeric.floating.GoFloat64Type;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.NumericType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * A Go untyped float type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoUntypedFloat implements Type, NumericType {

	/**
	 * Unique instance of GoUntypedInt type.
	 */
	public static final GoUntypedFloat INSTANCE = new GoUntypedFloat();

	private GoUntypedFloat() {
	}

	@Override
	public String toString() {
		return "float(untyped)";
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
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoFloat32Type
				|| other instanceof GoFloat64Type
				|| other instanceof GoUntypedFloat
				|| other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoFloat32Type || other instanceof GoFloat64Type || other instanceof GoUntypedFloat
				? other
				: Untyped.INSTANCE;
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
	public Expression defaultValue(CFG cfg, CodeLocation location) {
		return new GoFloat(cfg, location, 0.0);
	}

	@Override
	public boolean isIntegral() {
		return false;
	}

	@Override
	public Set<Type> allInstances(TypeSystem type) {
		return Collections.singleton(this);
	}
}
