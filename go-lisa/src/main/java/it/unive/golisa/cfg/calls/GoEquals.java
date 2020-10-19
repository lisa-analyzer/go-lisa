package it.unive.golisa.cfg.calls;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NativeCall;

/**
 * Go equals native function class.
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoEquals extends NativeCall {
	
	/**
	 * Builds a Go equals expression. 
	 * The location where this Go and expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this Go equals expression belongs to
	 * @param exp1	left-side operand
	 * @param exp2 	right-side operand 
	 */
	public GoEquals(CFG cfg, Expression exp1, Expression exp2) {
		super(cfg, null, -1, -1, "==", exp1, exp2);
	}
	
	/**
	 * Builds a Go equals expression at a given location in the program.
	 * 
	 * @param cfg           the cfg that this equals expression belongs to
	 * @param sourceFile    the source file where this equals expression happens. If
	 *                      unknown, use {@code null}
	 * @param line          the line number where this equals expression happens in the
	 *                      source file. If unknown, use {@code -1}
	 * @param col           the column where this equals expression happens in the source
	 *                      file. If unknown, use {@code -1}
	 * @param exp1		    left-side operand
	 * @param exp2		    right-side operand
	 */
	public GoEquals(CFG cfg, String sourceFile, int line, int col, Expression exp1, Expression exp2) {
		super(cfg, sourceFile, line, col, "==", exp1, exp2);
	}
}
