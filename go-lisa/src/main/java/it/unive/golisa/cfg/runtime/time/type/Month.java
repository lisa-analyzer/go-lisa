package it.unive.golisa.cfg.runtime.time.type;

import it.unive.golisa.cfg.expression.literal.GoInteger;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.Collections;

/**
 * A Month specifies a month of the year (January = 1, ...).
 * 
 * @link https://pkg.go.dev/time#Month type Month int
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class Month implements GoType {

	/**
	 * Unique instance of the {@link Month} type.
	 */
	public static final Month INSTANCE = new Month();

	private Month() {
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof Month || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof Month || other.isUntyped())
			return other;
		return Untyped.INSTANCE;
	}

	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(this);
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoInteger(cfg, location, 0);
	}

	@Override
	public String toString() {
		return "time.Month";
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
