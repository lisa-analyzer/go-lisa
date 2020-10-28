package it.unive.golisa.cfg.calls.binary;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.NativeCall;
import it.unive.lisa.cfg.statement.Expression;

/**
 * Go numerical division native function class (e1 / e2).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoDiv extends NativeCall {
	
	/**
	 * Builds a Go division expression. 
	 * The location where this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp1	left-hand side operand
	 * @param exp2 	right-hand side operand 
	 */
	public GoDiv(CFG cfg, Expression exp1, Expression exp2) {
		super(cfg, null, -1, -1, "/", exp1, exp2);
	}
}
