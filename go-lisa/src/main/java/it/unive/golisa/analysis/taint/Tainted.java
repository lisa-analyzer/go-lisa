package it.unive.golisa.analysis.taint;

import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * The tainted constant.
 */
public class Tainted extends Constant {

	/**
	 * Builds the instance of tainted.
	 * 
	 * @param location the location
	 */
	public Tainted(CodeLocation location) {
		super(Untyped.INSTANCE, "T", location);
	}
	
	/**
	 * Builds the instance of tainted.
	 * @param type the type of this expression
	 * @param location the location
	 */
	public Tainted(Type type, CodeLocation location) {
		super(type, "T", location);
	}

}
