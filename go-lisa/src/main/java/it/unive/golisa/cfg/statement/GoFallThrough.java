package it.unive.golisa.cfg.statement;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.NoOp;

public class GoFallThrough extends NoOp {

	public GoFallThrough(CFG cfg) {
		super(cfg);
	}
}
