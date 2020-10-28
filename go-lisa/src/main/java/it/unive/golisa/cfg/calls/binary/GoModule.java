package it.unive.golisa.cfg.calls.binary;

import it.unive.lisa.cfg.CFG;
import it.unive.lisa.cfg.statement.Expression;
import it.unive.lisa.cfg.statement.NativeCall;

/**
 * A Go module function call (e1 % e2).
 * 
 * @author <a href="mailto:vincenzo.arceri@unive.it">Vincenzo Arceri</a>
 */
public class GoModule extends NativeCall {
	
	/**
	 * Builds a Go module expression. 
	 * The location where this expression appears is unknown 
	 * (i.e. no source file/line/column is available).
	 * 
	 * @param cfg	the cfg that this expression belongs to
	 * @param exp1	left-hand side operand
	 * @param exp2 	right-hand side operand 
	 */
	public GoModule(CFG cfg, Expression exp1, Expression exp2) {
		super(cfg, null, -1, -1, "%", exp1, exp2);
	}
	
	/**
	 * Builds a Go module expression at a given location in the program.
	 * 
	 * @param cfg           the cfg that this expression belongs to
	 * @param sourceFile    the source file where this expression happens. If
	 *                      unknown, use {@code null}
	 * @param line          the line number where this expression happens in the
	 *                      source file. If unknown, use {@code -1}
	 * @param col           the column where this expression happens in the source
	 *                      file. If unknown, use {@code -1}
	 * @param exp1		    left-hand side operand
	 * @param exp2		    right-hand side operand
	 */
	public GoModule(CFG cfg, String sourceFile, int line, int col, Expression exp1, Expression exp2) {
		super(cfg, sourceFile, line, col, "%", exp1, exp2);
	}
}
