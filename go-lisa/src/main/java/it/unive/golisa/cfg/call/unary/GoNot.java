package it.unive.golisa.cfg.call.unary;

import it.unive.golisa.cfg.type.GoBoolType;
import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NativeCall;

/**
 * Go unary not native function class (e.g., !(x > y)).
 * The static type of this expression is definite {@link GoBoolType}.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoNot extends NativeCall {
	
	/**
	 * Builds a Go unary not expression. The location where 
	 * this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp	operand
	 */
	public GoNot(CFG cfg, Expression exp) {
		super(cfg, null, -1, -1, "!", GoBoolType.INSTANCE, exp);
	}
}
