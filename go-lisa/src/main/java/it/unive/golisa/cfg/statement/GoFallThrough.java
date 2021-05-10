package it.unive.golisa.cfg.statement;

import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.NoOp;

public class GoFallThrough extends NoOp {

	public GoFallThrough(CFG cfg, SourceCodeLocation location) {
		super(cfg, location);
	}
}
