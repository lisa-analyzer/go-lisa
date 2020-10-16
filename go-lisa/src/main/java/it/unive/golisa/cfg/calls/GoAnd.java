package it.unive.golisa.cfg.calls;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NativeCall;

/**
 * Go Boolean and native function class.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoAnd extends NativeCall {
	
	/**
	 * Builds a Go and expression. 
	 * The location where this Go and expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this Go and expression belongs to
	 * @param exp1	left-side operand
	 * @param exp2 	right-side operand 
	 */
	public GoAnd(CFG cfg, Expression exp1, Expression exp2) {
		super(cfg, null, -1, -1, "&&", exp1, exp2);
	}
	
	@Override
	public String toString() {
		return getParameters()[0].toString() + " && " + getParameters()[1].toString();
	}
}
