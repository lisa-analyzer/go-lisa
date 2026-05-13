package it.unive.golisa.cfg.expression.instrumented;

import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.Type;

/**
 * Taint analysis placeholder for tainted expressions.
 */
public class TaintPlaceholder extends Constant {

	/**
	 * Builds the placeholder.
	 * @param type the type 
	 * @param location the location
	 */
	public TaintPlaceholder(Type type, CodeLocation location) {
		super(type, "#TAINT_RETURNED_VALUE#", location);
	}

}
