package it.unive.golisa.cfg.literals;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Literal;

/**
 * Go integer value class.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoInteger extends Literal {

	/**
	 * Builds a Go integer value. The location where 
	 * this Go integer value appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg   the cfg that this Go integer belongs to
	 * @param value the integer value
	 */
	public GoInteger(CFG cfg, Integer value) {
		super(cfg, value);
	}
}
