package it.unive.golisa.cfg.type;

import java.util.Collection;
import java.util.Collections;

import it.unive.golisa.cfg.expression.literal.GoString;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.type.StringType;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;


/**
 * String type of Go. This is the only string type available for Go.
 * 
 * It implements the singleton design pattern, that is 
 * the instances of this type are unique. The unique instance of
 * this type can be retrieved by {@link GoStringType#INSTANCE}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoStringType implements StringType, GoType {

	/**
	 * Unique instance of GoString type. 
	 */
	public static final GoStringType INSTANCE = new GoStringType();
	
	private GoStringType() {}

	@Override
	public String toString() {
		return "string";
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof GoStringType;
	}
	
	@Override
	public int hashCode() {
		return System.identityHashCode(this);
	}
	

	@Override
	public boolean canBeAssignedTo(Type other) {
		return other instanceof GoStringType || other.isUntyped();
	}

	@Override
	public Type commonSupertype(Type other) {
		return other instanceof GoStringType ? this : Untyped.INSTANCE;
	}
	
	@Override
	public Expression defaultValue(CFG cfg, SourceCodeLocation location) {
		return new GoString(cfg, location, "");
	}
		
	@Override
	public Collection<Type> allInstances() {
		return Collections.singleton(this);
	}
}
