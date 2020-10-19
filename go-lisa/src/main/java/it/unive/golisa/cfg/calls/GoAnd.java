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
	
	/**
	 * Builds a Go and expression at a given location in the program.
	 * 
	 * @param cfg           the cfg that this and expression belongs to
	 * @param sourceFile    the source file where this and expression happens. If
	 *                      unknown, use {@code null}
	 * @param line          the line number where this and expression happens in the
	 *                      source file. If unknown, use {@code -1}
	 * @param col           the column where this and expression happens in the source
	 *                      file. If unknown, use {@code -1}
	 * @param exp1		    left-side operand
	 * @param exp2		    right-side operand
	 */
	public GoAnd(CFG cfg, String sourceFile, int line, int col, Expression exp1, Expression exp2) {
		super(cfg, sourceFile, line, col, "&&", exp1, exp2);
	}
}
