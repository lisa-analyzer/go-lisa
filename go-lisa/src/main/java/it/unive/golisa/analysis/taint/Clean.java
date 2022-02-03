package it.unive.golisa.analysis.taint;

import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.Type;
import it.unive.lisa.type.Untyped;

public class Clean extends Constant {

	public Clean(CodeLocation location) {
		super(Untyped.INSTANCE, "C", location);
	}

	public Clean(Type type, CodeLocation location) {
		super(type, "C", location);
	}

}
