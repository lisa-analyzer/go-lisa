package it.unive.golisa.cfg.runtime.math.rand.type;

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
 * A Random generator
 * 
 * @link https://pkg.go.dev/math/rand#Rand Rand
 * 
 * @author <a href="mailto:luca.olivieri@univr.it">Luca Olivieri</a>
 */
public class Rand implements GoType {

	/**
	 * Unique instance of GoInt64 type.
	 */
	public static final Rand INSTANCE = new Rand();

	private Rand() {
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof Rand || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof Rand || other.isUntyped())
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
		return "math/rand.Rand";
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
