package it.unive.golisa.cfg.expression.instrumented;

import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.symbolic.value.Constant;
import it.unive.lisa.type.Type;

public class TaintPlaceholder extends Constant {

	public TaintPlaceholder(Type type, CodeLocation location) {
		super(type, "#TAINT_RETURNED_VALUE#", location);
	}

}
