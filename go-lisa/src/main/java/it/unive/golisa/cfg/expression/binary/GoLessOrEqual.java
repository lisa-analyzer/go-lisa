package it.unive.golisa.cfg.expression.binary;

import it.unive.lisa.program.cfg.CFG;
import it.unive.lisa.program.cfg.CodeLocation;
import it.unive.lisa.program.cfg.statement.Expression;
import it.unive.lisa.program.cfg.statement.comparison.LessOrEqual;

/**
 * A Go less or equal than expression (e.g., e1 < e2).
 * 
 * @author <a href="mailto:vincenzo.arceri@unipr.it">Vincenzo Arceri</a>
 */
public class GoLessOrEqual extends LessOrEqual {

	public GoLessOrEqual(CFG cfg, CodeLocation location, Expression left, Expression right) {
		super(cfg, location, left, right);
	}

}
