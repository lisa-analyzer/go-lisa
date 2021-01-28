package it.unive.golisa.cfg.type.composite;

import java.util.Collection;
import java.util.Collections;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.GoType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;

public class GoErrorType implements GoType {

	/**
	 * Unique instance of GoError type. 
	 */
	public static final GoErrorType INSTANCE = new GoErrorType();

	private GoErrorType() {}

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
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Type commonSupertype(Type other) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Expression defaultValue(CFG cfg) {
		return new GoNil(cfg);
	}

	@Override
	public boolean isGoInteger() {
		return false;
	}

	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(this);
	}
}
