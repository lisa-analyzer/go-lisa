package it.unive.golisa.cfg.type;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.composite.GoChannelType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoFunctionType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;
import java.util.Collection;
import java.util.Collections;

/*
 * nil is a valid value for the following. - Pointers - Unsafe pointers -
 * Interfaces - Channels - Maps - Slices - Functions
 */
public class GoNilType implements GoType {

	/**
	 * Unique instance of GoInt16 type.
	 */
	public static final GoNilType INSTANCE = new GoNilType();

	private GoNilType() {
	}

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoPointerType
				|| other instanceof GoInterfaceType
				|| other instanceof GoMapType
				|| other instanceof GoChannelType
				|| other instanceof GoSliceType
				|| other instanceof GoFunctionType
				|| other instanceof GoNilType
				|| other instanceof GoErrorType
				|| other instanceof Untyped;
	}

	@Override
	public Type commonSupertype(Type other) {
		if (other instanceof GoPointerType
				|| other instanceof GoInterfaceType
				|| other instanceof GoMapType
				|| other instanceof GoChannelType
				|| other instanceof GoSliceType
				|| other instanceof GoFunctionType
				|| other instanceof GoNilType
				|| other instanceof GoErrorType
				|| other instanceof Untyped)
			return other;
		return Untyped.INSTANCE;
	}

	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(this);
	}

	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoNil(cfg, location);
	}

	@Override
	public String toString() {
		return "nil";
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
