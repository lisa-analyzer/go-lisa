package it.unive.golisa.cfg.type;

import it.unive.lisa.cfg.type.StringType;

/**
 * String type of Go. This is the only string type available for Go.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoStringType implements StringType {

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
}
