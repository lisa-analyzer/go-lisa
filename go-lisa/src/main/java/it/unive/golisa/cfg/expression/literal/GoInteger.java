package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Literal;

/**
 * Go integer value class.
 * The static type of a Go integer value is {@link GoBoolInt}.
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
	public GoInteger(CFG cfg, SourceCodeLocation location, Object value) {
		super(cfg, location, value, GoUntypedInt.INSTANCE);
	}
}
