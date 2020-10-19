package it.unive.golisa.cfg.calls;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.NativeCall;
import it.unive.lisa.cfg.statement.Expression;

/**
 * Go numerical minus native function class.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoMinus extends NativeCall {

	/**
	 * Builds a Go minus expression. 
	 * The location where this Go minus expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this Go minus expression belongs to
	 * @param exp1	left-side operand
	 * @param exp2 	right-side operand 
	 */
	public GoMinus(CFG cfg, Expression exp1, Expression exp2) {
		super(cfg, null, -1, -1, "-", exp1, exp2);
	}
}
