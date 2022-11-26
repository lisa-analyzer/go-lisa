package it.unive.golisa.cfg.expression.literal;

import it.unive.golisa.cfg.type.GoNilType;
import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.literal.Literal;
import it.unive.lisa.type.Type;

/**
 * A Go nil value.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoNil extends Literal<String> {

	/**
	 * Builds a Go nil value.
	 * 
	 * @param cfg      the cfg that this literal belongs to
	 * @param location the location where this expression is defined
	 */
	public GoNil(CFG cfg, SourceCodeLocation location) {
		super(cfg, location, "nil", GoNilType.INSTANCE);
	}
	
	public GoNil(Type type, CFG cfg, SourceCodeLocation location) {
		super(cfg, location, "nil", type);
	}
}
