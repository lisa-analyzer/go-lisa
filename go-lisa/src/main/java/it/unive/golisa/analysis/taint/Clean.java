package it.unive.golisa.analysis.taint;

import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.Type;

/**
 * The class represents the clean constant.
 */
public class Clean extends Constant {

	/**
	 * Builds the instance of clean.
	 * 
	 * @param type     the type
	 * @param location the location
	 */
	public Clean(Type type, CodeLocation location) {
		super(type, "C", location);
	}

}
