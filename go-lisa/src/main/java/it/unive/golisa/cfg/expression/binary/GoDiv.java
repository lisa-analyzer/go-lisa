package it.unive.golisa.cfg.expression.binary;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.numeric.Division;

/**
 * A Go division expression (e.g., x / y).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoDiv extends Division {

	/**
	 * Builds the division expression.
	 * 
	 * @param cfg      the {@link CFG} where this expression lies
	 * @param location the location where this expression is defined
	 * @param left     the left-hand side of this operation
	 * @param right    the right-hand side of this operation
	 */
	public GoDiv(CFG cfg, CodeLocation location, Expression left, Expression right) {
		super(cfg, location, left, right);
	}
}
