package it.unive.golisa.cfg.expression.literal;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.NullLiteral;

/**
 * A Go nil value.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoNil extends NullLiteral {

	/**
	 * Builds a Go nil value. The location where 
	 * this literal appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg the cfg that this literal belongs to
	 */
	public GoNil(CFG cfg) {
		super(cfg);
	}
	
	@Override
	public String toString() {
		return "nil";
	}
}
