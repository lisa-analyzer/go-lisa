package it.unive.golisa.cfg.statement;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.NoOp;

/**
 * A goto statement (e.g., goto L;).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoTo extends NoOp {

	/**
	 * Builds the goto statement.
	 * 
	 * @param cfg      the {@link CFG} where this statement lies
	 * @param location the location where this statement is defined
	 */
	public GoTo(CFG cfg, CodeLocation location) {
		super(cfg, location);
	}
}
