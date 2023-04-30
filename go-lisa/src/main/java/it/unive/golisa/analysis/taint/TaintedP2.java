package it.unive.golisa.analysis.taint;

import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

/**
 * The tainted constant.
 */
public class TaintedP2 extends Constant {

	/**
	 * Builds the instance of tainted.
	 * 
	 * @param location the location
	 */
	public TaintedP2(CodeLocation location) {
		this(Untyped.INSTANCE, location);
	}

	public TaintedP2(Type type, CodeLocation location) {
		super(type, "T-P1", location);
	}

}
