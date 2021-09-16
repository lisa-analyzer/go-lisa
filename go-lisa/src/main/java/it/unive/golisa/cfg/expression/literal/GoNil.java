package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.type.GoNilType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Literal;

/**
 * A Go nil value.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoNil extends Literal {

	/**
	 * Builds a Go nil value. The location where 
	 * this literal appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg the cfg that this literal belongs to
	 */
	public GoNil(CFG cfg, SourceCodeLocation location) {
		super(cfg, location, "nil", GoNilType.INSTANCE);
	}

}
