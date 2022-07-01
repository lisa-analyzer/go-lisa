package it.unive.golisa.cfg.statement;

import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.NoOp;

/**
 * A fallthrough statement.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoFallThrough extends NoOp {

	/**
	 * Builds the fallthrough expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 */
	public GoFallThrough(CFG cfg, SourceCodeLocation location) {
		super(cfg, location);
	}
}
