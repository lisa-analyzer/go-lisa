package it.unive.golisa.cfg.statement;

import it.unive.lisa.program.SourceCodeLocation;
import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.Return;

/**
 * Go return statement class (e.g., return 5)
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoReturn extends Return {

	/**
	 * Builds a Go return statement.
	 * 
	 * @param cfg        the cfg that this statement belongs to
	 * @param location   the location where this expression is defined
	 * @param expression the expression to be returned
	 */
	public GoReturn(CFG cfg, SourceCodeLocation location, Expression expression) {
		super(cfg, location, expression);
	}
}
