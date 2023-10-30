package it.unive.golisa.cfg.type.untyped;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.type.composite.GoSliceType;
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
 * A Go untyped int type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoUntypedInt implements Type, NumericType {

	/**
	 * Unique instance of GoUntypedInt type.
	 */
	public static final GoUntypedInt INSTANCE = new GoUntypedInt();

	private GoUntypedInt() {
	}

	@Override
	public String toString() {
		return "int(untyped)";
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
		return other.isNumericType() || other.isUntyped()
				|| (other instanceof GoSliceType && canBeAssignedTo(((GoSliceType) other).getContentType()));
	}

	@Override
	public Type commonSupertype(Type other) {
		return other.isNumericType() ? other : Untyped.INSTANCE;
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
	public Set<Type> allInstances(TypeSystem type) {
		return Collections.singleton(this);
	}
}
