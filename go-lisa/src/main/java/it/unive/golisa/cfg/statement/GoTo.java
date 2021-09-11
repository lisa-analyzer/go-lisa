package it.unive.golisa.cfg.statement;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.NoOp;

public class GoTo extends NoOp {

	public GoTo(CFG cfg, CodeLocation location) {
		super(cfg, location);
	}

}
