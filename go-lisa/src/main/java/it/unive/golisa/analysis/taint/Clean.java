package it.unive.golisa.analysis.taint;

import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.Untyped;

public class Clean extends Constant {

	public Clean(CodeLocation location) {
		super(Untyped.INSTANCE, "C", location);
	}

}
