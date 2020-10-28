package it.unive.golisa.cfg.type;

import it.unive.lisa.cfg.type.BooleanType;

/**
 * Boolean type of Go. This is the only Boolean type available for Go.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoBoolType implements BooleanType {
	
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
}
