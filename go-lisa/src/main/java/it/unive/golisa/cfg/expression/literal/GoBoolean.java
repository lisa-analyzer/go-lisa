package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.literal.Literal;

/**
 * A Go Boolean value. The static type of a Go Boolean value is
 * {@link GoBoolType}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoBoolean extends Literal<Boolean> {

	/**
	 * Builds a Go boolean value. The location where this Go integer value
	 * appears is unknown (i.e. no source file/line/column is available).
	 * 
	 * @param cfg   the cfg that this Go integer belongs to
	 * @param value the Boolean value
	 */
	public GoBoolean(CFG cfg, SourceCodeLocation location, Boolean value) {
		super(cfg, location, value, GoBoolType.INSTANCE);
	}
}
