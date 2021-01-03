package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.type.numeric.floating.GoFloat64Type;
import it.unive.golisa.cfg.type.untyped.GoUntypedFloat;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Literal;

/**
 * Go float value class.
 * The static type of a Go float value is {@link GoFloat64Type}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoFloat extends Literal {

	/**
	 * Builds a Go float value. The location where 
	 * this Go float value appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg   the cfg that this float value belongs to
	 * @param value the float value
	 */
	public GoFloat(CFG cfg, Double value) {
		super(cfg, value, GoUntypedFloat.INSTANCE);
	}
}
