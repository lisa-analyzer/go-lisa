package it.unive.golisa.cfg.runtime.time.type;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;
import java.util.Collections;
import java.util.Set;

/**
 * A Duration represents the elapsed time between two instants as an int64
 * nanosecond count. The representation limits the largest representable
 * duration to approximately 290 years.
 * 
 * @link https://pkg.go.dev/time#Duration type Duration int64
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Duration implements GoType {

	/**
	 * Unique instance of GoInt64 type.
	 */
	public static final Duration INSTANCE = new Duration();

	private Duration() {
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof Duration || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof Duration || other.isUntyped())
			return other;
		return Untyped.INSTANCE;
	}

	@Override
	public Set<Type> allInstances(TypeSystem type) {
		return Collections.singleton(this);
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoInteger(cfg, location, 0);
	}

	@Override
	public String toString() {
		return "time.Duration";
	}

	@Override
	public boolean equals(Object other) {
		return this == other;
	}

	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
}
