package it.unive.golisa.cfg.type;

import it.unive.golisa.cfg.expression.literal.GoBoolean;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.type.BooleanType;
import it.unive.lisa.cfg.type.Type;
import it.unive.lisa.cfg.type.Untyped;

/**
 * Boolean type of Go. This is the only Boolean type available for Go.
 * 
 * It implements the singleton design pattern, that is 
 * the instances of this type are unique. The unique instance of
 * this type can be retrieved by {@link GoBoolType#INSTANCE}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoBoolType implements BooleanType, GoType {
	
	/**
	 * Unique instance of GoBoolean type. 
	 */
	public static final GoBoolType INSTANCE = new GoBoolType();
	
	private GoBoolType() {}

	@Override
	public String toString() {
		return "bool";
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof GoBoolType;
	}
	
	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoBoolType || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoBoolType ? this : Untyped.INSTANCE;
	}

	@Override
	public Expression defaultValue(CFG cfg) {
		return new GoBoolean(cfg, false);
	}
	
	@Override
	public boolean isIntegerType() {
		return false;
	}
}
