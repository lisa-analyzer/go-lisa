package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.type.untyped.GoUntypedInt;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.literal.Literal;

/**
 * A Go integer literal value.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoInteger extends Literal<Object> {

	/**
	 * Builds a Go integer literal value.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param value    the integer value
	 */
	public GoInteger(CFG cfg, SourceCodeLocation location, Object value) {
		super(cfg, location, value, GoUntypedInt.INSTANCE);
	}
}
