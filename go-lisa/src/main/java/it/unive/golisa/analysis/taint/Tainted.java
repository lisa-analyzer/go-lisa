package it.unive.golisa.analysis.taint;

import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.Type;

public class Tainted extends Constant {

	public Tainted(Type type, Object value, CodeLocation location) {
		super(type, "T", location);
	}

}
