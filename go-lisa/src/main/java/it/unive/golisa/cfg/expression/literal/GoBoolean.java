package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Literal;

/**
 * A Go Boolean value.
 * The static type of a Go Boolean value is {@link GoBoolType}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoBoolean extends Literal {
	
	/**
	 * Builds a Go boolean value. The location where 
	 * this Go integer value appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg   the cfg that this Go integer belongs to
	 * @param value the Boolean value
	 */
	public GoBoolean(CFG cfg, Boolean value) {
		super(cfg, value, GoBoolType.INSTANCE);
	}
}
