package it.unive.golisa.cfg.expression.literal;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.literal.Literal;
import it.unive.lisa.type.Type;

/**
 * A Go function literal.
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoFunctionLiteral extends Literal<CFG> {

	/**
	 * Builds a function literal.
	 * 
	 * @param cfg        the {@link CFG} where this expression lies
	 * @param location   the location where this expression is defined
	 * @param value      the function literal
	 * @param staticType the static type of this function literal
	 */
	public GoFunctionLiteral(CFG cfg, CodeLocation location, CFG value, Type staticType) {
		super(cfg, location, value, staticType);
	}

}
