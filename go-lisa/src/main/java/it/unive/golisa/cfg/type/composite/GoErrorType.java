package it.unive.golisa.cfg.type.composite;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoNilType;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * The Go error type.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoErrorType implements GoType {

	/**
	 * Unique instance of Go error type.
	 */
	public static final GoErrorType INSTANCE = new GoErrorType();

	private GoErrorType() {
	}

	@Override
	public String toString() {
		return "error";
	}

	@Override
	public boolean equals(Object other) {
		return other instanceof GoErrorType;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoErrorType || other instanceof GoNilType || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof GoErrorType
				|| other instanceof GoNilType)
			return this;
		return Untyped.INSTANCE;
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoNil(cfg, location);
	}

	@Override
	public Set<Type> allInstances(TypeSystem type) {
		return Collections.singleton(this);
	}
}
