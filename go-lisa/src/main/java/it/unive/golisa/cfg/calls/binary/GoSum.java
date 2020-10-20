package it.unive.golisa.cfg.calls.binary;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NativeCall;

/**
 * Go numerical sum native function class.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoSum extends NativeCall {
	
	/**
	 * Builds a Go sum expression. The location where 
	 * this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp1	left-hand side operand
	 * @param exp2 	right-hand side operand 
	 */
	public GoSum(CFG cfg, Expression exp1, Expression exp2) {
		super(cfg, null, -1, -1, "+", exp1, exp2);
	}
}
