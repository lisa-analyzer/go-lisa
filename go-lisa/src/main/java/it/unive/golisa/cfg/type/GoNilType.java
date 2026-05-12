package it.unive.golisa.cfg.type;

import java.util.Collections;
import java.util.Set;

import it.unive.golisa.cfg.expression.literal.GoNil;
import it.unive.golisa.cfg.type.composite.GoChannelType;
import it.unive.golisa.cfg.type.composite.GoErrorType;
import it.unive.golisa.cfg.type.composite.GoFunctionType;
import it.unive.golisa.cfg.type.composite.GoInterfaceType;
import it.unive.golisa.cfg.type.composite.GoMapType;
import it.unive.golisa.cfg.type.composite.GoPointerType;
import it.unive.golisa.cfg.type.composite.GoSliceType;
import it.unive.golisa.cfg.type.composite.GoTupleType;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.TypeSystem;
import it.unive.lisa.type.Untyped;

/**
 * The Go nil type. nil is a valid value for the following type: pointers,
 * unsafe pointers, interfaces, channels, maps, slices, functions.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoNilType implements Type {

	/**
	 * Unique instance of Go nil type.
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
				|| other instanceof GoTupleType
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
				|| other instanceof GoTupleType
				|| other instanceof Untyped)
			return other;
		return Untyped.INSTANCE;
	}

	@Override
	public Set<Type> allInstances(TypeSystem type) {
		return Collections.singleton(this);
	}

	@Override
	public Expression defaultValue(CFG cfg, CodeLocation location) {
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
