package it.unive.golisa.cfg.literals;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Literal;

/**
 * Go string value class.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoString extends Literal {

	/**
	 * Builds a Go string value. The location where 
	 * this Go string value appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this Go string belongs to
	 * @param value the string value
	 */
	public GoString(CFG cfg, String value) {
		super(cfg, value);
	}

}
