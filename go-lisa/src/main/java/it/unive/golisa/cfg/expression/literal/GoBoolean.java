package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.literal.Literal;

/**
 * A Go Boolean literal value.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoBoolean extends Literal<Boolean> {

	/**
	 * Builds a Go Boolean literal value.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param value    the Boolean value
	 */
	public GoBoolean(CFG cfg, SourceCodeLocation location, Boolean value) {
		super(cfg, location, value, GoBoolType.INSTANCE);
	}
}
