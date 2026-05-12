package it.unive.golisa.cfg.expression.unary;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.logic.Not;


/**
 * Go unary not Boolean expression (e.g., !(x > y)).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoNot extends Not {

	public GoNot(CFG cfg, CodeLocation location, Expression expression) {
		super(cfg, location, expression);
	}

}
