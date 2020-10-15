package it.unive.golisa.cfg.calls;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NativeCall;

/**
 * Go integer sum native function class.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoSum extends NativeCall {

	/**
	 * Builds a Go sum expression. The location where 
	 * this Go sum expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this Go sum expression belongs to
	 * @param e1	left-side operand
	 * @param e2 	right-side operand 
	 */
	public GoSum(CFG cfg,Expression e1, Expression e2) {
		super(cfg, "+");
	}

}
