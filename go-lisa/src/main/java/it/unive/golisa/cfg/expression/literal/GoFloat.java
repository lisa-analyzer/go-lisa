package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.type.untyped.GoUntypedFloat;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.literal.Literal;

/**
 * A Go float literal value.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoFloat extends Literal<Double> {

	/**
	 * Builds a Go float literal value.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param value    the float value
	 */
	public GoFloat(CFG cfg, CodeLocation location, Double value) {
		super(cfg, location, value, GoUntypedFloat.INSTANCE);
	}
}
