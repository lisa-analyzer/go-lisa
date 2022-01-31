package it.unive.golisa.cfg.expression.unknown;

import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.literal.Literal;
import it.unive.lisa.type.Untyped;

/**
 * Placeholder for an unknown value.
 * 
 * @author <a href="mailto:luca.olvieri@univr.it">Luca Olivieri</a>
 */
public class GoUnknown extends Literal<String> {

	/**
	 */
	public GoUnknown(CFG cfg, SourceCodeLocation location) {
		super(cfg, location, "UNKNOWN" , Untyped.INSTANCE);
	}

	@Override
	public String toString() {
		return "<" + getValue() + ">";
	}
}
