package it.unive.golisa.cfg.expression;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.VariableRef;

public class GoAnonymousVariable extends VariableRef {

	public GoAnonymousVariable(CFG cfg, CodeLocation location) {
		super(cfg, location, "_");
	}

}
