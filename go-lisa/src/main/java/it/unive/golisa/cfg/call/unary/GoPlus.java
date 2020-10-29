package it.unive.golisa.cfg.call.unary;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NativeCall;

/**
 * Go unary plus native function class (e.g., +(5 - 3), +5).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoPlus extends NativeCall {

	/**
	 * Builds a Go unary plus expression. The location where 
	 * this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp	operand
	 */
	public GoPlus(CFG cfg, Expression exp) {
		super(cfg, null, -1, -1, "+", exp);
	}

}
