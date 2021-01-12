package it.unive.golisa.cfg.statement;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Return;

/**
 * Go return statement class (e.g., var x int = 5).
 * TODO: at the moment, we handle only the case when {@code expression} is a single expressin
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoReturn extends Return {

	/**
	 * Builds a Go return statement.
	 * 
	 * @param cfg			the cfg that this statement belongs to
	 * @param expression	the expression to be returned
	 */
	public GoReturn(CFG cfg, Expression expression) {
		super(cfg, expression);
	}
}
